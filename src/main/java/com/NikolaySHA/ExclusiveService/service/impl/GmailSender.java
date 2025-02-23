package com.NikolaySHA.ExclusiveService.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static jakarta.mail.Message.RecipientType.TO;

@Component
public class GmailSender {
    
    private static final String APPLICATION_NAME = "Exclusive Service Email API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String STORED_CREDENTIAL_PATH = TOKENS_DIRECTORY_PATH + "/StoredCredential";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret_299103921534-n6n44h5ju3493qilbskniinitmd0cbu4.apps.googleusercontent.com.json";
    private static final String SENDER = "exclautoservice@gmail.com";
    private final Gmail service;
    
    public GmailSender() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GmailSender.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get(TOKENS_DIRECTORY_PATH).toFile()))
                .setAccessType("offline")
                .build();
        
        Credential credential = flow.loadCredential("user");
        
        if (credential != null && credential.getAccessToken() != null) {
            try {
                if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
                    System.out.println("Access token expired. Attempting to refresh...");
                    if (credential.refreshToken()) {
                        System.out.println("Token refreshed successfully.");
                    } else {
                        throw new IOException("Failed to refresh token!");
                    }
                }
            } catch (IOException e) {
                System.err.println("Token is invalid. Deleting stored credentials...");
                deleteStoredCredential();
                credential = null;  // Задължително, за да се поиска нов токен
            }
        }
        
        if (credential == null) {
            System.out.println("No valid credentials found. Requesting new authorization...");
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
        
        return credential;
    }
    
    
    private static void deleteStoredCredential() {
        try {
            Files.deleteIfExists(Paths.get(STORED_CREDENTIAL_PATH));
            System.out.println("Stored credential deleted.");
        } catch (IOException e) {
            System.err.println("Error deleting stored credential: " + e.getMessage());
        }
    }
    
    public void sendMail(String subject, String message, String recipient) throws IOException, GeneralSecurityException, MessagingException {
        Credential credential = getCredentials(GoogleNetHttpTransport.newTrustedTransport());
        
        if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
            System.out.println("Refreshing token before sending email...");
            if (!credential.refreshToken()) {
                System.err.println("Failed to refresh token before sending email! Deleting stored credentials...");
                deleteStoredCredential();
                throw new IOException("Cannot refresh token. User reauthorization required.");
            }
        }
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(SENDER));
        email.addRecipient(TO, new InternetAddress(recipient));
        email.setSubject(subject);
        email.setText(message);
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message msg = new Message();
        msg.setRaw(encodedEmail);
        
        try {
            msg = service.users().messages().send("me", msg).execute();
            System.out.println("Message sent successfully. Message ID: " + msg.getId());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }
}
