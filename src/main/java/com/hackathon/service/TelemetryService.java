package com.hackathon.service;

import com.hackathon.dto.EndpointMetricsDTO;
import com.hackathon.dto.TelemetryResponseDTO;
//import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TelemetryService {

    private final MeterRegistry meterRegistry;

    public TelemetryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public TelemetryResponseDTO getTelemetryData() {
        List<EndpointMetricsDTO> endpointMetricsList = new ArrayList<>();

        // Coleta métricas separadamente para cada método e URI
        collectEndpointMetrics("/simulacoes", "POST", "Simulacao (POST)", endpointMetricsList);
        collectEndpointMetrics("/simulacoes", "GET", "Simulacao (GET)", endpointMetricsList);
        collectEndpointMetrics("/simulacoes/por-dia", "GET", "SimulacaoPorDia (GET)", endpointMetricsList);
        collectEndpointMetrics("/telemetria", "GET", "Telemetria (GET)", endpointMetricsList);

        return new TelemetryResponseDTO(LocalDate.now(), endpointMetricsList);
    }

    private void collectEndpointMetrics(String uri, String httpMethod, String apiName, List<EndpointMetricsDTO> metricsList) {
        // Busca os timers aplicando filtros por URI, método HTTP e resultado
        Timer successTimer = meterRegistry.find("http.server.requests").tag("uri", uri).tag("method", httpMethod).tag("outcome", "SUCCESS").timer();
        Timer clientErrorTimer = meterRegistry.find("http.server.requests").tag("uri", uri).tag("method", httpMethod).tag("outcome", "CLIENT_ERROR").timer();
        Timer serverErrorTimer = meterRegistry.find("http.server.requests").tag("uri", uri).tag("method", httpMethod).tag("outcome", "SERVER_ERROR").timer();

        long successCount = (successTimer != null) ? successTimer.count() : 0;
        long clientErrorCount = (clientErrorTimer != null) ? clientErrorTimer.count() : 0;
        long serverErrorCount = (serverErrorTimer != null) ? serverErrorTimer.count() : 0;
        
        long totalRequests = successCount + clientErrorCount + serverErrorCount;

        // Só adiciona na lista se houver alguma requisição para este endpoint
        if (totalRequests > 0) {
            double totalTimeSuccess = successTimer != null ? successTimer.totalTime(TimeUnit.MILLISECONDS) : 0;
            double totalTimeClientError = clientErrorTimer != null ? clientErrorTimer.totalTime(TimeUnit.MILLISECONDS) : 0;
            double totalTimeServerError = serverErrorTimer != null ? serverErrorTimer.totalTime(TimeUnit.MILLISECONDS) : 0;
            double totalTimeAll = totalTimeSuccess + totalTimeClientError + totalTimeServerError;

            double maxTime = 0;
            if (successTimer != null) maxTime = Math.max(maxTime, successTimer.max(TimeUnit.MILLISECONDS));
            if (clientErrorTimer != null) maxTime = Math.max(maxTime, clientErrorTimer.max(TimeUnit.MILLISECONDS));
            if (serverErrorTimer != null) maxTime = Math.max(maxTime, serverErrorTimer.max(TimeUnit.MILLISECONDS));

            // Nota: O Micrometer não expõe o tempo MÍNIMO por padrão para evitar
            // sobrecarga de métricas. Em um cenário real, isso exigiria configuração
            // de SLOs (Service Level Objectives). Para o desafio, vamos usar 0 como placeholder.
            double minTime = 0.0;
            
            double averageTime = totalTimeAll / totalRequests;
            double successPercentage = (double) successCount / totalRequests;

            metricsList.add(new EndpointMetricsDTO(
                apiName,
                totalRequests,
                averageTime,
                minTime,
                maxTime,
                successPercentage
            ));
        }
    }
}