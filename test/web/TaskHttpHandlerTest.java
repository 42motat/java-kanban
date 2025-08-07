package web;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskHttpHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer appServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void startServer() throws IOException {
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutdownServer() {
        appServer.stop();
    }

    @Test
    public void shouldAddTaskTest() throws IOException, InterruptedException {
//        Task task_1 = new Task(100001, "task_1", "task_1 desc", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        Task task_2 = new Task("task_2", "task_2 desc", TaskStatus.NEW, LocalDateTime.of(2025, 8, 7, 11, 24), Duration.ofMinutes(30));
        String taskToJson = gson.toJson(task_1);

        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(request);
        System.out.println(response);
        assertEquals(201, response.statusCode());

    }

    @Test
    public void shouldGetTaskTest() throws IOException, InterruptedException {
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW, LocalDateTime.of(2025, 8, 7, 11, 24), Duration.ofMinutes(30));
        String taskToJson = gson.toJson(task_1);


    }

}
