package com.project.downloadmanager.util;

import java.util.regex.Pattern;

public class UrlValidator {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\s/$.?#].[^\s]*$",
            Pattern.CASE_INSENSITIVE);

    public static boolean isValidUrl(String url) {
        return URL_PATTERN.matcher(url).matches();
    }
}
