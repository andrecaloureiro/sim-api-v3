package com.hackathon.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SimulacaoRequestDTO {

    private BigDecimal valorDesejado;
    private int prazo;
}