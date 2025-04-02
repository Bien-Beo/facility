package com.utc2.facility.controller;

import com.utc2.facility.dto.response.NavigationResponse;
import com.utc2.facility.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("result", dashboardService.getDashboard());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/{employeeId}")
    public NavigationResponse getRoomCount(@PathVariable String employeeId) {
        return dashboardService.getRoomCountByEmployeeId(employeeId);
    }
}
