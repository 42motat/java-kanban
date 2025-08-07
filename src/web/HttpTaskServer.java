package web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;
import web.handlers.*;
import web.jsonAdapters.DateTimeFormatterAdapter;
import web.jsonAdapters.DurationTypeAdapter;
import web.jsonAdapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer appServer;
    private static TaskManager taskManager;

//    private static Gson gson = new Gson();

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
//            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
            .create();

//    private static Gson gson = new GsonBuilder()
//            // Адаптер для LocalDateTime
//            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
//                return src == null ? null : context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            })
//            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
//                String dateTimeString = json.getAsString();
//                return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//            })
//            // Адаптер для Duration
//            .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, typeOfSrc, context) -> {
//                return src == null ? null : context.serialize(src.getSeconds());
//            })
//            .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, typeOfT, context) -> {
//                long seconds = json.getAsLong();
//                return Duration.ofSeconds(seconds);
//            })
//            .create();

    public HttpTaskServer(TaskManager taskManager) {
        HttpTaskServer.taskManager = Managers.getDefault();
    }

    public static Gson getGson() {
        return gson;
    }

    public static void start() throws IOException {
        appServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        appServer.createContext("/tasks", new TaskHttpHandler(taskManager));
        appServer.createContext("/epics", new EpicHttpHandler(taskManager));
        appServer.createContext("/subtasks", new SubtaskHttpHandler(taskManager));
        appServer.createContext("/history", new HistoryHttpHandler(taskManager));
        appServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
        appServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        if (appServer != null) {
            appServer.stop(0); // Параметр указывает на время ожидания завершения текущих запросов в миллисекундах
            System.out.println("Сервер остановлен");
        }
    }

    public static void main(String[] args) throws Exception {
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();

        Task task_1 = new Task(100001, "task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);

        Task task_2 = new Task("task_2", "task_2 desc", TaskStatus.NEW, LocalDateTime.of(2025, 8, 7, 11, 24), Duration.ofMinutes(30));
        taskManager.createTask(task_2);


        //        server.stop();
    }
}


