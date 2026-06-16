# Estágio 1: Executa a aplicação usando o JRE (mais leve e seguro que o JDK)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copia explicitamente apenas o JAR executável final gerado pelo Spring Boot
COPY target/deliverytech_fat-*.jar app.jar

EXPOSE 8080

# Melhora a inicialização do Java 21 para contêineres e reduz consumo de memória
ENTRYPOINT ["java", "-XX:+UseG1GC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]