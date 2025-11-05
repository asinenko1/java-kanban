package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler  {
    private final TaskManager taskManager;
    private final Gson gson;


    public HistoryHandler(TaskManager taskManager, Gson gson) {
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
        List<Task> history = taskManager.getHistory();
        String json = gson.toJson(history);
        sendText(h, json);
    }
}
