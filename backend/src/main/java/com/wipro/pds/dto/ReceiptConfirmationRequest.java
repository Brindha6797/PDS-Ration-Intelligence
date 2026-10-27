package com.wipro.pds.dto;

public class ReceiptConfirmationRequest {
    private String remarks;

    public ReceiptConfirmationRequest() {}
    public ReceiptConfirmationRequest(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
