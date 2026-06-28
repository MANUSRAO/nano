package com.manusrao.nano.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class PartitionScheduler {

    private final JdbcTemplate jdbc;
    private final ZoneId zoneId;

    public PartitionScheduler(JdbcTemplate jdbc,
                              @Value("${nano.partition.timezone:Asia/Kolkata}") String timezone) {
        this.jdbc = jdbc;
        this.zoneId = ZoneId.of(timezone);
    }

    @Scheduled(cron = "${nano.partition.cron:0 0 0 * * SUN}")
    public void addWeeklyPartition() {
        String maxBoundary = jdbc.queryForObject(
                "SELECT MAX(PARTITION_DESCRIPTION) FROM INFORMATION_SCHEMA.PARTITIONS "
                        + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'click_events' "
                        + "AND PARTITION_DESCRIPTION <> 'MAXVALUE'",
                String.class);

        if (maxBoundary == null) {
            return;
        }

        LocalDate lastBoundaryDate = Instant.ofEpochSecond(Long.parseLong(maxBoundary))
                .atZone(zoneId)
                .toLocalDate();
        LocalDate nextDate = lastBoundaryDate.plusWeeks(1);
        String partitionName = "p_" + nextDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String boundary = nextDate + " 00:00:00";

        String sql = "ALTER TABLE click_events ADD PARTITION ("
                + "PARTITION " + partitionName
                + " VALUES LESS THAN (UNIX_TIMESTAMP('" + boundary + "')))";

        jdbc.execute(sql);
    }
}
