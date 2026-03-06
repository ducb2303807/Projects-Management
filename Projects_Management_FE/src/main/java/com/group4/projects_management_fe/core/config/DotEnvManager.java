package com.group4.projects_management_fe.core.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class DotEnvManager {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./Projects_Management_FE")
            .ignoreIfMissing()
            .load();

    private DotEnvManager() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích, không thể khởi tạo!");
    }

    public static String getEnv(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }
}
