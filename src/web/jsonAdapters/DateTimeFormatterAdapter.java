package web.jsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterAdapter extends TypeAdapter<DateTimeFormatter> {

    @Override
    public void write(JsonWriter out, DateTimeFormatter value) throws IOException {
        // Здесь вы можете определить, как именно будет записываться объект DateTimeFormatter в JSON
        value = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        out.value(value.toString());
    }

    @Override
    public DateTimeFormatter read(JsonReader in) throws IOException {
        // Здесь вы должны определить, как будет создаваться объект DateTimeFormatter из JSON
        String pattern = in.nextString();
        return DateTimeFormatter.ofPattern(pattern);
    }
}
