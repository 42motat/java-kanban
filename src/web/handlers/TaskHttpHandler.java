package web.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskNotFoundException;
import exceptions.TimeConflicted;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = getGson();

    public TaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

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
                    String strTaskFromJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    // если в запросе не передаётся айди, то это создание нового task
                    if (taskId == null) {
                        try {
                            Task taskFromJson = gson.fromJson(strTaskFromJson, Task.class);
                            Task task = getTaskToCreate(taskFromJson);
                            taskManager.createTask(task);
                            sendCreatedStatus(exchange);
                        } catch (TimeConflicted e) {
                            sendHasTimeConflict(exchange, "Обнаружено пересечение времени с существующей задачей");
                        }
                    } else if (taskManager.getTaskById(Integer.parseInt(taskId)) == null) {
                        sendNotFound(exchange, "В списке задач нет задачи с ID " + taskId);
                    } else {
                        try {
                            Task taskFromJson = gson.fromJson(strTaskFromJson, Task.class);
                            Task task = getTaskToUpdate(taskFromJson);
                            taskManager.updateTask(task);
                            sendCreatedStatus(exchange);
                        } catch (TimeConflicted e) {
                            sendHasTimeConflict(exchange, "Обнаружено пересечение времени с существующей задачей");
                        }
                    }
                    break;

            case "DELETE":
                if (taskId == null) {
                    sendBadRequest(exchange, "Необходимо указать ID задачи для удаления");
                } else if (taskManager.getTaskById(Integer.parseInt(taskId)) == null) {
                    sendNotFound(exchange, "В списке задач нет задачи с ID " + taskId);
                } else {
                    taskManager.deleteTaskById(Integer.parseInt(taskId));
                    sendText(exchange, "Задача с ID " + taskId + " удалена");
                }
                break;

            default:
                sendBadRequest(exchange, "Проверьте корректность запроса");
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Задача не найдена");
        } catch (RuntimeException e) {
            sendBadRequest(exchange, "Произошла ошибка");
        }
    }

    private static Task getTaskToUpdate(Task taskFromJson) {
        Task task;
        if (taskFromJson.getStartTime() != null && taskFromJson.getDuration() != null) {
            task = new Task(taskFromJson.getTaskId(), taskFromJson.getTaskTitle(),
                    taskFromJson.getTaskDesc(), taskFromJson.getTaskStatus(),
                    taskFromJson.getStartTime(), taskFromJson.getDuration());
        } else {
            task = new Task(taskFromJson.getTaskId(), taskFromJson.getTaskTitle(),
                    taskFromJson.getTaskDesc(), taskFromJson.getTaskStatus(),
                    null, null);
        }
        return task;
    }

    private static Task getTaskToCreate(Task taskFromJson) {
        Task task;
        if (taskFromJson.getStartTime() != null && taskFromJson.getDuration() != null) {
            task = new Task(taskFromJson.getTaskTitle(),
                    taskFromJson.getTaskDesc(), taskFromJson.getTaskStatus(),
                    taskFromJson.getStartTime(), taskFromJson.getDuration());
        } else {
            task = new Task(taskFromJson.getTaskTitle(),
                    taskFromJson.getTaskDesc(), taskFromJson.getTaskStatus(),
                    null, null);
        }
        return task;
    }
}
