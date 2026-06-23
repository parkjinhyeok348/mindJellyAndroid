package com.mindJellyProject.mindjelly.common;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class AgingFilter {
    public static final int AGING_DAYS = 7;

    private AgingFilter() {}

    public static List<JellyDrawerResDTO> filterCompleted(List<JellyDrawerResDTO> jellies, LocalDate today) {
        List<JellyDrawerResDTO> result = new ArrayList<>();
        if (jellies == null) return result;
        for (JellyDrawerResDTO jelly : jellies) {
            if (isCompleted(jelly, today)) result.add(jelly);
        }
        return result;
    }

    public static boolean isCompleted(JellyDrawerResDTO jelly, LocalDate today) {
        if (jelly == null) return false;
        if (!"AGING".equalsIgnoreCase(jelly.getStatus())) return false;
        try {
            LocalDate completionDate = LocalDate.parse(jelly.getCreateDate()).plusDays(AGING_DAYS);
            return !completionDate.isAfter(today);
        } catch (Exception e) {
            return false;
        }
    }
}
