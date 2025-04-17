package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class GsonUtil {

    private static final Gson gson = createGson();

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                //.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                //.registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                //.registerTypeAdapter(Date.class, new DateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static Gson getGson() {
        return gson;
    }
}
