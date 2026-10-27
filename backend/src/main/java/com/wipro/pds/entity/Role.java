package com.wipro.pds.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_roles")
    @SequenceGenerator(name = "seq_roles", sequenceName = "seq_roles", allocationSize = 1)
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME", unique = true, nullable = false, length = 50)
    private String roleName;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    public Role() {}

    public Role(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
