# Estágio 1: Build da aplicação com Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace/app

# Copia os arquivos do Maven e baixa as dependências
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copia o código fonte e compila o projeto
COPY src src
RUN ./mvnw package -DskipTests

# Estágio 2: Criação da imagem final, menor e otimizada
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia apenas o .jar compilado do estágio de build anterior
COPY --from=build /workspace/app/target/sim-api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta em que a aplicação roda
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]