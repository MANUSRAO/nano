package com.manusrao.nano.service;

import com.maxmind.db.MaxMindDbConstructor;
import com.maxmind.db.MaxMindDbParameter;
import com.maxmind.db.Reader;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoIpService {

    private final Reader ipv4Reader;
    private final Reader ipv6Reader;

    public GeoIpService(
            @Value("${nano.geoip.ipv4-path}") String ipv4Path,
            @Value("${nano.geoip.ipv6-path}") String ipv6Path) throws IOException {
        this.ipv4Reader = new Reader(new File(ipv4Path), Reader.FileMode.MEMORY_MAPPED);
        this.ipv6Reader = new Reader(new File(ipv6Path), Reader.FileMode.MEMORY_MAPPED);
    }

    public GeoLocation lookup(InetAddress ip) {
        if (ip == null) {
            return new GeoLocation(null, null);
        }
        try {
            Reader reader = ip.getAddress().length == 16 ? ipv6Reader : ipv4Reader;
            DbipRecord record = reader.get(ip, DbipRecord.class);
            if (record == null) {
                return new GeoLocation(null, null);
            }
            return new GeoLocation(record.countryCode, record.city);
        } catch (IOException e) {
            return new GeoLocation(null, null);
        }
    }

    @PreDestroy
    public void close() throws IOException {
        ipv4Reader.close();
        ipv6Reader.close();
    }

    public static class DbipRecord {
        public final String countryCode;
        public final String city;

        @MaxMindDbConstructor
        public DbipRecord(
                @MaxMindDbParameter(name = "country_code") String countryCode,
                @MaxMindDbParameter(name = "city") String city) {
            this.countryCode = countryCode;
            this.city = city;
        }
    }

    public record GeoLocation(String countryCode, String city) {}
}
