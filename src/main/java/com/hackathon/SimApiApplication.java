/**
 * Aplicação principal para a API de Simulação de Crédito.
 * Desafio: CEF-Hackathon-ago2025
 * * @author Andre C A Loureiro (c107192)
 * @version 1.0.0
 */

package com.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hackathon")
public class SimApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimApiApplication.class, args);
	}

}
