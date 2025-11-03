package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();

            switch (method) {
                case "GET" -> handleGet(h, path);
                case "POST" -> handlePost(h);
                case "DELETE" -> handleDelete(h);
                default -> sendNotFound(h);
            }
        } catch (IllegalStateException e) {
            sendHasInteractions(h);
        } catch (Exception e) {
            sendServerError(h);
        }


    }


    private void handleGet(HttpExchange h, String path) throws IOException {
        sendText(h, "ok");
    }

    private void handlePost(HttpExchange h) throws IOException {
        sendSuccess(h);
    }

    private void handleDelete(HttpExchange h) throws IOException {
        sendSuccess(h);
    }

}