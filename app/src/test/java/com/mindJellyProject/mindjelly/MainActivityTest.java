package com.mindJellyProject.mindjelly;

import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Test
    public void pickRandom_returnsItemFromArray() {
        String[] messages = {"msg1", "msg2", "msg3"};
        String result = MainActivity.pickRandom(messages, new Random());
        assertTrue(Arrays.asList(messages).contains(result));
    }

    @Test
    public void pickRandom_emptyArray_returnsEmptyString() {
        String result = MainActivity.pickRandom(new String[]{}, new Random());
        assertEquals("", result);
    }

    @Test
    public void pickRandom_nullArray_returnsEmptyString() {
        String result = MainActivity.pickRandom(null, new Random());
        assertEquals("", result);
    }
}
