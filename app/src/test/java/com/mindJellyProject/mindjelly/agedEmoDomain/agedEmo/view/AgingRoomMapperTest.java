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
    public void filtersOnlyAgingJellies() {
        JellyDrawerResDTO aging = new JellyDrawerResDTO(1L, 10L, true, "2026-05-15");
        aging.setStatus("AGING");
        aging.setEmo1Name("기쁨");
        aging.setEmo2Name("평온");

        JellyDrawerResDTO waiting = new JellyDrawerResDTO(2L, 11L, false, "2026-05-15");
        waiting.setStatus("WAITING");

        List<JellyDrawerResDTO> rows = AgingRoomMapper.toAgingRows(Arrays.asList(aging, waiting));

        assertEquals(1, rows.size());
        assertEquals(Long.valueOf(1L), rows.get(0).getJellyId());
    }

    @Test
    public void formatsEmotionTitleAndDday() {
        JellyDrawerResDTO aging = new JellyDrawerResDTO(1L, 10L, true, "2026-05-15");
        aging.setStatus("AGING");
        aging.setEmo1Name("기쁨");
        aging.setEmo2Name("평온");

        assertEquals("기쁨 + 평온", AgingRoomMapper.emotionTitle(aging));
        assertEquals("D-4", AgingRoomMapper.formatDday("2026-05-15", LocalDate.of(2026, 5, 18)));
        assertEquals("D-Day", AgingRoomMapper.formatDday("2026-05-10", LocalDate.of(2026, 5, 18)));
        assertEquals("D-?", AgingRoomMapper.formatDday(null, LocalDate.of(2026, 5, 18)));
    }

    @Test
    public void formatsCreatedDateNullToDash() {
        assertEquals("2026-05-15", AgingRoomMapper.formatCreatedDate("2026-05-15"));
        assertEquals("-", AgingRoomMapper.formatCreatedDate(null));
    }

    @Test
    public void detectsCompletedJelliesByStatus() {
        JellyDrawerResDTO completed = new JellyDrawerResDTO(1L, 10L, false, "2026-05-10");
        completed.setStatus("MATURED");

        assertTrue(AgingRoomMapper.hasCompletedJellies(Arrays.asList(completed)));
    }
}
