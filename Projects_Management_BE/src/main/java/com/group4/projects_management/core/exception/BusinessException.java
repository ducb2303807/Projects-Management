package com.group4.projects_management.core.exception;

import com.group4.common.enums.BusinessErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
    public BusinessException(String message,BusinessErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
