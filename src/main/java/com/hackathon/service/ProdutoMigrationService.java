package com.hackathon.service;

import com.hackathon.model.Produto;
import com.hackathon.repository.ProdutoRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!test") // Garante que este serviço não rode durante os testes
public class ProdutoMigrationService implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;

    // Injeta as credenciais do SQL Server diretamente do application.properties
    @Value("${sqlserver.datasource.url}")
    private String sqlServerUrl;

    @Value("${sqlserver.datasource.username}")
    private String sqlServerUsername;

    @Value("${sqlserver.datasource.password}")
    private String sqlServerPassword;

    @Value("${sqlserver.datasource.driver-class-name}")
    private String sqlServerDriverClassName;

    public ProdutoMigrationService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) {
        // Verifica se o banco de dados local (H2) já tem dados.
        // Se tiver, a migração não é necessária.
        if (produtoRepository.count() > 0) {
            System.out.println("### PRODUTO MIGRATION: Banco de dados local já populado. Migração ignorada.");
            return;
        }

        System.out.println("### PRODUTO MIGRATION: Iniciando migração de dados do SQL Server...");
        HikariDataSource sqlServerDataSource = null;
        try {
            // 1. Cria a fonte de dados (DataSource) para o SQL Server manualmente
            sqlServerDataSource = createSqlServerDataSource();
            JdbcTemplate sqlServerJdbcTemplate = new JdbcTemplate(sqlServerDataSource);
            
            System.out.println("### PRODUTO MIGRATION: Conexão com SQL Server estabelecida.");

            // 2. Executa a consulta para buscar os produtos
            String sql = "SELECT CO_PRODUTO, NO_PRODUTO, PC_TAXA_JUROS, NU_MINIMO_MESES, NU_MAXIMO_MESES, VR_MINIMO, VR_MAXIMO FROM dbo.PRODUTO";
            
            System.out.println("### PRODUTO MIGRATION: Executando query no SQL Server...");
            List<Produto> produtos = sqlServerJdbcTemplate.query(sql, (rs, rowNum) -> {
                Produto produto = new Produto();
                produto.setCoProduto(rs.getInt("CO_PRODUTO"));
                produto.setNoProduto(rs.getString("NO_PRODUTO"));
                produto.setPcTaxaJuros(rs.getBigDecimal("PC_TAXA_JUROS"));
                produto.setNuMinimoMeses(rs.getInt("NU_MINIMO_MESES"));

                int nuMaximoMeses = rs.getInt("NU_MAXIMO_MESES");
                if (!rs.wasNull()) {
                    produto.setNuMaximoMeses(nuMaximoMeses);
                }
                
                produto.setVrMinimo(rs.getBigDecimal("VR_MINIMO"));

                BigDecimal vrMaximo = rs.getBigDecimal("VR_MAXIMO");
                if (!rs.wasNull()) {
                    produto.setVrMaximo(vrMaximo);
                }
                return produto;
            });
            
            System.out.println("### PRODUTO MIGRATION: " + produtos.size() + " produtos encontrados.");

            // 3. Salva os produtos no banco de dados local (H2)
            produtoRepository.saveAll(produtos);
            System.out.println("### PRODUTO MIGRATION: Dados salvos no banco de dados H2 com sucesso!");

        } catch (Exception e) {
            System.err.println("### PRODUTO MIGRATION: ERRO! Falha ao migrar dados do SQL Server.");
            e.printStackTrace(); // Imprime o erro detalhado no console
        
        } finally {
            // 4. Garante que a conexão com o SQL Server seja fechada
            if (sqlServerDataSource != null && !sqlServerDataSource.isClosed()) {
                sqlServerDataSource.close();
                System.out.println("### PRODUTO MIGRATION: Conexão com o SQL Server foi fechada.");
            }
        }
    }

    // Método auxiliar para criar e configurar o DataSource do SQL Server
    private HikariDataSource createSqlServerDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(sqlServerUrl);
        dataSource.setUsername(sqlServerUsername);
        dataSource.setPassword(sqlServerPassword);
        dataSource.setDriverClassName(sqlServerDriverClassName);
        return dataSource;
    }
}