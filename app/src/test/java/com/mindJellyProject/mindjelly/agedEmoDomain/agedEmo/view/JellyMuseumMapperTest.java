package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JellyMuseumMapperTest {
    @Test
    public void mapsCompletedJelliesToDisplayRows() {
        AgedEmoMuseumResDTO dto = new AgedEmoMuseumResDTO(3L, "2026-05-18", 7L);

        List<JellyMuseumMapper.DisplayJelly> rows = JellyMuseumMapper.toDisplayRows(Arrays.asList(dto));

        assertEquals(1, rows.size());
        assertEquals("완성 젤리 ID: 3", rows.get(0).title);
        assertEquals("조합 ID: 7", rows.get(0).combinationText);
        assertEquals("완성일: 2026-05-18", rows.get(0).createdText);
    }

    @Test
    public void mapsNullDatesToDash() {
        AgedEmoMuseumResDTO dto = new AgedEmoMuseumResDTO(3L, null, 7L);

        List<JellyMuseumMapper.DisplayJelly> rows = JellyMuseumMapper.toDisplayRows(Arrays.asList(dto));

        assertEquals("완성일: -", rows.get(0).createdText);
    }
}
