package com.mindJellyProject.mindjelly.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepositoryErrorTest {
    @Test
    public void messageUsesThrowableMessageWhenPresent() {
        RuntimeException error = new RuntimeException("boom");

        assertEquals("boom", RepositoryError.message(error));
    }

    @Test
    public void messageFallsBackToClassNameWhenThrowableMessageIsNull() {
        RuntimeException error = new RuntimeException();

        assertEquals("RuntimeException", RepositoryError.message(error));
    }

    @Test
    public void prefixedMessageDoesNotAppendLiteralNull() {
        RuntimeException error = new RuntimeException();

        assertEquals("Error: RuntimeException", RepositoryError.prefixedMessage(error));
    }
}
