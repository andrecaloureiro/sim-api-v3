package com.hackathon.service;

import com.hackathon.model.Produto;
import com.hackathon.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;

    public DataLoader(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (produtoRepository.count() == 0) {
            System.out.println("Inserindo dados iniciais na tabela de produtos...");

            Produto p1 = new Produto();
            p1.setCoProduto(1);
            p1.setNoProduto("Produto 1");
            p1.setPcTaxaJuros(new BigDecimal("0.017900000"));
            p1.setNuMinimoMeses(0);
            p1.setNuMaximoMeses(24);
            p1.setVrMinimo(new BigDecimal("200.00"));
            p1.setVrMaximo(new BigDecimal("10000.00"));

            Produto p2 = new Produto();
            p2.setCoProduto(2);
            p2.setNoProduto("Produto 2");
            p2.setPcTaxaJuros(new BigDecimal("0.017500000"));
            p2.setNuMinimoMeses(25);
            p2.setNuMaximoMeses(48);
            p2.setVrMinimo(new BigDecimal("10001.00"));
            p2.setVrMaximo(new BigDecimal("100000.00"));

            Produto p3 = new Produto();
            p3.setCoProduto(3);
            p3.setNoProduto("Produto 3");
            p3.setPcTaxaJuros(new BigDecimal("0.018200000"));
            p3.setNuMinimoMeses(49);
            p3.setNuMaximoMeses(96);
            p3.setVrMinimo(new BigDecimal("100000.01"));
            p3.setVrMaximo(new BigDecimal("1000000.00"));

            // Note que o VR_MAXIMO e NU_MAXIMO_MESES são nulos no documento, use 'null'
            Produto p4 = new Produto();
            p4.setCoProduto(4);
            p4.setNoProduto("Produto 4");
            p4.setPcTaxaJuros(new BigDecimal("0.015100000"));
            p4.setNuMinimoMeses(96);
            p4.setNuMaximoMeses(null);
            p4.setVrMinimo(new BigDecimal("1000000.01"));
            p4.setVrMaximo(null);

            produtoRepository.save(p1);
            produtoRepository.save(p2);
            produtoRepository.save(p3);
            produtoRepository.save(p4);
        }
    }
}