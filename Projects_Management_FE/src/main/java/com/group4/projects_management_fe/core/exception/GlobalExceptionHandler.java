package com.group4.projects_management_fe.core.exception;

import com.group4.projects_management_fe.core.interfaces.ErrorNotifier;
import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class GlobalExceptionHandler {
    private static ErrorNotifier notifier;

    public static void initialize(ErrorNotifier errorNotifier) {
        notifier = errorNotifier;

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> handleException(throwable));

        RxJavaPlugins.setErrorHandler(throwable -> {
            Throwable cause = throwable;
            if (throwable instanceof UndeliverableException) {
                cause = throwable.getCause();
            }
            handleException(cause);
        });
    }

    public static void handleException(Throwable throwable) {
        if (throwable == null) return;

        Throwable rootCause = unwrap(throwable);

        if (rootCause instanceof UnauthorizedException) {
            log.warn("Cảnh báo bảo mật: {}", rootCause.getMessage());
        } else {
            log.error("Global Exception Captured: ", rootCause);
        }

        // LỌC LỖI MẠNG: Không hiện Alert cho các lỗi kết nối SSE bị ngắt
        if (isNetworkNoise(rootCause)) {
            log.warn("Network noise captured (no alert shown): {}", rootCause.getMessage());
            return;
        }

        if (notifier != null) {
            if (rootCause instanceof UnauthorizedException) {
                notifier.showWarning("Hết phiên làm việc", "Vui lòng đăng nhập lại để tiếp tục.");
                notifier.navigateToLogin();
            } else if (rootCause instanceof ApiException) {
                notifier.showError("Thông báo", rootCause.getMessage());
            } else {
                // Các lỗi kỹ thuật không mong muốn (NullPointer, Sql, v.v.)
                notifier.showCrashReport("Hệ thống gặp sự cố bất ngờ", rootCause);
            }
        }
    }

    private static Throwable unwrap(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && (
                cause instanceof RuntimeException ||
                        cause instanceof InvocationTargetException)) {
            cause = cause.getCause();
        }
        return cause;
    }

    private static boolean isNetworkNoise(Throwable t) {
        return t instanceof java.io.EOFException ||
                t instanceof java.net.SocketException ||
                t instanceof java.net.SocketTimeoutException ||
                (t.getMessage() != null && t.getMessage().contains("Canceled"));
    }
}
