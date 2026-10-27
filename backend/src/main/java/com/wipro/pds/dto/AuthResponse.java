package com.wipro.pds.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String fullName;
    private List<String> roles;
    private Long shopId;
    private Long godownId;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String fullName, List<String> roles, Long shopId, Long godownId) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.roles = roles;
        this.shopId = shopId;
        this.godownId = godownId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getGodownId() { return godownId; }
    public void setGodownId(Long godownId) { this.godownId = godownId; }
}
