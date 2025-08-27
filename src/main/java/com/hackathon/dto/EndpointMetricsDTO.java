package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointMetricsDTO {
    private String nomeApi;
    private long qtdRequisicoes;
    private double tempoMedio; // em milissegundos
    private double tempoMinimo; // em milissegundos
    private double tempoMaximo; // em milissegundos
    private double percentualSucesso;
}