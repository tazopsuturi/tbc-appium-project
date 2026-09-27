package com.tbc.appium.exceptions;

/**
 * Unchecked exception for framework-level failures (bad configuration, driver start-up problems, etc.),
 * as opposed to assertion failures which indicate a defect in the application under test.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
