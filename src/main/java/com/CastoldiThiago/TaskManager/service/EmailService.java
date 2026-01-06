package com.CastoldiThiago.TaskManager.service;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String fromEmail;

    public void sendVerificationEmail(String to, String code) {
        // 1. Configurar cliente
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

        // 2. Configurar Remitente
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(fromEmail);
        sender.setName("Task Manager App");
        sendSmtpEmail.setSender(sender);

        // 3. Configurar Destinatario
        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        recipient.setEmail(to);
        sendSmtpEmail.setTo(List.of(recipient));

        // 4. Contenido
        sendSmtpEmail.setSubject("Verificación de Cuenta");
        sendSmtpEmail.setHtmlContent("<html><body><h1>Tu código es: " + code + "</h1></body></html>");

        // 5. Enviar
        try {
            CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
            System.out.println("Email enviado! ID: " + result.getMessageId());
        } catch (ApiException e) {
            System.err.println("Error enviando email: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}