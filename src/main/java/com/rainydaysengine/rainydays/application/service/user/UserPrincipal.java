package com.rainydaysengine.rainydays.application.service.user;

import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final UsersEntity user;

    public UserPrincipal(UsersEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmailAddress();
    }

    // Returns username
    public String getIdentity() {
        return user.getUsername();
    }

    // TODO: Implement
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // TODO: Implement
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // TODO: Implement
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
