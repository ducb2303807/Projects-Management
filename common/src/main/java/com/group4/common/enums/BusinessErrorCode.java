package com.group4.common.enums;

import lombok.Getter;

@Getter
public enum BusinessErrorCode {
    // Auth
    AUTH_USER_NOT_FOUND("AUTH_001","User not found"),
    AUTH_INVALID_PASSWORD("AUTH_002","Invalid password"),
    AUTH_INVALID_TOKEN("TOKEN_003","Token invalid"),
    AUTH_TOKEN_EXPIRED("AUTH_004","Token expired"),
    AUTH_ACCOUNT_LOCKED("AUTH_005","Account locked"),
    AUTH_EMAIL_ALREADY_EXISTS("AUTH_006","Email already exists"),
    AUTH_USERNAME_ALREADY_EXISTS("AUTH_007","Username already exists"),
    AUTH_REQUIRED("AUTH_008","Authentication required"),

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
