package com.wipro.pds.controller;

import com.wipro.pds.dto.SaleRequest;
import com.wipro.pds.dto.SaleResponse;
import com.wipro.pds.entity.Sale;
import com.wipro.pds.security.UserPrincipal;
import com.wipro.pds.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER', 'SHOPKEEPER')")
    public ResponseEntity<SaleResponse> processSale(
        @Valid @RequestBody SaleRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long operatorId = principal != null ? principal.getUserId() : 1L;
        SaleResponse response = salesService.processSale(request, operatorId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Sale>> getSalesByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(salesService.getSalesByShop(shopId));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Sale>> getSalesByCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(salesService.getSalesByCard(cardId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(salesService.getSaleById(id));
    }
}
