package com.manusrao.nano.model;

import java.util.List;
import java.util.Map;

public final class AgentConstants {

    public static final short OS_UNKNOWN = 0;
    public static final short OS_WINDOWS = 1;
    public static final short OS_WINDOWS_LEGACY = 2;
    public static final short OS_MACOS = 3;
    public static final short OS_ANDROID = 4;
    public static final short OS_LINUX = 5;
    public static final short OS_IOS = 6;

    public static final short BROWSER_UNKNOWN = 0;
    public static final short BROWSER_EDGE = 1;
    public static final short BROWSER_CHROME = 2;
    public static final short BROWSER_FIREFOX = 3;
    public static final short BROWSER_SAFARI = 4;
    public static final short BROWSER_OPERA = 5;

    public static final Map<Short, String> OS_MAP = Map.of(
            OS_UNKNOWN, "Unknown",
            OS_WINDOWS, "Windows",
            OS_WINDOWS_LEGACY, "Windows (Legacy)",
            OS_MACOS, "macOS",
            OS_ANDROID, "Android",
            OS_LINUX, "Linux",
            OS_IOS, "iOS"
    );

    public static final Map<Short, String> BROWSER_MAP = Map.of(
            BROWSER_UNKNOWN, "Unknown",
            BROWSER_EDGE, "Edge",
            BROWSER_CHROME, "Chrome",
            BROWSER_FIREFOX, "Firefox",
            BROWSER_SAFARI, "Safari",
            BROWSER_OPERA, "Opera"
    );

    public static final Map<String, String> DEVICE_MAP = Map.of(
            "desktop", "Desktop",
            "mobile", "Mobile",
            "tablet", "Tablet",
            "unknown", "Unknown"
    );

    public static final List<DetectionRule<Short>> OS_RULES = List.of(
            new DetectionRule<>("windows nt 10", OS_WINDOWS, List.of()),
            new DetectionRule<>("windows nt 11", OS_WINDOWS, List.of()),
            new DetectionRule<>("windows", OS_WINDOWS_LEGACY, List.of()),
            new DetectionRule<>("mac os x", OS_MACOS, List.of()),
            new DetectionRule<>("android", OS_ANDROID, List.of()),
            new DetectionRule<>("linux", OS_LINUX, List.of("android")),
            new DetectionRule<>("iphone", OS_IOS, List.of()),
            new DetectionRule<>("ipad", OS_IOS, List.of()),
            new DetectionRule<>("ipod", OS_IOS, List.of())
    );

    public static final List<DetectionRule<Short>> BROWSER_RULES = List.of(
            new DetectionRule<>("edg/", BROWSER_EDGE, List.of()),
            new DetectionRule<>("chrome/", BROWSER_CHROME, List.of("edg/")),
            new DetectionRule<>("firefox/", BROWSER_FIREFOX, List.of()),
            new DetectionRule<>("safari/", BROWSER_SAFARI, List.of("chrome/")),
            new DetectionRule<>("opr/", BROWSER_OPERA, List.of()),
            new DetectionRule<>("opera", BROWSER_OPERA, List.of())
    );

    public static final List<DetectionRule<DeviceType>> DEVICE_RULES = List.of(
            new DetectionRule<>("tablet", DeviceType.TABLET, List.of()),
            new DetectionRule<>("ipad", DeviceType.TABLET, List.of()),
            new DetectionRule<>("mobile", DeviceType.MOBILE, List.of("tablet")),
            new DetectionRule<>("windows", DeviceType.DESKTOP, List.of("mobile", "android")),
            new DetectionRule<>("mac os", DeviceType.DESKTOP, List.of("mobile", "android")),
            new DetectionRule<>("linux", DeviceType.DESKTOP, List.of("mobile", "android")),
            new DetectionRule<>("cros", DeviceType.DESKTOP, List.of("mobile", "android"))
    );

    private AgentConstants() {
    }
}
