package com.mindJellyProject.mindjelly.jellyDomain.jelly.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for JellyDrawerResDTO — verifies Phase 2 new fields (emo1Name, emo1Icon,
 * emo2Name, emo2Icon, status) and JellyStartAgingReqDTO default status.
 *
 * TDD RED: these tests fail until the fields/classes are added.
 */
public class JellyDrawerResDTOTest {

    @Test
    public void jellyDrawerResDTO_hasEmo1Name() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-14");
        // getEmo1Name() should exist and return null (not yet set via Gson)
        assertNull(dto.getEmo1Name());
    }

    @Test
    public void jellyDrawerResDTO_hasEmo1Icon() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-14");
        assertNull(dto.getEmo1Icon());
    }

    @Test
    public void jellyDrawerResDTO_hasEmo2Name() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-14");
        assertNull(dto.getEmo2Name());
    }

    @Test
    public void jellyDrawerResDTO_hasEmo2Icon() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-14");
        assertNull(dto.getEmo2Icon());
    }

    @Test
    public void jellyDrawerResDTO_hasStatus() {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(1L, 2L, false, "2026-05-14");
        assertNull(dto.getStatus());
    }

    @Test
    public void jellyStartAgingReqDTO_defaultStatusIsAging() {
        JellyStartAgingReqDTO reqDTO = new JellyStartAgingReqDTO();
        assertEquals("AGING", reqDTO.getStatus());
    }
}
