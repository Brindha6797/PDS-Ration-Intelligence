package com.wipro.pds;

import com.wipro.pds.entity.CardType;
import com.wipro.pds.entity.Commodity;
import com.wipro.pds.entity.EntitlementRule;
import com.wipro.pds.repository.CardHolderRepository;
import com.wipro.pds.repository.CommodityRepository;
import com.wipro.pds.repository.EntitlementRepository;
import com.wipro.pds.repository.EntitlementRuleRepository;
import com.wipro.pds.service.EntitlementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class EntitlementCalculationTest {

    private EntitlementRepository entitlementRepository;
    private EntitlementRuleRepository ruleRepository;
    private CardHolderRepository cardHolderRepository;
    private CommodityRepository commodityRepository;
    private EntitlementService entitlementService;

    @BeforeEach
    void setUp() {
        entitlementRepository = Mockito.mock(EntitlementRepository.class);
        ruleRepository = Mockito.mock(EntitlementRuleRepository.class);
        cardHolderRepository = Mockito.mock(CardHolderRepository.class);
        commodityRepository = Mockito.mock(CommodityRepository.class);

        entitlementService = new EntitlementService(
            entitlementRepository, ruleRepository, cardHolderRepository, commodityRepository
        );
    }

    @Test
    @DisplayName("TC-03: Verify AAY Fixed Entitlement (35kg Rice)")
    void testAayFixedEntitlement() {
        CardType aay = new CardType(1L, "AAY", "Antyodaya Anna Yojana");
        Commodity rice = new Commodity();
        rice.setCommodityId(1L);
        rice.setCommodityCode("RICE_COMMON");

        EntitlementRule rule = new EntitlementRule();
        rule.setCalcMethod("FIXED_PER_CARD");
        rule.setBaseQuantity(new BigDecimal("35.00"));
        rule.setPerMemberQuantity(BigDecimal.ZERO);

        Mockito.when(ruleRepository.findActiveRules(eq(1L), eq(1L), any(LocalDate.class)))
            .thenReturn(List.of(rule));

        BigDecimal result = entitlementService.calculateEntitlementQuantity(aay, 4, rice, LocalDate.now());
        assertEquals(new BigDecimal("35.00"), result, "AAY Rice should be fixed 35kg regardless of family size");
    }

    @Test
    @DisplayName("TC-03B: Verify PHH Per-Member Entitlement with Ceiling Enforcement")
    void testPhhPerMemberWithCeiling() {
        CardType phh = new CardType(2L, "PHH", "Priority Household");
        Commodity rice = new Commodity();
        rice.setCommodityId(1L);

        EntitlementRule rule = new EntitlementRule();
        rule.setCalcMethod("PER_MEMBER_WITH_CEILING");
        rule.setBaseQuantity(BigDecimal.ZERO);
        rule.setPerMemberQuantity(new BigDecimal("5.00"));
        rule.setMaxQuantityCeiling(new BigDecimal("20.00"));

        Mockito.when(ruleRepository.findActiveRules(eq(2L), eq(1L), any(LocalDate.class)))
            .thenReturn(List.of(rule));

        // 3 members -> 15kg
        BigDecimal result3 = entitlementService.calculateEntitlementQuantity(phh, 3, rice, LocalDate.now());
        assertEquals(new BigDecimal("15.00"), result3, "3 members @ 5kg = 15kg");

        // 6 members -> ceiling 20kg (not 30kg)
        BigDecimal result6 = entitlementService.calculateEntitlementQuantity(phh, 6, rice, LocalDate.now());
        assertEquals(new BigDecimal("20.00"), result6, "6 members @ 5kg capped at ceiling of 20kg");
    }

    @Test
    @DisplayName("TC-04: Verify Missing Entitlement Rule Returns Zero Gracefully")
    void testMissingRuleReturnsZero() {
        CardType phh = new CardType(2L, "PHH", "Priority Household");
        Commodity kerosene = new Commodity();
        kerosene.setCommodityId(99L);

        Mockito.when(ruleRepository.findActiveRules(eq(2L), eq(99L), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());

        BigDecimal result = entitlementService.calculateEntitlementQuantity(phh, 3, kerosene, LocalDate.now());
        assertEquals(BigDecimal.ZERO, result, "Missing rule should return 0.00 safely without throwing exception");
    }
}
