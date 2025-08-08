package web.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskNotFoundException;
import exceptions.TimeConflicted;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

public class SubtaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = getGson();

    public SubtaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

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
                    String strSubtaskFromJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    if (subtaskId == null) {
                        try {
                            Subtask subtaskFromJson = gson.fromJson(strSubtaskFromJson, Subtask.class);
                            int epicId = subtaskFromJson.getEpicId();
                            Subtask subtask = getSubtaskToCreate(epicId, subtaskFromJson);
                            taskManager.createSubtask(taskManager.getEpicById(epicId), subtask);
                            sendCreatedStatus(exchange);
                        } catch (TimeConflicted e) {
                            sendHasTimeConflict(exchange, "Обнаружено пересечение времени с существующей задачей");
                        }
                    } else if (taskManager.getTaskById(Integer.parseInt(subtaskId)) == null) {
                            sendNotFound(exchange, "В списке подзадач нет подзадачи с ID " + subtaskId);
                    } else {
                        try {
                            Subtask subtaskFromJson = gson.fromJson(strSubtaskFromJson, Subtask.class);
                            int epicId = subtaskFromJson.getEpicId();
                            Subtask subtask = getSubtaskToUpdate(epicId, subtaskFromJson);
                            taskManager.updateSubtask(subtask, subtask, taskManager.getEpicById(epicId));
                            sendCreatedStatus(exchange);
                        } catch (TimeConflicted e) {
                            sendHasTimeConflict(exchange, "Обнаружено пересечение времени с существующей задачей");
                        }
                    }
                    break;

                case "DELETE":
                    if (subtaskId == null) {
                        sendBadRequest(exchange, "Необходимо указать ID подзадачи для удаления");
                    } else if (taskManager.getTaskById(Integer.parseInt(subtaskId)) == null) {
                        sendNotFound(exchange, "В списке подзадач нет задачи с ID " + subtaskId);
                    } else {
                        taskManager.deleteTaskById(Integer.parseInt(subtaskId));
                        sendText(exchange, "Подзадача с ID " + subtaskId + " удалена");
                    }
                    break;
                default:
                    sendBadRequest(exchange, "Проверьте корректность запроса");
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Подзадача не найдена");
        } catch (RuntimeException e) {
            sendBadRequest(exchange, "Произошла ошибка");
        }
    }

    private static Subtask getSubtaskToUpdate(int epicId, Subtask subtaskFromJson) {
        Subtask subtask;
        if (subtaskFromJson.getStartTime() != null && subtaskFromJson.getDuration() != null) {
            subtask = new Subtask(subtaskFromJson.getTaskId(), subtaskFromJson.getTaskTitle(),
                                  subtaskFromJson.getTaskDesc(), subtaskFromJson.getTaskStatus(),
                                   epicId, subtaskFromJson.getStartTime(), subtaskFromJson.getDuration());
        } else {
            subtask = new Subtask(subtaskFromJson.getTaskId(), subtaskFromJson.getTaskTitle(),
                    subtaskFromJson.getTaskDesc(), subtaskFromJson.getTaskStatus(), epicId, null, null);
        }
        return subtask;
    }

    private static Subtask getSubtaskToCreate(int epicId, Subtask subtaskFromJson) {
        Subtask subtask;
        if (subtaskFromJson.getStartTime() != null && subtaskFromJson.getDuration() != null) {
            subtask = new Subtask(subtaskFromJson.getTaskTitle(), subtaskFromJson.getTaskDesc(), epicId,
                                  subtaskFromJson.getStartTime(), subtaskFromJson.getDuration());
        } else {
            subtask = new Subtask(subtaskFromJson.getTaskTitle(), subtaskFromJson.getTaskDesc(), epicId,
                                 null, null);
        }
        return subtask;
    }
}

