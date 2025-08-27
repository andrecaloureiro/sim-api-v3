package com.hackathon.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AgregadoDiarioDTO {
    private LocalDate dataReferencia;
    private List<SimulacaoPorProdutoDTO> simulacoes;
}