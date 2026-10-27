package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_users")
    @SequenceGenerator(name = "seq_users", sequenceName = "seq_users", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "FULL_NAME", nullable = false, length = 150)
    private String fullName;

    @Column(name = "EMAIL", unique = true, length = 100)
    private String email;

    @Column(name = "PHONE", length = 15)
    private String phone;

    @Column(name = "TALUK_NAME", length = 100)
    private String talukName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ASSIGNED_SHOP_ID")
    private Shop assignedShop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ASSIGNED_GODOWN_ID")
    private Godown assignedGodown;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "LAST_LOGIN_AT")
    private LocalDateTime lastLoginAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLES",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getTalukName() { return talukName; }
    public void setTalukName(String talukName) { this.talukName = talukName; }
    public Shop getAssignedShop() { return assignedShop; }
    public void setAssignedShop(Shop assignedShop) { this.assignedShop = assignedShop; }
    public Godown getAssignedGodown() { return assignedGodown; }
    public void setAssignedGodown(Godown assignedGodown) { this.assignedGodown = assignedGodown; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}
