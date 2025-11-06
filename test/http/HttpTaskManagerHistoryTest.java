package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerHistoryTest {

    TaskManager manager;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client;

    public HttpTaskManagerHistoryTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();

        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        Epic epic = new Epic("Epic1", "Description1");
        manager.addEpic(epic);

        Subtask sb = new Subtask("S1", "D1", epic.getId());
        manager.addSubtask(sb);


        manager.getEpicByID(epic.getId());
        manager.getSubtaskByID(sb.getId());
        manager.getTaskByID(task.getId());


        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonArray arr = gson.fromJson(response.body(), JsonArray.class);

        assertNotNull(arr, "История не возвращается");
        assertEquals(3, arr.size(), "Некорректное количество элементов в истории");

        assertEquals("S1", arr.get(1).getAsJsonObject().get("name").getAsString());

    }
}
