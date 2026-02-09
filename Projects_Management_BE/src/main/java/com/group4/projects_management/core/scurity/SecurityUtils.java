package com.group4.projects_management.core.scurity;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails userPrincipal) {
            return userPrincipal.getUserId();
        }
        return null;
    }
}
