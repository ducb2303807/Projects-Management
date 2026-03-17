package com.group4.projects_management_fe.core.interfaces;

public interface ErrorNotifier {
    void showWarning(String title, String message);
    void showError(String title, String message);
    void showCrashReport(String title, Throwable exception);
    void navigateToLogin();
}
