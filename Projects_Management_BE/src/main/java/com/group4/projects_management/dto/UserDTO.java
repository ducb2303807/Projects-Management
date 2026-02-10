package com.group4.projects_management.dto;

public class UserDTO {
    private Long userId;
    private Long systemRoleId;
    private String userName;
    private String userUsername;
    private String userPasswordHashed;
    private String userEmail;
    private boolean userIsActive;
    private String userAddress;

    public UserDTO() {}

    public UserDTO(Long userId, Long systemRoleId, String userName, String userUsername,
                   String userPasswordHashed, String userEmail, boolean userIsActive, String userAddress) {
        this.userId = userId;
        this.systemRoleId = systemRoleId;
        this.userName = userName;
        this.userUsername = userUsername;
        this.userPasswordHashed = userPasswordHashed;
        this.userEmail = userEmail;
        this.userIsActive = userIsActive;
        this.userAddress = userAddress;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSystemRoleId() { return systemRoleId; }
    public void setSystemRoleId(Long systemRoleId) { this.systemRoleId = systemRoleId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserUsername() { return userUsername; }
    public void setUserUsername(String userUsername) { this.userUsername = userUsername; }

    public String getUserPasswordHashed() { return userPasswordHashed; }
    public void setUserPasswordHashed(String userPasswordHashed) { this.userPasswordHashed = userPasswordHashed; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public boolean isUserIsActive() { return userIsActive; }
    public void setUserIsActive(boolean userIsActive) { this.userIsActive = userIsActive; }

    public String getUserAddress() { return userAddress; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
}
