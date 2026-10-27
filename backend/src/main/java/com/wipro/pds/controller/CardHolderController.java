package com.wipro.pds.controller;

import com.wipro.pds.entity.CardHolder;
import com.wipro.pds.entity.FamilyMember;
import com.wipro.pds.service.CardHolderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CardHolderController {

    private final CardHolderService cardHolderService;

    public CardHolderController(CardHolderService cardHolderService) {
        this.cardHolderService = cardHolderService;
    }

    @GetMapping
    public ResponseEntity<Page<CardHolder>> getAllCards(
        @RequestParam(required = false) Long shopId,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(cardHolderService.getAllCards(shopId, search, PageRequest.of(page, size, Sort.by("cardId").ascending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardHolder> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardHolderService.getCardById(id));
    }

    @GetMapping("/by-number/{cardNumber}")
    public ResponseEntity<CardHolder> getCardByNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardHolderService.getCardByNumber(cardNumber));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<FamilyMember>> getCardMembers(@PathVariable Long id) {
        return ResponseEntity.ok(cardHolderService.getMembersByCardId(id));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<FamilyMember> addFamilyMember(@PathVariable Long id, @RequestBody FamilyMember member) {
        return ResponseEntity.ok(cardHolderService.addFamilyMember(id, member));
    }

    @GetMapping("/inactive-drawal-report")
    public ResponseEntity<List<CardHolder>> getThreeMonthNonDrawalReport() {
        return ResponseEntity.ok(cardHolderService.getThreeMonthNonDrawalCards());
    }
}
