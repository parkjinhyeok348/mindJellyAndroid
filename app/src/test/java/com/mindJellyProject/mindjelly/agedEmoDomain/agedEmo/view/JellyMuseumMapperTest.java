package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JellyMuseumMapperTest {
    @Test
    public void formatsTitleFromAgedEmoId() {
        assertEquals("완성 젤리 #3", JellyMuseumMapper.title(3L));
    }

    @Test
    public void formatsCreatedDateNullToDash() {
        assertEquals("2026-05-18", JellyMuseumMapper.formatCreatedDate("2026-05-18"));
        assertEquals("-", JellyMuseumMapper.formatCreatedDate(null));
        assertEquals("-", JellyMuseumMapper.formatCreatedDate(""));
    }
}
