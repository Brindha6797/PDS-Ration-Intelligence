package com.wipro.pds.dto;

import jakarta.validation.constraints.NotBlank;

public class ComplaintRequest {
    private Long cardId;
    private Long shopId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    private String language = "en";
    private String priority = "MEDIUM";

    public ComplaintRequest() {}

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
