package com.manusrao.nano.domains;

import com.manusrao.nano.converter.InetAddressConverter;
import com.manusrao.nano.model.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.time.Instant;

@Entity
@Table(
        name = "click_events",
        indexes = @Index(name = "idx_url_id_clicked_at", columnList = "url_id, clicked_at")
)
@IdClass(ClickEventId.class)
@Data
@NoArgsConstructor
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Id
    @Column(name = "clicked_at", updatable = false)
    private Instant clickedAt = Instant.now();

    @Column(name = "url_id", nullable = false)
    private Long urlId;

    @Convert(converter = InetAddressConverter.class)
    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @Column(name = "os_id")
    private Short osId;

    @Column(name = "browser_id")
    private Short browserId;

    @Convert(converter = DeviceType.Converter.class)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "city", length = 128)
    private String city;
}
