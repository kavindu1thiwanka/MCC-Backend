package com.bms.util;

public class BMSCheckedException extends Exception {

    public BMSCheckedException(String message) {
        super(message);
    }

    public BMSCheckedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public BMSCheckedException() {
        throw new RuntimeException("Something went wrong! Please contact your system administrator.");
    }
}
