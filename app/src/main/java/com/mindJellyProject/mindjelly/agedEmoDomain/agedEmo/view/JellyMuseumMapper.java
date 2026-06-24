package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

final class JellyMuseumMapper {
    private JellyMuseumMapper() {
    }

    static String title(Long agedEmoId) {
        return "완성 젤리 #" + agedEmoId;
    }

    static String formatCreatedDate(String createDate) {
        return createDate == null || createDate.trim().isEmpty() ? "-" : createDate;
    }
}
