package com.wipro.pds.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class DispatchRequest {
    @NotNull(message = "Godown ID is required")
    private Long godownId;

    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotNull(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotNull(message = "Driver name is required")
    private String driverName;

    private String driverPhone;
    private String remarks;

    @NotEmpty(message = "Dispatch items cannot be empty")
    private List<DispatchItemRequest> items;

    public DispatchRequest() {}

    public Long getGodownId() { return godownId; }
    public void setGodownId(Long godownId) { this.godownId = godownId; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public List<DispatchItemRequest> getItems() { return items; }
    public void setItems(List<DispatchItemRequest> items) { this.items = items; }
}
