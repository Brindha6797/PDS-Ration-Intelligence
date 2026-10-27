package com.wipro.pds.service;

import com.wipro.pds.entity.Shop;
import com.wipro.pds.entity.ShopStock;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.ShopRepository;
import com.wipro.pds.repository.ShopStockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopStockRepository shopStockRepository;

    public ShopService(ShopRepository shopRepository, ShopStockRepository shopStockRepository) {
        this.shopRepository = shopRepository;
        this.shopStockRepository = shopStockRepository;
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Shop getShopById(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
    }

    public List<ShopStock> getStockByShopId(Long shopId) {
        return shopStockRepository.findByShop_ShopId(shopId);
    }
}
