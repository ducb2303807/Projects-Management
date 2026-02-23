package com.group4.common.enums;

import lombok.Getter;

@Getter
public enum BusinessErrorCode {
    // Auth
    USER_NOT_FOUND("AUTH_001","User not found"),
    INVALID_PASSWORD("AUTH_002","Invalid password"),
    TOKEN_EXPIRED("AUTH_003","Token expired"),
    ACCOUNT_LOCKED("AUTH_004","Account locked"),
    EMAIL_ALREADY_EXISTS("AUTH_005","Email already exists"),
    USERNAME_ALREADY_EXISTS("AUTH_006","Username already exists"),

    // Project
    PROJECT_NOT_FOUND("PROJ_001","Project not found"),
    PROJECT_ALREADY_EXISTS("PROJ_002","Project already exists"),
    PROJECT_ACCESS_DENIED("PROJ_003","Project Access denied"),

    SYSTEM_ERROR("SYS_001","System error"),
    SYSTEM_RESOURCE_NOT_FOUND("SYS_002","System resource not found"),
    SYSTEM_INTERNAL_SERVER_ERROR("SYS_003","System internal server error"),
    SYSTEM_VALIDATION_ERROR("SYS_004","System Validation error"),
    SYSTEM_ACCESS_DENIED("SYS_005","System Access denied"),
    INVALID_PARAMETER("SYS_006","Invalid parameter")
    ;

    private final String code;
    private final String defaultMessage;

    BusinessErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
