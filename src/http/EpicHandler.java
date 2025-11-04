package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import manager.exceptions.NotFoundException;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.List;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public EpicHandler(TaskManager taskManager, Gson gson) {
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
            List<Epic> epics = taskManager.getAllEpics();
            sendText(h, gson.toJson(epics));
        } else if (splitStrings.length == 3) {
            int id = Integer.parseInt(splitStrings[2]);
            Epic epic = taskManager.getEpicByID(id);
            sendText(h, gson.toJson(epic));
        } else if (splitStrings.length == 4 && "subtasks".equals(splitStrings[3])) {
            int id = Integer.parseInt(splitStrings[2]);
            List<Subtask> subtasks = taskManager.getSubtasksByEpic(id);
            sendText(h, gson.toJson(subtasks));
        } else {
            throw new NotFoundException("Wrong path");
        }

    }

    private void handlePost(HttpExchange h) throws IOException {
        InputStream is = h.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            epic.setStatus(TaskStatus.NEW);
            if (epic.getId() == 0) {
                taskManager.addEpic(epic);
            } else {
                taskManager.updateEpic(epic);
            }
            sendSuccess(h);
        } catch (Exception e) {
            handleException(h, e);
        }

    }

    private void handleDelete(HttpExchange h, String path) throws IOException {
        String[] splitStrings = path.split("/");

        if (splitStrings.length == 2) {
            taskManager.removeAllEpics();
            sendSuccess(h);
        } else if (splitStrings.length == 3) {
            int id = Integer.parseInt(splitStrings[2]);
            taskManager.removeEpicById(id);
            sendGood(h);
        } else {
            throw new NotFoundException("Wrong path");
        }
    }

}
