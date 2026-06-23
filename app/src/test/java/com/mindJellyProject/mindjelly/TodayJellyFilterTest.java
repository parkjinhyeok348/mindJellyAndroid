package com.mindJellyProject.mindjelly;

import com.mindJellyProject.mindjelly.common.TodayJellyFilter;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TodayJellyFilterTest {

    private static JellyDrawerResDTO makeJelly(long id, String createDate) {
        return new JellyDrawerResDTO(id, 1L, false, createDate);
    }

    @Test
    public void containsToday_withTodayEntry_returnsTrue() {
        List<JellyDrawerResDTO> jellies = Arrays.asList(
                makeJelly(1L, "2026-06-20"),
                makeJelly(2L, "2026-06-23")
        );
        assertTrue(TodayJellyFilter.containsToday(jellies, "2026-06-23"));
    }

    @Test
    public void containsToday_withoutTodayEntry_returnsFalse() {
        List<JellyDrawerResDTO> jellies = Arrays.asList(
                makeJelly(1L, "2026-06-20"),
                makeJelly(2L, "2026-06-21")
        );
        assertFalse(TodayJellyFilter.containsToday(jellies, "2026-06-23"));
    }

    @Test
    public void containsToday_nullList_returnsFalse() {
        assertFalse(TodayJellyFilter.containsToday(null, "2026-06-23"));
    }

    @Test
    public void containsToday_emptyList_returnsFalse() {
        assertFalse(TodayJellyFilter.containsToday(new ArrayList<>(), "2026-06-23"));
    }

    @Test
    public void containsToday_entryWithNullDate_returnsFalse() {
        List<JellyDrawerResDTO> jellies = Arrays.asList(makeJelly(1L, null));
        assertFalse(TodayJellyFilter.containsToday(jellies, "2026-06-23"));
    }
}
