package com.wipro.pds.controller;

import com.wipro.pds.dto.ComplaintRequest;
import com.wipro.pds.dto.ComplaintResolveRequest;
import com.wipro.pds.entity.Complaint;
import com.wipro.pds.entity.ComplaintHistory;
import com.wipro.pds.security.UserPrincipal;
import com.wipro.pds.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/complaints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public ResponseEntity<Page<Complaint>> getAllComplaints(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(complaintService.getAllComplaints(PageRequest.of(page, size)));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Complaint>> getComplaintsByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(complaintService.getComplaintsByShop(shopId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ComplaintHistory>> getComplaintHistory(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintHistory(id));
    }

    @PostMapping
    public ResponseEntity<Complaint> registerComplaint(
        @Valid @RequestBody ComplaintRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        String registeredBy = principal != null ? principal.getUsername() : "CITIZEN_PORTAL";
        return ResponseEntity.ok(complaintService.registerComplaint(request, registeredBy));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Complaint> assignOfficer(
        @PathVariable Long id,
        @RequestBody Map<String, Long> body,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long officerId = body.get("officerId");
        String username = principal != null ? principal.getUsername() : "OFFICER";
        return ResponseEntity.ok(complaintService.assignOfficer(id, officerId, username));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Complaint> resolveComplaint(
        @PathVariable Long id,
        @Valid @RequestBody ComplaintResolveRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        String username = principal != null ? principal.getUsername() : "OFFICER";
        return ResponseEntity.ok(complaintService.resolveComplaint(id, request, username));
    }
}
