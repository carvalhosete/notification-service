package br.com.project.notification_service.consumer;

import br.com.project.notification_service.dto.NotificacaoBoasVindasDTO;
import br.com.project.notification_service.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoBoasVindasConsumer {

    @Autowired
    private EmailService emailService;

    @SqsListener("fila-notificacao-boas-vindas")
    public void receberNotificacao(NotificacaoBoasVindasDTO payload){
        System.out.println("Novo usuário cadastrado! Preparando e-mail para " + payload.email());

        try{
            emailService.enviarEmailBoasVindas(payload.email(), payload.nome());
        } catch (MailException e){
            System.err.println("Erro ao disparar SMTP para boas-vindas " + e.getMessage());
            throw e;
        } catch (Exception e){
            System.err.println("Erro inesperado no processamento de boas-vindas: " + e.getMessage());
        }
    }
}
