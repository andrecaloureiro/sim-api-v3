package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDTO {
    private LocalDate dataReferencia;
    private List<EndpointMetricsDTO> listaEndpoints;
}