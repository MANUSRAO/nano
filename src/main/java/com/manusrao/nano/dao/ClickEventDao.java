package com.manusrao.nano.dao;

import com.manusrao.nano.domains.ClickEvent;
import com.manusrao.nano.repository.ClickEventRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClickEventDao {

    private final ClickEventRepository repository;
    private final JdbcTemplate jdbc;

    public ClickEventDao(ClickEventRepository repository, JdbcTemplate jdbc) {
        this.repository = repository;
        this.jdbc = jdbc;
    }

    public ClickEvent save(ClickEvent event) {
        return repository.save(event);
    }

    public long countByUrlIdAndDateRange(Long urlId, Instant from, Instant to) {
        return repository.countByUrlIdAndClickedAtBetween(urlId, from, to);
    }

    public Map<Short, Long> aggregateOsCounts(Long urlId, Instant from, Instant to) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT os_id, COUNT(*) AS cnt FROM click_events "
                        + "WHERE url_id = ? AND clicked_at >= ? AND clicked_at < ? "
                        + "GROUP BY os_id",
                urlId, from, to);
        return toShortCountMap(rows, "os_id");
    }

    public Map<Short, Long> aggregateBrowserCounts(Long urlId, Instant from, Instant to) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT browser_id, COUNT(*) AS cnt FROM click_events "
                        + "WHERE url_id = ? AND clicked_at >= ? AND clicked_at < ? "
                        + "GROUP BY browser_id",
                urlId, from, to);
        return toShortCountMap(rows, "browser_id");
    }

    public Map<String, Long> aggregateDeviceTypeCounts(Long urlId, Instant from, Instant to) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT device_type, COUNT(*) AS cnt FROM click_events "
                        + "WHERE url_id = ? AND clicked_at >= ? AND clicked_at < ? "
                        + "GROUP BY device_type",
                urlId, from, to);
        return toStringCountMap(rows, "device_type");
    }

    public Map<String, Long> aggregateCountryCounts(Long urlId, Instant from, Instant to) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT country_code, COUNT(*) AS cnt FROM click_events "
                        + "WHERE url_id = ? AND clicked_at >= ? AND clicked_at < ? "
                        + "GROUP BY country_code",
                urlId, from, to);
        return toStringCountMap(rows, "country_code");
    }

    public Map<String, Long> aggregateCityCounts(Long urlId, Instant from, Instant to) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT city, COUNT(*) AS cnt FROM click_events "
                        + "WHERE url_id = ? AND clicked_at >= ? AND clicked_at < ? "
                        + "GROUP BY city",
                urlId, from, to);
        return toStringCountMap(rows, "city");
    }

    private Map<Short, Long> toShortCountMap(List<Map<String, Object>> rows, String keyCol) {
        Map<Short, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object key = row.get(keyCol);
            Short k = key != null ? ((Number) key).shortValue() : 0;
            Long v = ((Number) row.get("cnt")).longValue();
            result.put(k, v);
        }
        return result;
    }

    private Map<String, Long> toStringCountMap(List<Map<String, Object>> rows, String keyCol) {
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object key = row.get(keyCol);
            String k = key != null ? key.toString() : "";
            Long v = ((Number) row.get("cnt")).longValue();
            result.put(k, v);
        }
        return result;
    }
}
