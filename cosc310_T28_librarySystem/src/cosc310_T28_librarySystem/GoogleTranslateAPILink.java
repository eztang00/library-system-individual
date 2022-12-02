package cosc310_T28_librarySystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 
 * @author Maksym
 * copied from https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application
 *
 */
public class GoogleTranslateAPILink {

    public static void main(String[] args) throws IOException {
        String text = "Hello world!";
        //Translated text: Hallo Welt!
        System.out.println("Translated text: " + translate("en", "de", text));
    }

    static String translate(String langFrom, String langTo, String text) throws IOException {
        // INSERT YOU URL HERE
        String urlStr = "https://script.google.com/macros/s/AKfycbw9CH-WJOn0QygeycDIh-oZywgXicogyP2oUCg9pzijz4RSUks0py69fxbZkIx-CZDu/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}