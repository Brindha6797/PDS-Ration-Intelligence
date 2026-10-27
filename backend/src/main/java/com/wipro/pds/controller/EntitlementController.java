package com.wipro.pds.controller;

import com.wipro.pds.dto.AllotmentGenerateRequest;
import com.wipro.pds.dto.RemainingEntitlementDto;
import com.wipro.pds.entity.EntitlementRule;
import com.wipro.pds.service.EntitlementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entitlements")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EntitlementController {

    private final EntitlementService entitlementService;

    public EntitlementController(EntitlementService entitlementService) {
        this.entitlementService = entitlementService;
    }

    @GetMapping("/card/{cardId}/remaining")
    public ResponseEntity<List<RemainingEntitlementDto>> getRemainingEntitlement(
        @PathVariable Long cardId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month
    ) {
        int y = (year != null) ? year : LocalDate.now().getYear();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        return ResponseEntity.ok(entitlementService.getRemainingEntitlements(cardId, y, m));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Map<String, Object>> generateEntitlements(@Valid @RequestBody AllotmentGenerateRequest request) {
        int generatedCount = entitlementService.generateMonthlyEntitlements(request.getYear(), request.getMonth());
        return ResponseEntity.ok(Collections.singletonMap("generatedCount", generatedCount));
    }

    @GetMapping("/rules")
    public ResponseEntity<List<EntitlementRule>> getEntitlementRules() {
        return ResponseEntity.ok(entitlementService.getAllRules());
    }
}
