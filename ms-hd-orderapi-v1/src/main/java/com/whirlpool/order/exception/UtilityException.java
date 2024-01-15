package com.whirlpool.order.exception;

public class UtilityException extends BaseException {
    public UtilityException(String message) {
        super(message);
    }

    public UtilityException(Throwable cause) {
        super(cause);
    }

    public UtilityException(String message, Throwable cause) {
        super(message, cause);
    }
}
