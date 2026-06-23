package com.mindJellyProject.mindjelly;

import com.mindJellyProject.mindjelly.common.AgingFilter;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyDrawerResDTO;

import org.junit.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class AgingFilterTest {

    private static JellyDrawerResDTO makeJelly(long id, String status, String createDate) {
        JellyDrawerResDTO dto = new JellyDrawerResDTO(id, 1L, true, createDate);
        dto.setStatus(status);
        return dto;
    }

    @Test
    public void filterCompleted_returnsJelliesAgingAndPast7Days() {
        LocalDate today = LocalDate.of(2026, 6, 22);
        JellyDrawerResDTO past8  = makeJelly(1L, "AGING", "2026-06-14"); // 8일 전
        JellyDrawerResDTO exact7 = makeJelly(2L, "AGING", "2026-06-15"); // 정확히 7일 전
        JellyDrawerResDTO only2  = makeJelly(3L, "AGING", "2026-06-20"); // 2일 전, 미완
        JellyDrawerResDTO notAging = makeJelly(4L, "SAVED", "2026-06-14"); // AGING 아님

        List<JellyDrawerResDTO> result = AgingFilter.filterCompleted(
                Arrays.asList(past8, exact7, only2, notAging), today);

        assertEquals(2, result.size());
        assertEquals(1L, (long) result.get(0).getJellyId());
        assertEquals(2L, (long) result.get(1).getJellyId());
    }

    @Test
    public void filterCompleted_nullList_returnsEmpty() {
        assertTrue(AgingFilter.filterCompleted(null, LocalDate.now()).isEmpty());
    }

    @Test
    public void filterCompleted_emptyList_returnsEmpty() {
        assertTrue(AgingFilter.filterCompleted(new ArrayList<>(), LocalDate.now()).isEmpty());
    }

    @Test
    public void isCompleted_nullJelly_returnsFalse() {
        assertFalse(AgingFilter.isCompleted(null, LocalDate.now()));
    }

    @Test
    public void isCompleted_invalidDate_returnsFalse() {
        assertFalse(AgingFilter.isCompleted(makeJelly(1L, "AGING", "not-a-date"), LocalDate.now()));
    }
}
