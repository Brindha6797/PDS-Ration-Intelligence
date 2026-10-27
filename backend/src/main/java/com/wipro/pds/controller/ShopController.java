package com.wipro.pds.controller;

import com.wipro.pds.entity.Shop;
import com.wipro.pds.entity.ShopStock;
import com.wipro.pds.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shops")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<List<ShopStock>> getShopStock(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getStockByShopId(id));
    }
}
