package web.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskNotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import web.HttpTaskServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    Gson gson = HttpTaskServer.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            String requestMethod = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            String epicId = null;
            String subtasks = null;

            if (pathParts.length == 3) {
                epicId = pathParts[2];
            } else if (pathParts.length == 4) {
                epicId = pathParts[2];
                subtasks = pathParts[3];
            }

            switch (requestMethod) {
                case "GET":
                    if (epicId == null) {
                        ArrayList<Epic> epicList = taskManager.getAllEpics();
                        String jsonList = gson.toJson(epicList);
                        sendText(exchange, jsonList);
                    } else if (epicId != null && subtasks == null) {
                        if (taskManager.getEpicById(Integer.parseInt(pathParts[2])) == null) {
                            sendNotFound(exchange, "В списке задач нет задачи с ID " + epicId);
                        } else {
                            Epic epic = taskManager.getEpicById(Integer.parseInt(pathParts[2]));
                            sendText(exchange, gson.toJson(epic));
                        }
                    } else if (epicId != null && subtasks != null) {
                        Epic epic = taskManager.getEpicById(Integer.parseInt(pathParts[2]));
                        ArrayList<Subtask> subtaskList = taskManager.getSubtasksByEpicId(Integer.parseInt(pathParts[2]));
                        String jsonList = gson.toJson(subtaskList);
                        sendText(exchange, jsonList);
                    }
                    break;

                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String strEpicFromJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    if (epicId == null) {
                        try {
                            Epic epicFromJson = gson.fromJson(strEpicFromJson, Epic.class);
                            Epic epic = getEpic(epicFromJson);
                            taskManager.createEpic(epic);
                            sendCreatedStatus(exchange);
                        } catch (RuntimeException e) {
                            sendBadRequest(exchange, "Необходимо уточнить данные эпика");
                        }
                    } else if (taskManager.getTaskById(Integer.parseInt(epicId)) == null) {
                        sendNotFound(exchange, "В списке задач нет эпика с ID " + epicId);
                    } else {
                        try {
                            Epic epicFromJson = gson.fromJson(strEpicFromJson, Epic.class);
                            Epic epic = getEpic(epicFromJson);
                            taskManager.createEpic(epic);
                            sendCreatedStatus(exchange);
                        } catch (RuntimeException e) {
                            sendBadRequest(exchange, "Необходимо уточнить данные эпика");
                        }
                    }
                    break;

                case "DELETE":
                    if (epicId == null) {
                        sendBadRequest(exchange, "Необходимо указать ID задачи для удаления");
                    } else if (taskManager.getTaskById(Integer.parseInt(epicId)) == null) {
                        sendNotFound(exchange, "В списке задач нет задачи с ID " + epicId);
                    } else {
                        taskManager.deleteTaskById(Integer.parseInt(epicId));
                        sendText(exchange, "Задача с ID " + epicId + " удалена");
                    }
                    break;
                default:
                    sendText(exchange, "Неверный запрос");
            }

        } catch (
            TaskNotFoundException e) {
            sendNotFound(exchange, "Задача не найдена");
        } catch (RuntimeException e) {
            sendBadRequest(exchange, "Произошла ошибка");
        }
    }

    private static Epic getEpic(Epic epicFromJson) {
        Epic epic;
        if (epicFromJson.getEpicId() != null) {
            epic = new Epic(epicFromJson, epicFromJson.getTaskId(), epicFromJson.getTaskTitle(), epicFromJson.getTaskDesc());
        } else {
            epic = new Epic(epicFromJson.getTaskTitle(), epicFromJson.getTaskDesc());
        }
        return epic;
    }

}

