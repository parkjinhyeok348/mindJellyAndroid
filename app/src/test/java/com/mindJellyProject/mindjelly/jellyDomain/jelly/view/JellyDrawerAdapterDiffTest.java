package com.mindJellyProject.mindjelly.jellyDomain.jelly.view;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JellyDrawerAdapterDiffTest {

    @Test
    public void sameItem_returnsFalseWhenLeftDtoIsNull() {
        JellyDrawerResDTO right = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");

        assertFalse(JellyDrawerAdapter.sameItem(null, right));
    }

    @Test
    public void sameItem_returnsFalseWhenRightJellyIdIsNull() {
        JellyDrawerResDTO left = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        JellyDrawerResDTO right = new JellyDrawerResDTO(null, 2L, false, "2026-05-18");

        assertFalse(JellyDrawerAdapter.sameItem(left, right));
    }

    @Test
    public void sameItem_returnsTrueWhenJellyIdsMatch() {
        JellyDrawerResDTO left = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        JellyDrawerResDTO right = new JellyDrawerResDTO(1L, 3L, true, "2026-05-19");

        assertTrue(JellyDrawerAdapter.sameItem(left, right));
    }

    @Test
    public void sameContent_returnsFalseWhenEitherDtoIsNull() {
        JellyDrawerResDTO right = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");

        assertFalse(JellyDrawerAdapter.sameContent(null, right));
    }

    @Test
    public void sameContent_returnsTrueWhenJellyIdStatusAndCreateDateMatch() {
        JellyDrawerResDTO left = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        JellyDrawerResDTO right = new JellyDrawerResDTO(1L, 3L, false, "2026-05-18");

        assertTrue(JellyDrawerAdapter.sameContent(left, right));
    }

    @Test
    public void sameContent_returnsFalseWhenDisplayedEmotionNameChanges() {
        JellyDrawerResDTO left = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        left.setEmo1Name("기쁨");
        JellyDrawerResDTO right = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        right.setEmo1Name("슬픔");

        assertFalse(JellyDrawerAdapter.sameContent(left, right));
    }

    @Test
    public void sameContent_returnsFalseWhenDisplayedEmotionIconChanges() {
        JellyDrawerResDTO left = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        left.setEmo1Icon("/images/happy.png");
        JellyDrawerResDTO right = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        right.setEmo1Icon("/images/sad.png");

        assertFalse(JellyDrawerAdapter.sameContent(left, right));
    }

    @Test
    public void emotionNamesText_joinsAvailableEmotionNames() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        dto.setEmo1Name("기쁨");
        dto.setEmo2Name("평온");

        assertEquals("기쁨 + 평온", JellyDrawerAdapter.emotionNamesText(dto));
    }

    @Test
    public void emotionNamesText_skipsMissingEmotionName() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-18");
        dto.setEmo2Name("평온");

        assertEquals("평온", JellyDrawerAdapter.emotionNamesText(dto));
    }

    @Test
    public void sameContent_handlesNullJellyIdsSafely() {
        JellyDrawerResDTO left = new JellyDrawerResDTO(null, 2L, false, "2026-05-18");
        JellyDrawerResDTO right = new JellyDrawerResDTO(null, 3L, false, "2026-05-18");

        assertTrue(JellyDrawerAdapter.sameContent(left, right));
    }
}
