package com.mindJellyProject.mindjelly.common;

public final class RepositoryError {
    private RepositoryError() {
    }

    public static String message(Throwable t) {
        if (t == null) {
            return "UnknownError";
        }

        String message = t.getMessage();
        return message != null && !message.trim().isEmpty() ? message : t.getClass().getSimpleName();
    }

    public static String prefixedMessage(Throwable t) {
        return "Error: " + message(t);
    }
}
