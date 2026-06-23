package com.mindJellyProject.mindjelly;

import com.mindJellyProject.mindjelly.common.WorkManagerScheduler;

import org.junit.Test;
import java.time.LocalTime;
import static org.junit.Assert.*;

public class WorkManagerSchedulerTest {

    @Test
    public void minutesToNextNineAm_before9am_returnsRemainingMinutes() {
        assertEquals(60L, WorkManagerScheduler.minutesToNextNineAm(LocalTime.of(8, 0)));
    }

    @Test
    public void minutesToNextNineAm_after9am_returnsNextDayMinutes() {
        assertEquals(23L * 60, WorkManagerScheduler.minutesToNextNineAm(LocalTime.of(10, 0)));
    }

    @Test
    public void minutesToNextNineAm_exactly9am_returnsFullDay() {
        assertEquals(24L * 60, WorkManagerScheduler.minutesToNextNineAm(LocalTime.of(9, 0)));
    }

    @Test
    public void minutesToNextNineAm_midnight_returnsNineHours() {
        assertEquals(9L * 60, WorkManagerScheduler.minutesToNextNineAm(LocalTime.of(0, 0)));
    }
}
