package com.hackathon.model;

import jakarta.persistence.Column; // Adicione este import
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@Entity
@Table(name = "simulacoes")
public class Simulacao {

    @Id
    private UUID idSimulacao; // O ID agora é um UUID

    private BigDecimal valorDesejado;
    private int prazo;
    private Integer codigoProduto;
    private String descricaoProduto; // Novo campo

    @Column(name = "PC_TAXA_JUROS", precision = 10, scale = 9)
    private BigDecimal taxaJuros;
    
    private LocalDate dataSimulacao;
    private BigDecimal valorTotalCredito; // Novo campo

    @Column(columnDefinition = "TEXT")
    private String resultadoSimulacaoJson; // Campo para armazenar o JSON de retorno

    // Construtor para gerar o UUID automaticamente
    public Simulacao() {
        this.idSimulacao = UUID.randomUUID();
    }
}