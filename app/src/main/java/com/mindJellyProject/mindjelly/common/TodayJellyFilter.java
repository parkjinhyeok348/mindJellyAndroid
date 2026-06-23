package com.mindJellyProject.mindjelly.common;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import java.util.List;

public final class TodayJellyFilter {

    private TodayJellyFilter() {}

    public static boolean containsToday(List<JellyDrawerResDTO> jellies, String today) {
        if (jellies == null || jellies.isEmpty()) return false;
        return jellies.stream().anyMatch(j -> today.equals(j.getCreateDate()));
    }
}
