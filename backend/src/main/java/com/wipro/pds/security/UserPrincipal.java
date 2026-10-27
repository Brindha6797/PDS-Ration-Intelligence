package com.wipro.pds.security;

import com.wipro.pds.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String fullName;
    private final Long assignedShopId;
    private final Long assignedGodownId;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.fullName = user.getFullName();
        this.assignedShopId = user.getAssignedShop() != null ? user.getAssignedShop().getShopId() : null;
        this.assignedGodownId = user.getAssignedGodown() != null ? user.getAssignedGodown().getGodownId() : null;
        this.active = "Y".equalsIgnoreCase(user.getIsActive());
        this.authorities = user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
            .collect(Collectors.toList());
    }

    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public Long getAssignedShopId() { return assignedShopId; }
    public Long getAssignedGodownId() { return assignedGodownId; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return active; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return active; }
}
