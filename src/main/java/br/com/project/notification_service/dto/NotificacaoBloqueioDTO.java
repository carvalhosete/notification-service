package br.com.project.notification_service.dto;

public record NotificacaoBloqueioDTO(
        String email,
        String motivo,
        String dataHora
) {}