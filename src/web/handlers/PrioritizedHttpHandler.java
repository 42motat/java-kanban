package web.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import web.HttpTaskServer;

import java.io.IOException;

public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    Gson gson = HttpTaskServer.getGson();

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
                break;
            case "POST", "DELETE":
                sendBadRequest(exchange, "Метод не поддерживается");
                break;
        }
    }
}
