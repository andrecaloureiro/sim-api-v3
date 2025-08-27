package com.hackathon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hackathon.dto.AgregadoDiarioDTO;
import com.hackathon.dto.ResultadoSimulacaoDTO;
import com.hackathon.dto.SimulacaoPorProdutoDTO;
import com.hackathon.dto.SimulacaoRequestDTO;
import com.hackathon.dto.SimulacaoResponseDTO;
import com.hackathon.model.Produto;
import com.hackathon.model.Simulacao;
import com.hackathon.repository.ProdutoRepository;
import com.hackathon.repository.SimulacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final CalculoService calculoService;
    private final ObjectMapper objectMapper;

    public SimulacaoService(ProdutoRepository produtoRepository, SimulacaoRepository simulacaoRepository, CalculoService calculoService) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
        this.calculoService = calculoService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public SimulacaoResponseDTO simularEmprestimo(SimulacaoRequestDTO request) {
        List<Produto> produtos = produtoRepository.findAll();
        Optional<Produto> produtoEncontrado = produtos.stream()
                .filter(p -> {
                    boolean atendeValor = request.getValorDesejado().compareTo(p.getVrMinimo()) >= 0
                            && (p.getVrMaximo() == null || request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0);
                    
                    boolean atendePrazo = request.getPrazo() >= p.getNuMinimoMeses()
                            && (p.getNuMaximoMeses() == null || request.getPrazo() <= p.getNuMaximoMeses());
                    
                    return atendeValor && atendePrazo;
                })
                .findFirst();

        if (produtoEncontrado.isPresent()) {
            Produto produto = produtoEncontrado.get();
            BigDecimal taxaJurosMensal = produto.getPcTaxaJuros();
            
            List<ResultadoSimulacaoDTO> resultados = new ArrayList<>();
            resultados.add(calculoService.calcularSAC(request.getValorDesejado(), request.getPrazo(), taxaJurosMensal));
            resultados.add(calculoService.calcularPRICE(request.getValorDesejado(), request.getPrazo(), taxaJurosMensal));

            BigDecimal valorTotalCredito = BigDecimal.ZERO;
            for (ResultadoSimulacaoDTO resultado : resultados) {
                if ("PRICE".equals(resultado.getTipo())) {
                    BigDecimal valorPrestacao = resultado.getParcelas().get(0).getValorPrestacao();
                    valorTotalCredito = valorPrestacao.multiply(BigDecimal.valueOf(request.getPrazo()));
                    break;
                }
            }

            Simulacao novaSimulacao = new Simulacao();
            
            SimulacaoResponseDTO response = new SimulacaoResponseDTO();
            response.setIdSimulacao(novaSimulacao.getIdSimulacao().toString());
            response.setCodigoProduto(produto.getCoProduto());
            response.setDescricaoProduto(produto.getNoProduto());
            response.setTaxaJuros(taxaJurosMensal);
            response.setResultadoSimulacao(resultados);
            response.setTimestamp(LocalDateTime.now());
            
            try {
                String jsonResponse = objectMapper.writeValueAsString(response);
                novaSimulacao.setResultadoSimulacaoJson(jsonResponse);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            novaSimulacao.setValorDesejado(request.getValorDesejado());
            novaSimulacao.setPrazo(request.getPrazo());
            novaSimulacao.setCodigoProduto(produto.getCoProduto());
            novaSimulacao.setDescricaoProduto(produto.getNoProduto());
            novaSimulacao.setTaxaJuros(taxaJurosMensal);
            novaSimulacao.setDataSimulacao(LocalDate.now());
            novaSimulacao.setValorTotalCredito(valorTotalCredito);
            
            simulacaoRepository.save(novaSimulacao);
            
            return response;
        }

        return null;
    }

    public List<Simulacao> listarTodas() {
        return simulacaoRepository.findAll();
    }

    public AgregadoDiarioDTO agregarPorProdutoEData(LocalDate data) {
        List<Simulacao> simulacoesDoDia = simulacaoRepository.findByDataSimulacao(data);

        List<SimulacaoPorProdutoDTO> agregados = simulacoesDoDia.stream()
                .collect(Collectors.groupingBy(Simulacao::getCodigoProduto))
                .entrySet().stream()
                .map(entry -> {
                    Integer codigoProduto = entry.getKey();
                    List<Simulacao> simulacoes = entry.getValue();

                    BigDecimal valorTotalDesejado = simulacoes.stream()
                            .map(Simulacao::getValorDesejado)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal valorTotalCredito = simulacoes.stream()
                            .map(Simulacao::getValorTotalCredito)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal valorMedioPrestacao = valorTotalCredito.divide(BigDecimal.valueOf(simulacoes.size()), 2, RoundingMode.HALF_UP);
                    
                    BigDecimal taxaMediaJuros = simulacoes.stream()
                        .map(Simulacao::getTaxaJuros)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(simulacoes.size()), 9, RoundingMode.HALF_UP);

                    SimulacaoPorProdutoDTO dto = new SimulacaoPorProdutoDTO();
                    dto.setCodigoProduto(codigoProduto);
                    dto.setDescricaoProduto(simulacoes.get(0).getDescricaoProduto());
                    dto.setTaxaMediaJuros(taxaMediaJuros);
                    dto.setValorMedioPrestacao(valorMedioPrestacao);
                    dto.setValorTotalDesejado(valorTotalDesejado);
                    dto.setValorTotalCredito(valorTotalCredito);

                    return dto;
                })
                .collect(Collectors.toList());

        AgregadoDiarioDTO resposta = new AgregadoDiarioDTO();
        resposta.setDataReferencia(data);
        resposta.setSimulacoes(agregados);

        return resposta;
    }
}