package com.hackathon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    private Integer coProduto;

    private String noProduto;
    
    @Column(name = "PC_TAXA_JUROS", precision = 10, scale = 9)
    private BigDecimal pcTaxaJuros;

    private Integer nuMinimoMeses;

    private Integer nuMaximoMeses;

    private BigDecimal vrMinimo;

    private BigDecimal vrMaximo;
    
}