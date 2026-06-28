# Nano

## Project Overview
**Nano** is efficient, single instance URL shortener designed for speed & user analytics.

Designed to run efficiently on minimal infra, Nano eliminates traditional database bottlenecks by utilizing mathematical ID permutation and in-memory stateless routing. The analytics captures dual stack IP networks (IPv4/IPv6), device telemetry, and geolocation data. The analytics is bifurcated based on time series partitioning for efficient metric aggregation.

### User Analytics Capabilities
Nano captures and processes the following telemetry on every click:
* **Network Data:** Stores both IPv4 and IPv6 addresses.
* **Geolocation:** Incoming IPs are mapped to country and city level data.
* **Device & Client Fingerprinting:** Extracts OS, Browser, and Device Type data.
---

## Architecture & Design Choices

### 1. Sequential Auto-Increment IDs
**Concept:** Utilizing MySQL's native `AUTO_INCREMENT` primary key for the base URL identifier, rather than using UUIDs, string codes, or distributed timestamp IDs (like Snowflake).

**Why it was done:**
* **Zero Page Splits:** In InnoDB, data is clustered physically by the Primary Key. Inserting sequential integers means MySQL strictly appends data to the end of the disk file, resulting in the absolute maximum write throughput possible.
* **Index Efficiency:** Eliminates the need for "fat" string indexes. Every database lookup during a redirect is a raw integer PK query, which is the fastest read operation in MySQL.

### 2. Mathematical ID Permutation (Bijective Function)
**Concept:** Before converting the database ID into a short URL, the application intercepts the ID and scrambles it using a Multiplicative Inverse algorithm.

*Formula:* `Scrambled = ((ID * Prime) % MaxLimit) ^ XOR_Mask`

**Why it was done:**
* **Prevents DB Scraping:** Sequential IDs are highly predictable (e.g., `url/a`, `url/b`). Permutation completely destroys this sequence. ID `1` might scramble to `1319223168`, and ID `2` might scramble to `2740263300`.
* **Stateless Application Layer:** Because the algorithm is perfectly reversible, the application does not need to store the scrambled integer or the final short string in the database. When a click occurs, the app decrypts the short URL mathematically back to the original database ID and executes a fast read.

### 3. Scrambling of the Base62 Encoder
**Concept:** Standard Base62 encoding uses a predictable 62 character dictionary (`0-9a-zA-Z`). Nano implements a custom encoder utilizing a single, randomly shuffled 62 character string hardcoded into the application.

**Why it was done:**
* **Cryptographic Protection:** If a malicious actor manages to reverse engineer the Prime number and XOR mask used in the permutation math, they still cannot decode the URLs without the secret shuffled dictionary. It acts as a static, memory free cryptographic key that secures the entire routing ecosystem.
---

## Database Schema

The database is heavily normalized for string data (devices/OS) but optimized for high speed writes and time series analytics.

```sql
-- ---------------------------------------------------------
-- TABLE: urls
-- ---------------------------------------------------------
CREATE TABLE urls (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- TABLE: click_events
-- ---------------------------------------------------------
CREATE TABLE click_events (
    id BIGINT UNSIGNED AUTO_INCREMENT,
    url_id BIGINT UNSIGNED NOT NULL,
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Network (Use INET6_ATON() on insert)
    ip_address VARBINARY(16), 
    
    os_id SMALLINT,
    browser_id SMALLINT,
    device_type ENUM('desktop', 'mobile', 'tablet', 'unknown'),
    
    country_code CHAR(2), 
    city VARCHAR(128),
    
    PRIMARY KEY (id, clicked_at),
    INDEX idx_url_id_clicked_at (url_id, clicked_at)
) ENGINE=InnoDB
PARTITION BY RANGE (UNIX_TIMESTAMP(clicked_at)) (
    PARTITION p_init VALUES LESS THAN (UNIX_TIMESTAMP('2026-07-06 00:00:00'))
);