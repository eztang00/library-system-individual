package cosc310_T28_librarySystem;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

/**
 * 
 * @author Maksym
 * copied from https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application
 * 
 */
public class GoogleStaticMapAPILink {

//    public static void main(String[] args) throws IOException {
//        System.out.println("Map: " + getMap().getWidth() + " " + getMap().getHeight());
//    }

    static BufferedImage getMap(String address) throws IOException {
	if (address == null || address.equals("")) {
	    address = "Rutland, Kelowna, BC";
	}
        String urlStr = "https://script.google.com/macros/s/AKfycbw0ldfBQE9NnuHzur6LSrPuEYgnlIQ1L3Awp5pYk1OPZXlNfG2YI4uf3z_Av0pf4sLP2g/exec" +
                "?start=" + URLEncoder.encode(address, "UTF-8") +
                "&end=" + URLEncoder.encode("3333 University Way, Kelowna, BC V1V 1V7", "UTF-8");
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
        //mostly copied from https://stackoverflow.com/questions/456367/reverse-parse-the-output-of-arrays-tostringint
        String[] strings = response.toString().split(",");
        byte[] result = new byte[strings.length];
        for (int i = 0; i < result.length; i++) {
          result[i] = Byte.parseByte(strings[i]);
        }
        return ImageIO.read(new ByteArrayInputStream(result));
    }
}