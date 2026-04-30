package br.com.project.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailBloqueio(String destinatario){
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(destinatario);
        mensagem.setSubject("Alerta de Segurança: Conta Bloqueada");
        mensagem.setText("Olá!\n\n" +
                "Detectamos múltiplas tentativas de login falhas na sua conta. " +
                "Por motivos de segurança, o seu acesso foi temporariamente bloqueado.\n\n" +
                "Por favor, entre em contato com o administrador do sistema.");

        mailSender.send(mensagem);
        System.out.println("E-mail disparado som sucesso para o Mailtrap " + destinatario);
    }

    public void enviarEmailBoasVindas(String destinatario, String nome){
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(destinatario);
        mensagem.setSubject("Bem-vindo ao IAM HelpDesk!");
        mensagem.setText("Olá, " + nome + "!\n\n" +
                "Sua conta foi criada com sucesso.\n" +
                "Estamos felizes em ter você a bordo do nosso sistema.\n" +
                "Acesse a plataforma de HelpDesk utilizando seu e-mail e senha cadastrados.");

        mailSender.send(mensagem);
        System.out.println("E-mail de boas vindas disparado para: " + destinatario);
    }
}