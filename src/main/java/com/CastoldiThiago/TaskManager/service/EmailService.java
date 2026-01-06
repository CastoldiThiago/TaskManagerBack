package com.CastoldiThiago.TaskManager.service;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String fromEmail;

    // Método específico para verificación
    public void sendVerificationEmail(String to, String code) {
        String subject = "Verificación de Cuenta - Task Manager";
        String htmlContent = "<html><body>" +
                "<h1>Bienvenido</h1>" +
                "<p>Tu código de verificación es: <strong>" + code + "</strong></p>" +
                "</body></html>";

        sendEmailInternal(to, subject, htmlContent);
    }

    // Método genérico
    public void sendEmail(String to, String subject, String text) {
        // Envolvemos el texto plano en HTML básico para que Brevo lo acepte bien
        String htmlContent = "<html><body><p>" + text + "</p></body></html>";

        sendEmailInternal(to, subject, htmlContent);
    }

    // Método privado para no repetir código de configuración de Brevo
    private void sendEmailInternal(String to, String subject, String htmlContent) {
        try {
            // 1. Configurar Cliente
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
            apiKeyAuth.setApiKey(apiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

            // 2. Remitente
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(fromEmail);
            sender.setName("Task Manager App");
            sendSmtpEmail.setSender(sender);

            // 3. Destinatario
            SendSmtpEmailTo recipient = new SendSmtpEmailTo();
            recipient.setEmail(to);
            sendSmtpEmail.setTo(List.of(recipient));

            // 4. Contenido
            sendSmtpEmail.setSubject(subject);
            sendSmtpEmail.setHtmlContent(htmlContent);

            // 5. Enviar
            CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
            System.out.println("Email enviado a " + to + ". ID: " + result.getMessageId());

        } catch (ApiException e) {
            System.err.println("Error enviando email: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}