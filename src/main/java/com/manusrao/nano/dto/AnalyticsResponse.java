package com.manusrao.nano.dto;

import com.manusrao.nano.model.AgentConstants;

import java.util.Map;

public record AnalyticsResponse(
        long totalClicks,
        Map<Short, Long> osCounts,
        Map<Short, Long> browserCounts,
        Map<String, Long> deviceTypeCounts,
        Map<String, Long> countryCounts,
        Map<String, Long> cityCounts,
        Map<Short, String> osMetadata,
        Map<Short, String> browserMetadata,
        Map<String, String> deviceMetadata
) {
    public static AnalyticsResponse of(long totalClicks,
                                       Map<Short, Long> osCounts,
                                       Map<Short, Long> browserCounts,
                                       Map<String, Long> deviceTypeCounts,
                                       Map<String, Long> countryCounts,
                                       Map<String, Long> cityCounts) {
        return new AnalyticsResponse(totalClicks,
                osCounts, browserCounts, deviceTypeCounts,
                countryCounts, cityCounts,
                AgentConstants.OS_MAP,
                AgentConstants.BROWSER_MAP,
                AgentConstants.DEVICE_MAP);
    }
}
