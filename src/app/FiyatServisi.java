package app;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FiyatServisi {
    private static final String API_KEY = "d1d83c9r01qic6lhp5s0d1d83c9r01qic6lhp5sg";

    public static Double getVarlikFiyati(String kod) {
        kod = kod.trim().toUpperCase();
        if (kod.startsWith("BINANCE:")) {
            // Kullanıcı doğrudan tam kripto kodunu girdiyse
            return getKriptoFiyat(kod);
        } else if (kod.endsWith("USDT")) {
            // Kullanıcı sade kripto kodu girdiyse (örn BTCUSDT, ETHUSDT)
            return getKriptoFiyat("BINANCE:" + kod);
        } else {
            // Diğer her şeyi hisse olarak kabul et
            return getHisseFiyati(kod);
        }
    }

    private static Double getKriptoFiyat(String sembol) {
        try {
            String urlStr = "https://finnhub.io/api/v1/quote?symbol=" + sembol + "&token=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream input = conn.getInputStream();
            JsonObject root = JsonParser.parseReader(new InputStreamReader(input)).getAsJsonObject();
            if (root.has("c") && !root.get("c").isJsonNull()) {
                return root.get("c").getAsDouble();
            } else {
                System.out.println("Kripto Fiyat çekilemedi: " + sembol);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Kripto Fiyat çekilemedi: " + sembol + " => " + e.getMessage());
            return null;
        }
    }

    private static Double getHisseFiyati(String kod) {
        try {
            String queryKodu = "BIST_" + kod.toUpperCase();
            String urlStr = "https://finnhub.io/api/v1/quote?symbol=" + queryKodu + "&token=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream input = conn.getInputStream();
            JsonObject root = JsonParser.parseReader(new InputStreamReader(input)).getAsJsonObject();
            if (root.has("c") && !root.get("c").isJsonNull()) {
                return root.get("c").getAsDouble();
            } else {
                System.out.println("Hisse fiyatı çekilemedi: " + kod);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Hisse fiyatı çekilemedi: " + kod + " => " + e.getMessage());
            return null;
        }
    }
}