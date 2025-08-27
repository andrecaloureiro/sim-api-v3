package com.hackathon.controller;

import com.hackathon.dto.TelemetryResponseDTO;
import com.hackathon.service.TelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telemetria")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping
    public ResponseEntity<TelemetryResponseDTO> getTelemetry() {
        TelemetryResponseDTO telemetryData = telemetryService.getTelemetryData();
        return ResponseEntity.ok(telemetryData);
    }
}