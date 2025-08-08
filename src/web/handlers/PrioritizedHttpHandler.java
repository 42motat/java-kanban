package web.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = getGson();

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, gson.toJson(prioritizedTasks));
                break;
            case "POST", "DELETE":
                sendMethodNotAllowed(exchange);
                break;
            default:
                sendBadRequest(exchange, "Проверьте корректность запроса");
        }
    }
}
