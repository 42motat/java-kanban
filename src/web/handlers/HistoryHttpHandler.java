package web.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import web.HttpTaskServer;

import java.io.IOException;

public class HistoryHttpHandler extends BaseHttpHandler implements HttpHandler {
    Gson gson = HttpTaskServer.getGson();

    public HistoryHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                String history = gson.toJson(taskManager.getHistory());
                sendText(exchange, gson.toJson(history));
                break;
            case "POST", "DELETE":
                sendBadRequest(exchange, "Метод не поддерживается");
                break;
            default:
                sendBadRequest(exchange, "Поддерживаемые методы: GET");
        }
    }
}
