package com.wipro.pds;

import com.wipro.pds.dto.SaleItemRequest;
import com.wipro.pds.dto.SaleRequest;
import com.wipro.pds.dto.SaleResponse;
import com.wipro.pds.entity.*;
import com.wipro.pds.exception.EntitlementExhaustedException;
import com.wipro.pds.exception.InsufficientStockException;
import com.wipro.pds.repository.*;
import com.wipro.pds.service.SalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class SalesSecurityAndValidationTest {

    private SaleRepository saleRepository;
    private ShopRepository shopRepository;
    private CardHolderRepository cardHolderRepository;
    private CommodityRepository commodityRepository;
    private ShopStockRepository shopStockRepository;
    private EntitlementRepository entitlementRepository;
    private StockMovementRepository stockMovementRepository;
    private AuditLogRepository auditLogRepository;
    private SalesService salesService;

    private Shop shop;
    private CardHolder card;
    private Commodity rice;
    private ShopStock stock;
    private Entitlement entitlement;

    @BeforeEach
    void setUp() {
        saleRepository = Mockito.mock(SaleRepository.class);
        shopRepository = Mockito.mock(ShopRepository.class);
        cardHolderRepository = Mockito.mock(CardHolderRepository.class);
        commodityRepository = Mockito.mock(CommodityRepository.class);
        shopStockRepository = Mockito.mock(ShopStockRepository.class);
        entitlementRepository = Mockito.mock(EntitlementRepository.class);
        stockMovementRepository = Mockito.mock(StockMovementRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);

        salesService = new SalesService(
            saleRepository, shopRepository, cardHolderRepository, commodityRepository,
            shopStockRepository, entitlementRepository, stockMovementRepository, auditLogRepository
        );

        shop = new Shop();
        shop.setShopId(1L);
        shop.setShopCode("FPS-TLK-001");
        shop.setShopStatus("ACTIVE");

        card = new CardHolder();
        card.setCardId(101L);
        card.setCardNumber("RC-TN-04-100234");
        card.setCardStatus("ACTIVE");
        card.setAssignedShop(shop);

        rice = new Commodity();
        rice.setCommodityId(1L);
        rice.setCommodityName("Common Rice");
        rice.setConsumerRatePerUnit(BigDecimal.ZERO);
        rice.setGovernmentSubsidyPerUnit(new BigDecimal("35.00"));
        rice.setMeasurementUnit("KG");

        stock = new ShopStock();
        stock.setStockId(10L);
        stock.setShop(shop);
        stock.setCommodity(rice);
        stock.setCurrentQuantity(new BigDecimal("100.00"));
        stock.setTotalDistributedMonth(BigDecimal.ZERO);

        entitlement = new Entitlement();
        entitlement.setEntitlementId(50L);
        entitlement.setCardHolder(card);
        entitlement.setCommodity(rice);
        entitlement.setEntitledQuantity(new BigDecimal("20.00"));
        entitlement.setRemainingQuantity(new BigDecimal("15.00"));
        entitlement.setDistributedQuantity(new BigDecimal("5.00"));
        entitlement.setStatus("AVAILABLE");

        Mockito.when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        Mockito.when(cardHolderRepository.findById(101L)).thenReturn(Optional.of(card));
        Mockito.when(commodityRepository.findById(1L)).thenReturn(Optional.of(rice));
        Mockito.when(shopStockRepository.findForUpdate(1L, 1L)).thenReturn(Optional.of(stock));
        Mockito.when(entitlementRepository.findByCardHolder_CardIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
            eq(101L), eq(1L), any(Integer.class), any(Integer.class)
        )).thenReturn(Optional.of(entitlement));
        Mockito.when(saleRepository.save(any(Sale.class))).thenAnswer(i -> {
            Sale s = i.getArgument(0);
            s.setSaleId(9001L);
            return s;
        });
    }

    @Test
    @DisplayName("TC-12: Overselling Beyond Permitted Entitlement Quota Throws Exception")
    void testOversellingBeyondEntitlementThrowsException() {
        SaleRequest req = new SaleRequest();
        req.setShopId(1L);
        req.setCardId(101L);
        // Permitted remaining is 15.00kg; requesting 20.00kg must be rejected
        req.setItems(List.of(new SaleItemRequest(1L, new BigDecimal("20.00"))));

        assertThrows(EntitlementExhaustedException.class, () -> salesService.processSale(req, 4L),
            "Must raise EntitlementExhaustedException when requested quantity exceeds remaining entitlement");
    }

    @Test
    @DisplayName("TC-13: Selling Beyond Available Shop Stock Throws Exception")
    void testSellingBeyondShopStockThrowsException() {
        // Drop available shop inventory down to 5.00kg
        stock.setCurrentQuantity(new BigDecimal("5.00"));

        SaleRequest req = new SaleRequest();
        req.setShopId(1L);
        req.setCardId(101L);
        // Card has 15kg entitlement, but shop only has 5kg in stock; requesting 10kg must be rejected
        req.setItems(List.of(new SaleItemRequest(1L, new BigDecimal("10.00"))));

        assertThrows(InsufficientStockException.class, () -> salesService.processSale(req, 4L),
            "Must raise InsufficientStockException when requested quantity exceeds available shop stock");
    }

    @Test
    @DisplayName("TC-11: Valid Distribution Deducts Entitlement & Inventory Correctly")
    void testValidSaleProcessesCleanly() {
        SaleRequest req = new SaleRequest();
        req.setShopId(1L);
        req.setCardId(101L);
        req.setItems(List.of(new SaleItemRequest(1L, new BigDecimal("10.00"))));

        SaleResponse resp = salesService.processSale(req, 4L);

        assertNotNull(resp);
        assertEquals(9001L, resp.getSaleId());
        assertTrue(resp.getBillNumber().startsWith("BILL-FPS-TLK-001"));
        assertEquals(new BigDecimal("5.00"), entitlement.getRemainingQuantity(), "Remaining entitlement should reduce to 5kg");
        assertEquals(new BigDecimal("90.00"), stock.getCurrentQuantity(), "Shop stock should reduce to 90kg");
        Mockito.verify(stockMovementRepository, Mockito.times(1)).save(any(StockMovement.class));
    }
}
