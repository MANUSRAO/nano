package com.manusrao.nano.util;

import com.manusrao.nano.model.AgentConstants;
import com.manusrao.nano.model.DetectionRule;
import com.manusrao.nano.model.DeviceType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAgentParser {

    public ParsedAgent parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new ParsedAgent(AgentConstants.OS_UNKNOWN, AgentConstants.BROWSER_UNKNOWN, DeviceType.UNKNOWN);
        }

        String ua = userAgent.toLowerCase();
        return new ParsedAgent(
                detect(ua, AgentConstants.OS_RULES, AgentConstants.OS_UNKNOWN),
                detect(ua, AgentConstants.BROWSER_RULES, AgentConstants.BROWSER_UNKNOWN),
                detect(ua, AgentConstants.DEVICE_RULES, DeviceType.UNKNOWN)
        );
    }

    private static <T> T detect(String ua, List<DetectionRule<T>> rules, T defaultValue) {
        for (DetectionRule<T> rule : rules) {
            if (ua.contains(rule.keyword())) {
                boolean excluded = false;
                for (String ex : rule.excludes()) {
                    if (ua.contains(ex)) {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded) {
                    return rule.value();
                }
            }
        }
        return defaultValue;
    }

    public record ParsedAgent(short osId, short browserId, DeviceType deviceType) {}
}
