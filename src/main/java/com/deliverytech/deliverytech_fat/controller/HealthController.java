package com.deliverytech.deliverytech_fat.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation; // 🔑 Importação necessária
import io.swagger.v3.oas.annotations.tags.Tag;     // 🔑 Importação necessária

/**
 * Controller responsável pelos endpoints de monitoramento da aplicação
 * Demonstra o uso de recursos modernos do Java 21
 */
@Tag(name = "Saúde da Aplicação", description = "Endpoints de monitoramento, DevOps e informações de infraestrutura do sistema")
@RestController
public class HealthController {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Endpoint para verificar o status da aplicação
     * @return Map com informações de saúde da aplicação
     */
    @Operation(
        summary = "Verificar o status de saúde do servidor", 
        description = "Retorna um mapa de dados imutável indicando que a API está operando (UP), exibindo a versão do Java em execução e a versão do Spring Boot mapeada na memória."
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        // Usando Map.of() (Java 9+) para criar mapa imutável
        Map<String, String> healthInfo = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().format(FORMATTER),
            "service", "Delivery API JAVA",
            "javaVersion", System.getProperty("java.version"),
            "springBootVersion", getClass().getPackage().getImplementationVersion() != null
                ? getClass().getPackage().getImplementationVersion() : "3.2.x",
            "environment", "development"
        );

        return ResponseEntity.ok(healthInfo);
    }   

    /**
     * Endpoint com informações detalhadas da aplicação
     * Demonstra o uso de Records (Java 14+)
     * @return AppInfo com dados da aplicação
     */
    @Operation(
        summary = "Obter informações detalhadas do projeto", 
        description = "Retorna dados institucionais da API utilizando a estrutura imutável de Records do Java. Exibe o nome do desenvolvedor, versão atual do ecossistema e descrição técnica."
    )
    @GetMapping("/info")
    public ResponseEntity<AppInfo> info() {
        AppInfo appInfo = new AppInfo(
            "Delivery Tech API",
            "1.0.0",
            "Allan Gutierez",
            System.getProperty("java.version"),
            "Spring Boot 3.2.x",
            LocalDateTime.now().format(FORMATTER),
            "Sistema de delivery moderno desenvolvido com as mais recentes tecnologias Java"
        );

        return ResponseEntity.ok(appInfo);
    }   

    /**
     * Record para demonstrar recurso do Java 14+ (disponível no JDK 21)
     * Records são classes imutáveis ideais para DTOs
     */
    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework,
        String timestamp,
        String description
    ) {
        // Construtor compacto para validação (opcional)
        public AppInfo {
            if (application == null || application.isBlank()) {
                throw new IllegalArgumentException("Application name cannot be null or blank");
            }
        }
    }
}
