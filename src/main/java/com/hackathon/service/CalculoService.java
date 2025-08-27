package com.hackathon.service;

import com.hackathon.dto.ParcelaDTO;
import com.hackathon.dto.ResultadoSimulacaoDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoService {

    public ResultadoSimulacaoDTO calcularSAC(BigDecimal valorDesejado, int prazo, BigDecimal taxaJurosMensal) {
        List<ParcelaDTO> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = valorDesejado;
        BigDecimal amortizacao = valorDesejado.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prestacao = amortizacao.add(juros).setScale(2, RoundingMode.HALF_UP);

            ParcelaDTO parcela = new ParcelaDTO();
            parcela.setNumero(i);
            parcela.setValorAmortizacao(amortizacao);
            parcela.setValorJuros(juros);
            parcela.setValorPrestacao(prestacao);
            parcelas.add(parcela);

            saldoDevedor = saldoDevedor.subtract(amortizacao);
        }

        ResultadoSimulacaoDTO resultado = new ResultadoSimulacaoDTO();
        resultado.setTipo("SAC");
        resultado.setParcelas(parcelas);

        return resultado;
    }

    public ResultadoSimulacaoDTO calcularPRICE(BigDecimal valorDesejado, int prazo, BigDecimal taxaJurosMensal) {
        List<ParcelaDTO> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = valorDesejado;
        BigDecimal prestacao = valorDesejado
                .multiply(taxaJurosMensal.add(BigDecimal.ONE).pow(prazo))
                .multiply(taxaJurosMensal)
                .divide(taxaJurosMensal.add(BigDecimal.ONE).pow(prazo).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = prestacao.subtract(juros).setScale(2, RoundingMode.HALF_UP);

            ParcelaDTO parcela = new ParcelaDTO();
            parcela.setNumero(i);
            parcela.setValorAmortizacao(amortizacao);
            parcela.setValorJuros(juros);
            parcela.setValorPrestacao(prestacao);
            parcelas.add(parcela);

            saldoDevedor = saldoDevedor.subtract(amortizacao);
        }

        ResultadoSimulacaoDTO resultado = new ResultadoSimulacaoDTO();
        resultado.setTipo("PRICE");
        resultado.setParcelas(parcelas);

        return resultado;
    }
}