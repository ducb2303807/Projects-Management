package com.group4.projects_management_fe.core.exception;

import com.group4.common.dto.ErrorResponse;

public class ApiException extends RuntimeException {
    private final ErrorResponse errorResponse;
    public ApiException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
    public ApiException(String defaultMessage) {
        super(defaultMessage);
        this.errorResponse = null;
    }
    public ApiException(String defaultMessage, Throwable defaultThrowable) {
        super(defaultMessage,defaultThrowable);
        this.errorResponse = null;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public int getStatus() {
        return errorResponse != null ? errorResponse.getStatus() : 0;
    }
}
