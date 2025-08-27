package com.hackathon.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SimulacaoPorProdutoDTO {
    private Integer codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaMediaJuros;
    private BigDecimal valorMedioPrestacao;
    private BigDecimal valorTotalDesejado;
    private BigDecimal valorTotalCredito;
}