package com.pharmacy.pattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implements the <b>Singleton Pattern</b> as a global logging mechanism.
 *
 * <p>
 * <b>Problem solved:</b> The application needs a consistent, globally
 * accessible
 * logging utility. Creating multiple logger instances would lead to scattered,
 * uncoordinated log output. Furthermore, logger initialization can be
 * expensive.
 * </p>
 *
 * <p>
 * <b>Solution:</b> The Singleton pattern guarantees exactly one
 * {@code AppLogger}
 * instance exists throughout the JVM lifetime. All classes share the same
 * logger
 * instance via {@link #getInstance()}.
 * </p>
 *
 * <p>
 * <b>Thread safety:</b> Uses double-checked locking with a {@code volatile}
 * field to ensure correctness in multi-threaded environments (e.g. Spring's
 * concurrent HTTP request handling).
 * </p>
 *
 * <p>
 * <b>Usage example:</b>
 * 
 * <pre>
 * AppLogger.getInstance().log("Drug saved: " + drug.getName());
 * AppLogger.getInstance().logError("Drug not found: " + barcode);
 * </pre>
 * </p>
 *
 * @see com.pharmacy.service.DrugService
 */
public class AppLogger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * volatile ensures the partially-constructed instance is never visible to other
     * threads.
     */
    private static volatile AppLogger instance;

    /**
     * Private constructor — prevents direct instantiation from outside this class.
     */
    private AppLogger() {
        // Singleton: no external instantiation allowed
    }

    /**
     * Returns the single global {@link AppLogger} instance.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the singleton AppLogger instance
     */
    public static AppLogger getInstance() {
        if (instance == null) {
            synchronized (AppLogger.class) {
                if (instance == null) { // Second check inside synchronized block
                    instance = new AppLogger();
                }
            }
        }
        return instance;
    }

    /**
     * Logs an informational message to the console.
     *
     * @param message the message to log
     */
    public void log(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("[PHARMACY LOG]   " + timestamp + " — " + message);
    }

    /**
     * Logs an error message to the console.
     *
     * @param message the error message to log
     */
    public void logError(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.err.println("[PHARMACY ERROR] " + timestamp + " — " + message);
    }

    /**
     * Logs a warning message to the console.
     *
     * @param message the warning message to log
     */
    public void logWarning(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("[PHARMACY WARN]  " + timestamp + " — " + message);
    }
}
