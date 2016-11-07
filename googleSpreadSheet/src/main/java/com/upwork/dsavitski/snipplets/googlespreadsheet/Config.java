package com.upwork.dsavitski.snipplets.googlespreadsheet;

import java.nio.file.Paths;

public class Config {
    public static final String WORKING_DIR = Paths.get("").toAbsolutePath().toString();
    public static final String API = "{\"installed\":{\"client_id\":\"xxx\",\"project_id\":\"xxx\",\"auth_uri\":\"ht" +
            "tps://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://accounts.google.com/o/oauth2/token\"," +
            "\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"xxxx" +
            "xx\",\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"http://localhost\"]}}";
    public static final String APPLICATION_NAME =
            "Test App";
    public static final String SPREAD_SHEET_NAME = "SpreadSheet";
    public static final String SHEET_NAME = "Sheet_Name";
    public static final String SHEET_TITLES = "Text;Number1;Number2;PercentFormula";
}
