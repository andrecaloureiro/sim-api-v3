package com.hackathon.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ResultadoSimulacaoDTO {

    private String tipo;
    private List<ParcelaDTO> parcelas;

}