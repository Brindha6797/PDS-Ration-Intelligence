package com.wipro.pds.dto;

import jakarta.validation.constraints.NotBlank;

public class AiAssistantQueryDto {
    @NotBlank(message = "Question prompt is required")
    private String query;

    public AiAssistantQueryDto() {}
    public AiAssistantQueryDto(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
