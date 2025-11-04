package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();

            if ("GET".equals(method)) {
                handleGet(h);
            } else {
                sendNotFound(h);
            }
        } catch (Exception e) {
            handleException(h, e);
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String json = gson.toJson(prioritizedTasks);
        sendText(h, gson.toJson(json));
    }
}
