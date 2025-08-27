package com.hackathon.sim_api;

import com.hackathon.service.EventHubService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.sql.DataSource;

// Desabilita a autoconfiguração padrão do DataSource para que possamos definir a nossa
@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
class SimApiApplicationTests {

    // Mock para o serviço do EventHub, como antes
    @MockitoBean
    private EventHubService eventHubService;

    // Esta classe de configuração interna será usada APENAS para os testes
    @Configuration
    static class TestDatabaseConfiguration {
        
        // Criamos manualmente o bean do DataSource, garantindo que ele exista
        // com a configuração correta antes de qualquer outra coisa.
        @org.springframework.context.annotation.Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        }
    }

    @Test
    void contextLoads() {
        // Agora o teste irá passar!
    }
}