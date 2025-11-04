package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import manager.exceptions.NotFoundException;

import tasks.Subtask;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
                case "DELETE" -> handleDelete(h, path);
                default -> sendNotFound(h);
            }
        } catch (Exception e) {
            handleException(h, e);
        }
    }


    private void handleGet(HttpExchange h, String path) throws IOException {
        String[] splitStrings = path.split("/");

        if (splitStrings.length == 2) {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            sendText(h, gson.toJson(subtasks));
        } else if (splitStrings.length == 3) {
            int id = Integer.parseInt(splitStrings[2]);
            Subtask subtask = taskManager.getSubtaskByID(id);
            sendText(h, gson.toJson(subtask));
        } else {
            throw new NotFoundException("Wrong path");
        }

    }

    private void handlePost(HttpExchange h) throws IOException {
        InputStream is = h.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getId() == 0) {
                taskManager.addSubtask(subtask);
            } else {
                taskManager.updateSubtask(subtask);
            }
            sendSuccess(h);
        } catch (Exception e) {
            handleException(h, e);
        }

    }

    private void handleDelete(HttpExchange h, String path) throws IOException {
        String[] splitStrings = path.split("/");

        if (splitStrings.length == 2) {
            taskManager.removeAllSubtasks();
            sendSuccess(h);
        } else if (splitStrings.length == 3) {
            int id = Integer.parseInt(splitStrings[2]);
            taskManager.removeSubtaskById(id);
            sendGood(h);
        } else {
            throw new NotFoundException("Wrong path");
        }
    }
}
