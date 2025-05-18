package com.yohan.event_planner.security;

import com.yohan.event_planner.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Adapter class that implements Spring Security's {@link UserDetails} interface,
 * wrapping the domain {@link User} entity to provide authentication and authorization
 * information to the security framework.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructs a new {@code CustomUserDetails} instance wrapping the provided {@link User}.
     *
     * @param user the domain user entity
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * <p>
     * Currently returns an empty list as no roles or authorities are implemented yet.
     *
     * @return the authorities granted to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: Map user roles to GrantedAuthority objects when roles are added
        return Collections.emptyList();
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the hashed password string
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username string
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account has expired.
     * <p>
     * Always returns {@code true} since account expiration is not currently implemented.
     *
     * @return {@code true} if the user's account is valid (non-expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * <p>
     * Always returns {@code true} since account locking is not currently implemented.
     *
     * @return {@code true} if the user is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * <p>
     * Always returns {@code true} since credential expiration is not currently implemented.
     *
     * @return {@code true} if the user's credentials are valid (non-expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return {@code true} if the user is enabled
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
