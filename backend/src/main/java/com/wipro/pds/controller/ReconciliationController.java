package com.wipro.pds.controller;

import com.wipro.pds.dto.ReconciliationRequest;
import com.wipro.pds.entity.StockReconciliation;
import com.wipro.pds.security.UserPrincipal;
import com.wipro.pds.service.ReconciliationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reconciliation")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping
    public ResponseEntity<List<StockReconciliation>> getAllReconciliations(@RequestParam(required = false) Long shopId) {
        if (shopId != null) {
            return ResponseEntity.ok(reconciliationService.getReconciliationsByShop(shopId));
        }
        return ResponseEntity.ok(reconciliationService.getAllReconciliations());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<StockReconciliation> recordReconciliation(
        @Valid @RequestBody ReconciliationRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long officerId = principal != null ? principal.getUserId() : 1L;
        return ResponseEntity.ok(reconciliationService.recordReconciliation(request, officerId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<StockReconciliation> updateStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        String status = body.get("status");
        String remarks = body.get("remarks");
        return ResponseEntity.ok(reconciliationService.updateStatus(id, status, remarks));
    }
}
