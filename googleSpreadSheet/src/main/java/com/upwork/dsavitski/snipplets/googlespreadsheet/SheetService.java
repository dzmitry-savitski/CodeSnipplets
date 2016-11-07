package com.upwork.dsavitski.snipplets.googlespreadsheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Represents google API auth service
 */
class SheetService {
    private static final java.io.File DATA_STORE_DIR = new java.io.File(Config.WORKING_DIR);
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);
    private static final Logger logger = Logger.getLogger(SheetService.class);
    private static Sheets service;
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            logger.error("SheetService error! ", t);
            System.exit(1);
        }
    }

    /**
     * Provides google oauth 2.0 authentication
     */
    private static Credential authorize() throws IOException {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY,
                        new InputStreamReader(new ByteArrayInputStream(Config.API.getBytes())));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        logger.info("Google api login success.  Credentials saved to " +
                DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Returns ready to work spreadSheet service
     */
    public static Sheets getSheetsService() throws IOException {
        if (service == null) {
            Credential credential = authorize();
            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(Config.APPLICATION_NAME)
                    .build();
        }
        return service;
    }
}
