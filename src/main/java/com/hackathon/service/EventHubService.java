package com.hackathon.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;

import org.springframework.beans.factory.annotation.Value; // Importe esta anotação

import org.springframework.stereotype.Service;

import java.util.Collections; // Importe esta classe

@Service
public class EventHubService {

    @Value("${azure.eventhub.connection-string}")
    private String connectionString;

    public void sendMessage(String message) {
        // Usamos 'try-with-resources' para garantir que o 'producer' seja fechado automaticamente
        try (EventHubProducerClient producer = new EventHubClientBuilder()
                .connectionString(connectionString)
                .buildProducerClient()) {

            // A correção está aqui: envolvemos o EventData em uma lista
            producer.send(Collections.singletonList(new EventData(message)));
            System.out.println("Mensagem enviada para o Event Hub com sucesso.");

        } catch (Exception e) {
            // Adicionamos um bloco para capturar possíveis erros de comunicação
            System.err.println("Erro ao enviar mensagem para o Event Hub: " + e.getMessage());
            e.printStackTrace();
        }
    }
}