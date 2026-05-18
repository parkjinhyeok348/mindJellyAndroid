package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgingRoomMapperTest {
    @Test
    public void mapsOnlyAgingJelliesWithRemainingDays() {
        JellyDrawerResDTO aging = new JellyDrawerResDTO(1L, 10L, true, "2026-05-15");
        aging.setStatus("AGING");
        aging.setEmo1Name("기쁨");
        aging.setEmo2Name("평온");

        JellyDrawerResDTO waiting = new JellyDrawerResDTO(2L, 11L, false, "2026-05-15");
        waiting.setStatus("WAITING");

        List<AgingRoomMapper.DisplayJelly> rows = AgingRoomMapper.toAgingRows(
                Arrays.asList(aging, waiting),
                LocalDate.of(2026, 5, 18)
        );

        assertEquals(1, rows.size());
        assertEquals("젤리 ID: 1", rows.get(0).title);
        assertEquals("감정: 기쁨 / 평온", rows.get(0).emotionText);
        assertEquals("D-4", rows.get(0).dDayText);
    }

    @Test
    public void detectsCompletedJelliesByStatus() {
        JellyDrawerResDTO completed = new JellyDrawerResDTO(1L, 10L, false, "2026-05-10");
        completed.setStatus("MATURED");

        assertTrue(AgingRoomMapper.hasCompletedJellies(Arrays.asList(completed)));
    }
}
