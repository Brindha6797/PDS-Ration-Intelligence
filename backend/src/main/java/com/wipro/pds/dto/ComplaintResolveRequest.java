package com.wipro.pds.dto;

import jakarta.validation.constraints.NotBlank;

public class ComplaintResolveRequest {
    @NotBlank(message = "Resolution notes are required")
    private String resolutionNotes;

    private String status = "RESOLVED";

    public ComplaintResolveRequest() {}
    public ComplaintResolveRequest(String resolutionNotes, String status) {
        this.resolutionNotes = resolutionNotes;
        this.status = status;
    }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
