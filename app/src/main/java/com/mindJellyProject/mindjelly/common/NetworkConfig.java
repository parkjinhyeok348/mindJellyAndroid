package com.mindJellyProject.mindjelly.common;

import com.mindJellyProject.mindjelly.BuildConfig;

public final class NetworkConfig {
    private NetworkConfig() {
    }

    public static String apiBaseUrl() {
        return ensureTrailingSlash(BuildConfig.API_BASE_URL);
    }

    public static String assetUrl(String path) {
        if (path == null || path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        return trimTrailingSlash(apiBaseUrl()) + "/" + trimLeadingSlash(path);
    }

    private static String ensureTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    private static String trimTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static String trimLeadingSlash(String value) {
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        return value;
    }
}
