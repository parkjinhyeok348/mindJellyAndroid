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

    /** 숙성중(AGING) 젤리만 남긴다. */
    static List<JellyDrawerResDTO> toAgingRows(List<JellyDrawerResDTO> jellies) {
        List<JellyDrawerResDTO> rows = new ArrayList<>();
        if (jellies == null) {
            return rows;
        }
        for (JellyDrawerResDTO jelly : jellies) {
            if (isAging(jelly)) {
                rows.add(jelly);
            }
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

    /** "기쁨 + 평온" 형태의 카드 제목. */
    static String emotionTitle(JellyDrawerResDTO jelly) {
        return nullToDash(jelly.getEmo1Name()) + " + " + nullToDash(jelly.getEmo2Name());
    }

    static String formatCreatedDate(String createDate) {
        return nullToDash(createDate);
    }

    /** 생성일 + 7일 기준 남은 일수. "D-4" / "D-Day" / 파싱불가 시 "D-?". */
    static String formatDday(String createDate, LocalDate today) {
        try {
            LocalDate completedDate = LocalDate.parse(createDate).plusDays(AGING_DAYS);
            long remainingDays = ChronoUnit.DAYS.between(today, completedDate);
            return remainingDays <= 0 ? "D-Day" : "D-" + remainingDays;
        } catch (RuntimeException ignored) {
            return "D-?";
        }
    }

    private static boolean isAging(JellyDrawerResDTO jelly) {
        String status = jelly.getStatus();
        return "AGING".equalsIgnoreCase(status)
                || (status == null && Boolean.TRUE.equals(jelly.getAging()));
    }

    private static String nullToDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }
}
