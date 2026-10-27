package com.wipro.pds.service;

import com.wipro.pds.entity.Commodity;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.CommodityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommodityService {

    private final CommodityRepository commodityRepository;

    public CommodityService(CommodityRepository commodityRepository) {
        this.commodityRepository = commodityRepository;
    }

    public List<Commodity> getAllCommodities() {
        return commodityRepository.findAll();
    }

    public Commodity getCommodityById(Long commodityId) {
        return commodityRepository.findById(commodityId)
            .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with ID: " + commodityId));
    }
}
