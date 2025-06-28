package app;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonDataManager {

    private static final String FILE_PATH = "yatirimlar.json";

    // Gelişmiş GSON kurulumumuz:
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class,
            (com.google.gson.JsonSerializer<LocalDate>)(src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.toString()))
        .registerTypeAdapter(LocalDate.class,
            (com.google.gson.JsonDeserializer<LocalDate>)(json, typeOfT, context) ->
                LocalDate.parse(json.getAsString()))
        .setPrettyPrinting()
        .create();

    public static void saveInvestments(List<HisseYatirimi> list) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<HisseYatirimi> loadInvestments() {
        if (!Files.exists(Paths.get(FILE_PATH))) return new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            return gson.fromJson(reader, new TypeToken<List<HisseYatirimi>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}