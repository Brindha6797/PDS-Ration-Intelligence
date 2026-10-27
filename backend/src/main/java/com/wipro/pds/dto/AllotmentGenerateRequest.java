package com.wipro.pds.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AllotmentGenerateRequest {
    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    public AllotmentGenerateRequest() {}
    public AllotmentGenerateRequest(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
}
