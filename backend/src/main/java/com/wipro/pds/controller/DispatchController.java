package com.wipro.pds.controller;

import com.wipro.pds.dto.DispatchRequest;
import com.wipro.pds.dto.ReceiptConfirmationRequest;
import com.wipro.pds.entity.Dispatch;
import com.wipro.pds.security.UserPrincipal;
import com.wipro.pds.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispatches")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Dispatch>> getDispatchesByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(dispatchService.getDispatchesByShop(shopId));
    }

    @GetMapping("/godown/{godownId}")
    public ResponseEntity<List<Dispatch>> getDispatchesByGodown(@PathVariable Long godownId) {
        return ResponseEntity.ok(dispatchService.getDispatchesByGodown(godownId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispatch> getDispatchById(@PathVariable Long id) {
        return ResponseEntity.ok(dispatchService.getDispatchById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'GODOWN_MGR')")
    public ResponseEntity<Dispatch> createDispatch(
        @Valid @RequestBody DispatchRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        String username = principal != null ? principal.getUsername() : "SYSTEM";
        return ResponseEntity.ok(dispatchService.createDispatch(request, username));
    }

    @PostMapping("/{id}/confirm-receipt")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'SHOPKEEPER')")
    public ResponseEntity<Dispatch> confirmReceipt(
        @PathVariable Long id,
        @RequestBody(required = false) ReceiptConfirmationRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        String username = principal != null ? principal.getUsername() : "SHOPKEEPER";
        String remarks = request != null ? request.getRemarks() : null;
        return ResponseEntity.ok(dispatchService.confirmReceipt(id, username, remarks));
    }
}
