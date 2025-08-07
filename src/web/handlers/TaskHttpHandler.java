package web.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskNotFoundException;
import managers.TaskManager;
import tasks.Task;
import web.HttpTaskServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    Gson gson = HttpTaskServer.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String taskId = null;

            if (pathParts.length == 3) {
                taskId = pathParts[2];
            }

            switch (requestMethod) {
                case "GET":
                    if (taskId == null) {
                        ArrayList<Task> taskList = taskManager.getAllTasks();
//                        System.out.println("Вот здесь");
//                        System.out.println(gson.toJson(taskList) + "ошибка");
                        String jsonList = gson.toJson(taskList);
                        sendText(exchange, jsonList);
                    } else if (taskManager.getTaskById(Integer.parseInt(taskId)) == null) {
                        sendNotFound(exchange, "В списке задач нет задачи с ID " + taskId);
                    } else {
                        Task task = taskManager.getTaskById(Integer.parseInt(taskId));
                        sendText(exchange, gson.toJson(task));
                    }
                    break;

                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String taskFromJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Здесь вот");
                    System.out.println(taskFromJson);
                    Task task = gson.fromJson(taskFromJson, Task.class);
                    System.out.println(task);

                    if (taskId == null) {
                        System.out.println("id null");
                        taskManager.createTask(task);
                        sendCreatedStatus(exchange);
                    } else if (taskManager.getTaskById(Integer.parseInt(taskId)) == null) {
                        System.out.println("id не найден");
                        sendNotFound(exchange, "В списке задач нет задачи с ID " + taskId);
                    } else {
                        System.out.println("id updated");
                        taskManager.updateTask(task);
                        sendCreatedStatus(exchange);
                    }
                    break;

                case "DELETE":
                    if (taskId == null) {
                        sendText(exchange, "Необходимо указать ID задачи для удаления");
                    } else if (taskManager.getTaskById(Integer.parseInt(taskId)) == null) {
                        sendNotFound(exchange, "В списке задач нет задачи с ID " + taskId);
                    } else {
                        taskManager.deleteTaskById(Integer.parseInt(taskId));
                        sendText(exchange, "Задача с ID " + taskId + " удалена");
                    }
                    break;
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Задача не найдена");
        } catch (RuntimeException e) {
            sendBadRequest(exchange, "Произошла ошибка");
        }
    }
}
