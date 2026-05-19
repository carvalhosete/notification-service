package br.com.project.notification_service.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Montar e enviar o e-mail de boas-vindas com os dados corretos")
    void enviarEmailBoasVindasCenario1(){
        String emailDestino = "cliente@email.com";
        String nomeCliente = "Cliente";

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.enviarEmailBoasVindas(emailDestino, nomeCliente);

        verify(mailSender).send(mailCaptor.capture());
        SimpleMailMessage mensagemCapturada = mailCaptor.getValue();

        assertEquals(emailDestino, Objects.requireNonNull(mensagemCapturada.getTo())[0]);
        assertEquals("Bem-vindo ao IAM HelpDesk!", mensagemCapturada.getSubject());
    }
}
