package br.com.project.notification_service.consumer;

import br.com.project.notification_service.dto.NotificacaoBloqueioDTO;
import br.com.project.notification_service.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NotificacaoBloqueioConsumer {

    @Autowired
    private EmailService emailService;

    @SqsListener("fila-notificacao-bloqueio")
    public void receberNotificacao(NotificacaoBloqueioDTO payload){
        System.out.println("Payload JSON recebido com sucesso na AWS SQS!");
        System.out.println("ALVO: " + payload.email());
        System.out.println("MOTIVO: " + payload.motivo());
        System.out.println("DATA: " + payload.dataHora());

        emailService.enviarEmailBloqueio(payload.email());
    }
}
