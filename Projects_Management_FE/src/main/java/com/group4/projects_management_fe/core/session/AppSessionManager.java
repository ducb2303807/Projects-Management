package com.group4.projects_management_fe.core.session;

import com.group4.common.dto.UserDTO;

public class AppSessionManager implements AuthSessionProvider {
    private static AppSessionManager instance;

    private String token;
    private UserDTO currentUser;

    private AppSessionManager() {
    }

    public static synchronized AppSessionManager getInstance() {
        if (instance == null) {
            instance = new AppSessionManager();
        }
        return instance;
    }

    public void createSession(String token, UserDTO user) {
        this.token = token;
        this.currentUser = user;
    }

    public void destroySession() {
        this.token = null;
        this.currentUser = null;
    }

    @Override
    public String getValidToken() {
        return token;
    }

    public UserDTO getCurrentUser() {
        return this.currentUser;
    }

    public boolean isLoggedIn() {
        return this.token != null && !this.token.isEmpty();
    }
}
