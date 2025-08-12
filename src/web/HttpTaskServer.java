/* Александру Ф.
 * Добрый день, Александр!
 * Заранее благодарю за код-ревью.
 */

package web;

import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;
import web.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer appServer;
    private static TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        HttpTaskServer.taskManager = taskManager;
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
            appServer.stop(0);
            System.out.println("Сервер остановлен");
        }
    }

    public static void main(String[] args) throws Exception {

    }
}


