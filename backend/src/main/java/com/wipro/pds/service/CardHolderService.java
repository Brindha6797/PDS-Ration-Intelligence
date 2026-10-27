package com.wipro.pds.service;

import com.wipro.pds.entity.CardHolder;
import com.wipro.pds.entity.CardType;
import com.wipro.pds.entity.FamilyMember;
import com.wipro.pds.entity.Shop;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.CardHolderRepository;
import com.wipro.pds.repository.CardTypeRepository;
import com.wipro.pds.repository.FamilyMemberRepository;
import com.wipro.pds.repository.ShopRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class CardHolderService {

    private final CardHolderRepository cardHolderRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final CardTypeRepository cardTypeRepository;
    private final ShopRepository shopRepository;

    public CardHolderService(CardHolderRepository cardHolderRepository,
                             FamilyMemberRepository familyMemberRepository,
                             CardTypeRepository cardTypeRepository,
                             ShopRepository shopRepository) {
        this.cardHolderRepository = cardHolderRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.shopRepository = shopRepository;
    }

    public Page<CardHolder> getAllCards(Long shopId, String search, Pageable pageable) {
        return cardHolderRepository.searchCards(shopId, search, pageable);
    }

    public CardHolder getCardById(Long cardId) {
        return cardHolderRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Ration Card not found with ID: " + cardId));
    }

    public CardHolder getCardByNumber(String cardNumber) {
        return cardHolderRepository.findByCardNumber(cardNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Ration Card not found with number: " + cardNumber));
    }

    public List<FamilyMember> getMembersByCardId(Long cardId) {
        return familyMemberRepository.findByCardHolder_CardId(cardId);
    }

    @Transactional
    public CardHolder saveCard(CardHolder cardHolder, Long cardTypeId, Long shopId) {
        CardType cardType = cardTypeRepository.findById(cardTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Card Type not found with ID: " + cardTypeId));
        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));

        cardHolder.setCardType(cardType);
        cardHolder.setAssignedShop(shop);
        return cardHolderRepository.save(cardHolder);
    }

    @Transactional
    public FamilyMember addFamilyMember(Long cardId, FamilyMember member) {
        CardHolder cardHolder = getCardById(cardId);
        member.setCardHolder(cardHolder);
        FamilyMember saved = familyMemberRepository.save(member);

        // Update total family member count
        List<FamilyMember> members = familyMemberRepository.findByCardHolder_CardId(cardId);
        cardHolder.setTotalFamilyMembers(Math.max(1, members.size()));
        cardHolderRepository.save(cardHolder);

        return saved;
    }

    public List<CardHolder> getThreeMonthNonDrawalCards() {
        // Strict boundary: Exclude current incomplete month, check past 3 completed calendar months
        YearMonth current = YearMonth.now();
        YearMonth startMonth = current.minusMonths(3);
        LocalDateTime startWindow = startMonth.atDay(1).atStartOfDay();
        LocalDateTime endWindow = current.atDay(1).atStartOfDay().minusNanos(1);

        return cardHolderRepository.findCardsWithNoDrawalsInWindow(startWindow, endWindow);
    }
}
