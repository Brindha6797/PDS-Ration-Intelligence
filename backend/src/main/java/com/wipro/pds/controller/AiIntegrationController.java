package com.wipro.pds.controller;

import com.wipro.pds.dto.*;
import com.wipro.pds.service.AiGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiIntegrationController {

    private final AiGatewayService aiGatewayService;

    public AiIntegrationController(AiGatewayService aiGatewayService) {
        this.aiGatewayService = aiGatewayService;
    }

    @GetMapping("/anomalies")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<List<AiAnomalyResultDto>> getAnomalies() {
        return ResponseEntity.ok(aiGatewayService.detectStockAnomalies());
    }

    @GetMapping("/forecast")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<List<AiForecastResultDto>> getDemandForecast(
        @RequestParam(required = false) Long shopId,
        @RequestParam(required = false) Long commodityId
    ) {
        return ResponseEntity.ok(aiGatewayService.forecastDemand(shopId, commodityId));
    }

    @PostMapping("/classify-complaint")
    public ResponseEntity<AiComplaintClassificationResultDto> classifyComplaint(
        @RequestBody Map<String, String> body
    ) {
        String text = body.get("text");
        String language = body.get("language");
        return ResponseEntity.ok(aiGatewayService.classifyComplaint(text, language));
    }

    @PostMapping("/officer-assistant")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<AiAssistantResponseDto> askOfficerAssistant(
        @Valid @RequestBody AiAssistantQueryDto queryDto
    ) {
        return ResponseEntity.ok(aiGatewayService.answerOfficerQuery(queryDto.getQuery()));
    }
}
