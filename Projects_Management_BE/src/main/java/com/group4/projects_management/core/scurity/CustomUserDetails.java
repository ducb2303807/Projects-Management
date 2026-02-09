package com.group4.projects_management.core.scurity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.group4.projects_management.entity;
import java.util.Collection;
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(String username) {
        this.username = username;
    }

    public Long getUserId() {return user.getUserId();}

    @Override public String getUsername() { return user.getUserUsername(); }
    @Override public String getPassword() { return user.getUserPasswordHashed(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.getUserIsActive(); }

}
