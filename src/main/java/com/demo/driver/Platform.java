package com.demo.driver;

public enum Platform {
    IOS,
    ANDROID;

    public static Platform from(String value) {
        if (value == null) return IOS;
        return switch (value.trim().toLowerCase()) {
            case "android" -> ANDROID;
            default -> IOS;
        };
    }
}
