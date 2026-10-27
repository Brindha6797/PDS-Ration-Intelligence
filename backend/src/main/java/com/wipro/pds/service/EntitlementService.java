package com.wipro.pds.service;

import com.wipro.pds.dto.RemainingEntitlementDto;
import com.wipro.pds.entity.*;
import com.wipro.pds.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EntitlementService {

    private final EntitlementRepository entitlementRepository;
    private final EntitlementRuleRepository ruleRepository;
    private final CardHolderRepository cardHolderRepository;
    private final CommodityRepository commodityRepository;

    public EntitlementService(EntitlementRepository entitlementRepository,
                              EntitlementRuleRepository ruleRepository,
                              CardHolderRepository cardHolderRepository,
                              CommodityRepository commodityRepository) {
        this.entitlementRepository = entitlementRepository;
        this.ruleRepository = ruleRepository;
        this.cardHolderRepository = cardHolderRepository;
        this.commodityRepository = commodityRepository;
    }

    public List<RemainingEntitlementDto> getRemainingEntitlements(Long cardId, Integer year, Integer month) {
        List<Entitlement> list = entitlementRepository.findByCardHolder_CardIdAndAllotmentYearAndAllotmentMonth(cardId, year, month);
        List<RemainingEntitlementDto> dtos = new ArrayList<>();

        for (Entitlement e : list) {
            RemainingEntitlementDto dto = new RemainingEntitlementDto();
            dto.setCommodityId(e.getCommodity().getCommodityId());
            dto.setCommodityCode(e.getCommodity().getCommodityCode());
            dto.setCommodityName(e.getCommodity().getCommodityName());
            dto.setMeasurementUnit(e.getCommodity().getMeasurementUnit());
            dto.setEntitledQuantity(e.getEntitledQuantity());
            dto.setDistributedQuantity(e.getDistributedQuantity());
            dto.setRemainingQuantity(e.getRemainingQuantity());
            dto.setConsumerRatePerUnit(e.getCommodity().getConsumerRatePerUnit());
            dto.setStatus(e.getStatus());
            dtos.add(dto);
        }
        return dtos;
    }

    public BigDecimal calculateEntitlementQuantity(CardType cardType, Integer familySize, Commodity commodity, LocalDate asOfDate) {
        List<EntitlementRule> rules = ruleRepository.findActiveRules(cardType.getCardTypeId(), commodity.getCommodityId(), asOfDate);
        if (rules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        EntitlementRule rule = rules.get(0);
        BigDecimal qty = BigDecimal.ZERO;

        switch (rule.getCalcMethod()) {
            case "FIXED_PER_CARD":
                qty = rule.getBaseQuantity();
                break;
            case "PER_MEMBER":
                qty = rule.getBaseQuantity().add(rule.getPerMemberQuantity().multiply(BigDecimal.valueOf(familySize)));
                break;
            case "PER_MEMBER_WITH_CEILING":
                qty = rule.getBaseQuantity().add(rule.getPerMemberQuantity().multiply(BigDecimal.valueOf(familySize)));
                if (rule.getMaxQuantityCeiling() != null && qty.compareTo(rule.getMaxQuantityCeiling()) > 0) {
                    qty = rule.getMaxQuantityCeiling();
                }
                break;
            case "SLAB_BASED":
                if (familySize <= 1) {
                    qty = rule.getBaseQuantity();
                } else {
                    qty = rule.getBaseQuantity().add(rule.getPerMemberQuantity().multiply(BigDecimal.valueOf(familySize - 1)));
                }
                if (rule.getMaxQuantityCeiling() != null && qty.compareTo(rule.getMaxQuantityCeiling()) > 0) {
                    qty = rule.getMaxQuantityCeiling();
                }
                break;
            default:
                qty = rule.getBaseQuantity();
        }

        return qty.max(BigDecimal.ZERO);
    }

    @Transactional
    public int generateMonthlyEntitlements(int year, int month) {
        List<CardHolder> activeCards = cardHolderRepository.findAll().stream()
            .filter(c -> "ACTIVE".equalsIgnoreCase(c.getCardStatus()))
            .toList();
        List<Commodity> activeCommodities = commodityRepository.findByIsActive("Y");
        LocalDate asOfDate = LocalDate.of(year, month, 1);

        int count = 0;
        for (CardHolder card : activeCards) {
            for (Commodity comm : activeCommodities) {
                Optional<Entitlement> existing = entitlementRepository
                    .findByCardHolder_CardIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
                        card.getCardId(), comm.getCommodityId(), year, month);

                if (existing.isEmpty()) {
                    BigDecimal qty = calculateEntitlementQuantity(card.getCardType(), card.getTotalFamilyMembers(), comm, asOfDate);
                    if (qty.compareTo(BigDecimal.ZERO) > 0) {
                        Entitlement e = new Entitlement();
                        e.setCardHolder(card);
                        e.setCommodity(comm);
                        e.setAllotmentYear(year);
                        e.setAllotmentMonth(month);
                        e.setEntitledQuantity(qty);
                        e.setDistributedQuantity(BigDecimal.ZERO);
                        e.setRemainingQuantity(qty);
                        e.setStatus("AVAILABLE");
                        e.setCreatedAt(LocalDateTime.now());
                        e.setUpdatedAt(LocalDateTime.now());
                        entitlementRepository.save(e);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public List<EntitlementRule> getAllRules() {
        return ruleRepository.findAll();
    }
}
