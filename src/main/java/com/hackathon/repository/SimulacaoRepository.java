package com.hackathon.repository;

import com.hackathon.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, UUID> {
  List<Simulacao> findByDataSimulacao(LocalDate data);
}