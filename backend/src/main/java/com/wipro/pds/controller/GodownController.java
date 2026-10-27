package com.wipro.pds.controller;

import com.wipro.pds.entity.Godown;
import com.wipro.pds.entity.GodownStock;
import com.wipro.pds.repository.GodownRepository;
import com.wipro.pds.repository.GodownStockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/godowns")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GodownController {

    private final GodownRepository godownRepository;
    private final GodownStockRepository godownStockRepository;

    public GodownController(GodownRepository godownRepository, GodownStockRepository godownStockRepository) {
        this.godownRepository = godownRepository;
        this.godownStockRepository = godownStockRepository;
    }

    @GetMapping
    public ResponseEntity<List<Godown>> getAllGodowns() {
        return ResponseEntity.ok(godownRepository.findAll());
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<List<GodownStock>> getGodownStock(@PathVariable Long id) {
        return ResponseEntity.ok(godownStockRepository.findByGodown_GodownId(id));
    }
}
