package com.manusrao.nano.service;

import com.manusrao.nano.dao.ClickEventDao;
import com.manusrao.nano.dao.UrlDao;
import com.manusrao.nano.domains.Url;
import com.manusrao.nano.dto.AnalyticsResponse;
import com.manusrao.nano.exception.UrlNotFoundException;
import com.manusrao.nano.util.Base62Encoder;
import com.manusrao.nano.util.IdPermutation;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class DashboardService {

    private final UrlDao urlDao;
    private final ClickEventDao clickEventDao;
    private final Base62Encoder base62Encoder;
    private final IdPermutation idPermutation;

    public DashboardService(UrlDao urlDao, ClickEventDao clickEventDao,
                            Base62Encoder base62Encoder, IdPermutation idPermutation) {
        this.urlDao = urlDao;
        this.clickEventDao = clickEventDao;
        this.base62Encoder = base62Encoder;
        this.idPermutation = idPermutation;
    }

    public String shorten(String longUrl) {
        Url url = urlDao.save(new Url(longUrl));
        long scrambledId = idPermutation.scramble(url.getId());
        return base62Encoder.encode(scrambledId);
    }

    public AnalyticsResponse getAnalytics(String shortCode, Instant from, Instant to) {
        long scrambledId = base62Encoder.decode(shortCode);
        long originalId = idPermutation.unscramble(scrambledId);

        Optional<Url> optUrl = urlDao.findById(originalId);
        if (optUrl.isEmpty()) {
            throw new UrlNotFoundException("No URL found for short code: " + shortCode);
        }

        long totalClicks = clickEventDao.countByUrlIdAndDateRange(originalId, from, to);

        return AnalyticsResponse.of(
                totalClicks,
                clickEventDao.aggregateOsCounts(originalId, from, to),
                clickEventDao.aggregateBrowserCounts(originalId, from, to),
                clickEventDao.aggregateDeviceTypeCounts(originalId, from, to),
                clickEventDao.aggregateCountryCounts(originalId, from, to),
                clickEventDao.aggregateCityCounts(originalId, from, to));
    }
}
