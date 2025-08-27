package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime; // Importe a classe LocalDateTime


@Data
public class SimulacaoResponseDTO {

    private String idSimulacao; // Ex: 20180702
    private Integer codigoProduto; // Ex: 1
    private String descricaoProduto; // Ex: "Produto 1"
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal taxaJuros; // Ex: 0.0179
    private List<ResultadoSimulacaoDTO> resultadoSimulacao;
    private LocalDateTime timestamp; // Novo campo para a data e hora
}