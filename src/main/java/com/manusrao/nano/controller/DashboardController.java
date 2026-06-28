package com.manusrao.nano.controller;

import com.manusrao.nano.dto.AnalyticsResponse;
import com.manusrao.nano.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestParam String url) {
        return ResponseEntity.ok(dashboardService.shorten(url));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponse> analytics(
            @RequestParam String code,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(dashboardService.getAnalytics(code, from, to));
    }
}
