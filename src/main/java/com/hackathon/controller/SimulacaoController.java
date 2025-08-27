package com.hackathon.controller;

import com.hackathon.dto.SimulacaoRequestDTO;
import com.hackathon.dto.SimulacaoResponseDTO;
import com.hackathon.dto.AgregadoDiarioDTO;

import com.hackathon.model.Simulacao; // Importe a classe Simulacao

import com.hackathon.service.SimulacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/simulacoes")
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping
    public ResponseEntity<SimulacaoResponseDTO> simular(@RequestBody SimulacaoRequestDTO request) {
        SimulacaoResponseDTO response = simulacaoService.simularEmprestimo(request);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Simulacao>> listarTodasSimulacoes() {
        List<Simulacao> simulacoes = simulacaoService.listarTodas();
        return ResponseEntity.ok(simulacoes);
    }

    @GetMapping("/por-dia")
    public ResponseEntity<AgregadoDiarioDTO> agregarPorDia(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {
        
        AgregadoDiarioDTO resultado = simulacaoService.agregarPorProdutoEData(dataReferencia);
        return ResponseEntity.ok(resultado);
        }
}