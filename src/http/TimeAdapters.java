package http;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeAdapters {

    private TimeAdapters() {}

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME_TYPE_ADAPTER = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(dtf));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String str = jsonReader.nextString();
            if (str == null || str.equals("null") || str.isEmpty()) {
                return null;
            }

            return LocalDateTime.parse(str, dtf);
        }
    };

    public static final TypeAdapter<Duration> DURATION_TYPE_ADAPTER = new TypeAdapter<Duration>() {
        @Override
        public void write(JsonWriter jsonWriter, Duration d) throws IOException {
            if (d == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(d.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String str = jsonReader.nextString();
            if (str == null || str.equals("null") || str.isEmpty()) {
                return null;
            }
            return Duration.ofMinutes(Long.parseLong(str));
        }
    };

    public static GsonBuilder bothAdapters(GsonBuilder gb) {
        return gb
                .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
                .registerTypeAdapter(Duration.class, DURATION_TYPE_ADAPTER);
    }
}
