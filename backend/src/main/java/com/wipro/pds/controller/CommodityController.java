package com.wipro.pds.controller;

import com.wipro.pds.entity.Commodity;
import com.wipro.pds.service.CommodityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commodities")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommodityController {

    private final CommodityService commodityService;

    public CommodityController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @GetMapping
    public ResponseEntity<List<Commodity>> getAllCommodities() {
        return ResponseEntity.ok(commodityService.getAllCommodities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Commodity> getCommodityById(@PathVariable Long id) {
        return ResponseEntity.ok(commodityService.getCommodityById(id));
    }
}
