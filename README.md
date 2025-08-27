# API de Simulação de Crédito - CEF Hackathon Agosto 2025

Esta é a API de backend desenvolvida como solução para o desafio do Hackathon da CAIXA de Agosto de 2025.

## Sobre o Projeto

A API permite a simulação de empréstimos utilizando os sistemas de amortização SAC e Price, com base em produtos, taxas e condições pré-definidas.

---

### Autor

* **Nome:** Andre C A Loureiro
* **Matrícula:** c107192

---

### Como Executar

#### Pré-requisitos
* Java 17
* Maven
* Docker e Docker Compose

#### Executando localmente
1.  Compile o projeto:
    ```sh
    ./mvnw clean package
    ```
2.  Execute a aplicação:
    ```sh
    java -jar target/sim-api-0.0.1-SNAPSHOT.jar
    ```

#### Executando com Docker
1.  Construa a imagem e inicie o container:
    ```sh
    docker-compose up --build [-d opcional para rodar no background]
    ```
A aplicação estará disponível em `http://localhost:8080`.

--> para encerrar: docker-compose down

## Endpoints da API

A seguir estão os detalhes dos endpoints disponíveis na aplicação.

### 0. Inicialização do Servidor

Ao inicializar, o sistema verifica se já existe pelo menos 1 registro na tabela PRODUTOS. Caso não haja, se conecta ao SQL server pára baixar a versão mais recente da tabela de produtos.


### 1. Simular Empréstimo

Realiza uma simulação de empréstimo:
- com base no valor e prazo desejados, 
- calcula as parcelas para os sistemas SAC e Price
- envia JSON resposta para o EventHub
- persiste o resultado.

* **Endpoint:** `POST /simulacoes`
* **Descrição:** Recebe um valor e um prazo, encontra um produto de crédito compatível, realiza os cálculos, salva a simulação e envia o resultado para o Azure Event Hub.
* **Corpo da Requisição (Request Body):**

    ```json
    {
        "valorDesejado": 15000.00,
        "prazo": 36
    }
    ```

* **Exemplo de Resposta (Response Body):**

    ```json
    {
        "idSimulacao": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "codigoProduto": 2,
        "descricaoProduto": "Produto 2",
        "taxaJuros": "0.017500000",
        "resultadoSimulacao": [
            {
                "tipo": "SAC",
                "parcelas": [
                    { "numero": 1, "valorAmortizacao": "416.67", "valorJuros": "262.50", "valorPrestacao": "679.17" },
                    { "numero": 2, "valorAmortizacao": "416.67", "valorJuros": "255.21", "valorPrestacao": "671.88" }
                ]
            },
            {
                "tipo": "PRICE",
                "parcelas": [
                    { "numero": 1, "valorAmortizacao": "298.37", "valorJuros": "262.50", "valorPrestacao": "560.87" },
                    { "numero": 2, "valorAmortizacao": "303.59", "valorJuros": "257.28", "valorPrestacao": "560.87" }
                ]
            }
        ],
        "timestamp": "2025-08-27T16:00:00.000Z"
    }
    ```

### 2. Listar Todas as Simulações

Retorna uma lista de todas as simulações já realizadas e salvas no banco de dados.

* **Endpoint:** `GET /simulacoes`
* **Descrição:** Retorna a lista completa de simulações persistidas.
* **Exemplo de Resposta (Response Body):**

    ```json
    [
        {
            "idSimulacao": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
            "valorDesejado": 15000.00,
            "prazo": 36,
            "codigoProduto": 2,
            "descricaoProduto": "Produto 2",
            "taxaJuros": 0.017500000,
            "dataSimulacao": "2025-08-27",
            "valorTotalCredito": 20191.32,
            "resultadoSimulacaoJson": "{...}"
        }
    ]
    ```

### 3. Consolidado Diário por Produto

Retorna dados agregados das simulações realizadas em um dia específico, agrupados por produto.

* **Endpoint:** `GET /simulacoes/por-dia?dataReferencia=YYYY-MM-DD`
* **Descrição:** Agrega os valores totais, taxas médias e valores médios de prestação para todas as simulações de uma data, separados por produto.
* **Parâmetro de Requisição (Query Param):**
    * `dataReferencia`: A data no formato `AAAA-MM-DD`. Ex: `2025-08-27`
* **Exemplo de Resposta (Response Body):**

    ```json
    {
        "dataReferencia": "2025-08-27",
        "simulacoes": [
            {
                "codigoProduto": 2,
                "descricaoProduto": "Produto 2",
                "taxaMediaJuros": 0.017500000,
                "valorMedioPrestacao": 560.87,
                "valorTotalDesejado": 15000.00,
                "valorTotalCredito": 20191.32
            }
        ]
    }
    ```

### 4. Telemetria da API

Retorna métricas de performance e uso dos endpoints da API.

* **Endpoint:** `GET /telemetria`
* **Descrição:** Exibe a quantidade de requisições, tempos de resposta (médio e máximo) e o percentual de sucesso para os endpoints monitorados.
* **Exemplo de Resposta (Response Body):**

    ```json
    {
        "dataReferencia": "2025-08-27",
        "listaEndpoints": [
            {
                "nomeApi": "Simulacao (POST)",
                "qtdRequisicoes": 50,
                "tempoMedio": 120.5,
                "tempoMinimo": 0.0,
                "tempoMaximo": 350.0,
                "percentualSucesso": 1.0
            },
            {
                "nomeApi": "Simulacao (GET)",
                "qtdRequisicoes": 15,
                "tempoMedio": 45.2,
                "tempoMinimo": 0.0,
                "tempoMaximo": 80.0,
                "percentualSucesso": 1.0
            }
        ]
    }
    ```

---