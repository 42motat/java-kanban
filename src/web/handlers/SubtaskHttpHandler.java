package web.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskNotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import web.HttpTaskServer;


public class SubtaskHttpHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    Gson gson = HttpTaskServer.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String subtaskId = null;

            if (pathParts.length == 3) {
                subtaskId = pathParts[2];
            }

            switch (requestMethod) {
                case "GET":
                    if (subtaskId == null) {
                        ArrayList<Subtask> subtaskList = taskManager.getAllSubtasks();
                        String jsonList = gson.toJson(subtaskList);
                        sendText(exchange, jsonList);
                    } else if (taskManager.getTaskById(Integer.parseInt(subtaskId)) == null) {
                        sendNotFound(exchange, "В списке задач нет задачи с ID " + subtaskId);
                    } else {
                        Subtask subtask = taskManager.getSubtaskById(Integer.parseInt(subtaskId));
                        sendText(exchange, gson.toJson(subtask));
                    }
                    break;

                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String subtaskFromJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(subtaskFromJson, Subtask.class);
                    int epicId = subtask.getEpicId();

                    if (subtaskId == null) {
                        taskManager.createSubtask(taskManager.getEpicById(epicId), subtask);
                        sendCreatedStatus(exchange);
                    } else if (taskManager.getTaskById(Integer.parseInt(subtaskId)) == null) {
                        sendNotFound(exchange, "В списке подзадач нет подзадачи с ID " + subtaskId);
                    } else {
                        taskManager.updateSubtask(subtask, subtask, taskManager.getEpicById(epicId));
                        sendCreatedStatus(exchange);
                    }
                    break;

                case "DELETE":
                    if (subtaskId == null) {
                        sendText(exchange, "Необходимо указать ID подзадачи для удаления");
                    } else if (taskManager.getTaskById(Integer.parseInt(subtaskId)) == null) {
                        sendNotFound(exchange, "В списке подзадач нет задачи с ID " + subtaskId);
                    } else {
                        taskManager.deleteTaskById(Integer.parseInt(subtaskId));
                        sendText(exchange, "Подзадача с ID " + subtaskId + " удалена");
                    }
                    break;
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Подзадача не найдена");
        } catch (RuntimeException e) {
            sendBadRequest(exchange, "Произошла ошибка");
        }
    }
}

