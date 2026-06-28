package com.manusrao.nano.service;

import com.manusrao.nano.dao.ClickEventDao;
import com.manusrao.nano.dao.UrlDao;
import com.manusrao.nano.domains.ClickEvent;
import com.manusrao.nano.domains.Url;
import com.manusrao.nano.exception.UrlNotFoundException;
import com.manusrao.nano.util.Base62Encoder;
import com.manusrao.nano.util.IdPermutation;
import com.manusrao.nano.util.UserAgentParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Optional;

@Service
public class ClientService {

    private final UrlDao urlDao;
    private final ClickEventDao clickEventDao;
    private final Base62Encoder base62Encoder;
    private final IdPermutation idPermutation;
    private final UserAgentParser userAgentParser;
    private final GeoIpService geoIpService;

    public ClientService(UrlDao urlDao, ClickEventDao clickEventDao,
                         Base62Encoder base62Encoder, IdPermutation idPermutation,
                         UserAgentParser userAgentParser, GeoIpService geoIpService) {
        this.urlDao = urlDao;
        this.clickEventDao = clickEventDao;
        this.base62Encoder = base62Encoder;
        this.idPermutation = idPermutation;
        this.userAgentParser = userAgentParser;
        this.geoIpService = geoIpService;
    }

    public HttpHeaders resolveAndTrack(String shortCode, HttpServletRequest request) {
        long scrambledId = base62Encoder.decode(shortCode);
        long originalId = idPermutation.unscramble(scrambledId);

        Optional<Url> optUrl = urlDao.findById(originalId);
        if (optUrl.isEmpty()) {
            throw new UrlNotFoundException("No URL found for short code: " + shortCode);
        }

        Url url = optUrl.get();
        recordClick(url.getId(), request);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url.getLongUrl()));
        return headers;
    }

    private void recordClick(Long urlId, HttpServletRequest request) {
        InetAddress ip = extractIp(request);

        ClickEvent event = new ClickEvent();
        event.setUrlId(urlId);
        event.setIpAddress(ip);
        UserAgentParser.ParsedAgent agent = userAgentParser.parse(request.getHeader("User-Agent"));
        event.setOsId(agent.osId());
        event.setBrowserId(agent.browserId());
        event.setDeviceType(agent.deviceType());

        GeoIpService.GeoLocation location = geoIpService.lookup(ip);
        event.setCountryCode(location.countryCode());
        event.setCity(location.city());

        clickEventDao.save(event);
    }

    private InetAddress extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
