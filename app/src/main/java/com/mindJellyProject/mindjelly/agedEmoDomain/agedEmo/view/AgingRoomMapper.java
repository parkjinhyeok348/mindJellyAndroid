package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

final class AgingRoomMapper {
    private static final int AGING_DAYS = 7;

    private AgingRoomMapper() {
    }

    static List<DisplayJelly> toAgingRows(List<JellyDrawerResDTO> jellies, LocalDate today) {
        List<DisplayJelly> rows = new ArrayList<>();
        if (jellies == null) {
            return rows;
        }

        for (JellyDrawerResDTO jelly : jellies) {
            if (!isAging(jelly)) {
                continue;
            }
            rows.add(new DisplayJelly(
                    "젤리 ID: " + jelly.getJellyId(),
                    "상태: 숙성중",
                    "생성일: " + nullToDash(jelly.getCreateDate()),
                    "감정: " + nullToDash(jelly.getEmo1Name()) + " / " + nullToDash(jelly.getEmo2Name()),
                    formatDday(jelly.getCreateDate(), today)
            ));
        }
        return rows;
    }

    static boolean hasCompletedJellies(List<JellyDrawerResDTO> jellies) {
        if (jellies == null) {
            return false;
        }

        for (JellyDrawerResDTO jelly : jellies) {
            String status = jelly.getStatus();
            if ("MATURED".equalsIgnoreCase(status)
                    || "COMPLETE".equalsIgnoreCase(status)
                    || "COMPLETED".equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAging(JellyDrawerResDTO jelly) {
        String status = jelly.getStatus();
        return "AGING".equalsIgnoreCase(status)
                || (status == null && Boolean.TRUE.equals(jelly.getAging()));
    }

    private static String formatDday(String createDate, LocalDate today) {
        try {
            LocalDate completedDate = LocalDate.parse(createDate).plusDays(AGING_DAYS);
            long remainingDays = ChronoUnit.DAYS.between(today, completedDate);
            return remainingDays <= 0 ? "D-Day" : "D-" + remainingDays;
        } catch (RuntimeException ignored) {
            return "D-?";
        }
    }

    private static String nullToDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    static final class DisplayJelly {
        final String title;
        final String statusText;
        final String createDateText;
        final String emotionText;
        final String dDayText;

        DisplayJelly(String title, String statusText, String createDateText, String emotionText, String dDayText) {
            this.title = title;
            this.statusText = statusText;
            this.createDateText = createDateText;
            this.emotionText = emotionText;
            this.dDayText = dDayText;
        }
    }
}
