package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exceptions.NotFoundException;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
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
            List<Task> tasks = taskManager.getAllTasks();
            sendText(h, gson.toJson(tasks));
        } else if (splitStrings.length == 3) {
            int id = Integer.parseInt(splitStrings[2]);
            Task task = taskManager.getTaskByID(id);
            sendText(h, gson.toJson(task));

        } else {
            throw new NotFoundException("Wrong path");
        }

    }

    private void handlePost(HttpExchange h) throws IOException {
        InputStream is = h.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Task task = gson.fromJson(body, Task.class);

            if (task == null) {
                sendBadRequest(h);
                return;
            }
            if (task.getId() == 0) {
                taskManager.addTask(task);
            } else {
                taskManager.updateTask(task);
            }
            sendSuccess(h);
        } catch (JsonSyntaxException e) {
            sendBadRequest(h);
        } catch (IllegalStateException e) {
            sendHasInteractions(h);
        } catch (Exception e) {
            System.out.println("Other exception");
            handleException(h, e);
        }

    }

    private void handleDelete(HttpExchange h, String path) throws IOException {
        String[] splitStrings = path.split("/");

        if (splitStrings.length == 2) {
            taskManager.removeAllTasks();
            sendSuccess(h);
        } else if (splitStrings.length == 3) {
            int id = Integer.parseInt(splitStrings[2]);
            taskManager.removeTaskById(id);
            sendGood(h);
        } else {
            throw new NotFoundException("Wrong path");
        }
    }

}