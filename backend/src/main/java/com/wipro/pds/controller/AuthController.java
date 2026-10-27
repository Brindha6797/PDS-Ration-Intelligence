package com.wipro.pds.controller;

import com.wipro.pds.dto.AuthRequest;
import com.wipro.pds.dto.AuthResponse;
import com.wipro.pds.security.UserPrincipal;
import com.wipro.pds.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", principal.getUserId());
        map.put("username", principal.getUsername());
        map.put("fullName", principal.getFullName());
        map.put("authorities", principal.getAuthorities());
        map.put("shopId", principal.getAssignedShopId());
        map.put("godownId", principal.getAssignedGodownId());
        return ResponseEntity.ok(map);
    }
}
