package com.wipro.pds.controller;

import com.wipro.pds.dto.DashboardStatsDto;
import com.wipro.pds.entity.AuditLog;
import com.wipro.pds.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    @GetMapping("/monthly-matrix")
    public ResponseEntity<List<Map<String, Object>>> getMonthlySalesMatrix(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month
    ) {
        int y = (year != null) ? year : LocalDate.now().getYear();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        return ResponseEntity.ok(reportService.getMonthlySalesMatrix(y, m));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(reportService.getRecentAuditLogs());
    }
}
