package com.wipro.pds.controller;

import com.wipro.pds.dto.AllotmentGenerateRequest;
import com.wipro.pds.entity.Allotment;
import com.wipro.pds.security.UserPrincipal;
import com.wipro.pds.service.AllotmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/allotments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AllotmentController {

    private final AllotmentService allotmentService;

    public AllotmentController(AllotmentService allotmentService) {
        this.allotmentService = allotmentService;
    }

    @GetMapping
    public ResponseEntity<List<Allotment>> getAllotments(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Long shopId
    ) {
        int y = (year != null) ? year : LocalDate.now().getYear();
        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        return ResponseEntity.ok(allotmentService.getAllotments(y, m, shopId));
    }

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Map<String, Object>> calculateAllotments(@Valid @RequestBody AllotmentGenerateRequest request) {
        int count = allotmentService.calculateMonthlyAllotments(request.getYear(), request.getMonth());
        return ResponseEntity.ok(Collections.singletonMap("calculatedCount", count));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Allotment> approveAllotment(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        Long officerId = principal != null ? principal.getUserId() : 1L;
        return ResponseEntity.ok(allotmentService.approveAllotment(id, officerId));
    }
}
