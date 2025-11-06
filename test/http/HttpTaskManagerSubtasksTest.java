package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubtasksTest {

    TaskManager manager;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client;

    public HttpTaskManagerSubtasksTest() {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        int epicId = epic1.getId();

        Subtask subtask = new Subtask("S1", "D1", epicId);
        subtask.setStatus(TaskStatus.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());

        String json = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subs = manager.getSubtasksByEpic(epicId);

        assertNotNull(subs, "Задачи не возвращаются");
        assertEquals(1, subs.size(), "Некорректное количество задач");
        assertEquals("S1", subs.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        int epicId = epic1.getId();

        Subtask subtask1 = new Subtask("S1", "D1", epicId);
        Subtask subtask2 = new Subtask("S2", "D2", epicId);
        Subtask subtask3 = new Subtask("S3", "D3", epicId);

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код");

        Subtask[] subtasksFromServer = gson.fromJson(response.body(), Subtask[].class);

        assertEquals(3, subtasksFromServer.length, "Некорректное количество задач");
        assertEquals("S2", subtasksFromServer[1].getName());


    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        int epicId = epic1.getId();

        Subtask subtask1 = new Subtask("S1", "D1", epicId);
        Subtask subtask2 = new Subtask("S2", "D2", epicId);
        Subtask subtask3 = new Subtask("S3", "D3", epicId);

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/subtasks/"+subtask3.getId());
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonObject jo = gson.fromJson(response.body(), JsonObject.class);
        assertNotNull(jo);

        assertEquals(subtask3.getId(), jo.get("id").getAsInt());
        assertEquals("S3", jo.get("name").getAsString());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        int epicId = epic1.getId();

        Subtask subtask = new Subtask("S1", "D1", epicId);
        subtask.setStatus(TaskStatus.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());

        manager.addSubtask(subtask);

        Subtask subtaskNew = new Subtask("New", "NewDesc", epicId);
        subtaskNew.setId(subtask.getId());
        subtaskNew.setStatus(TaskStatus.IN_PROGRESS);

        String json = gson.toJson(subtaskNew);


        URI url = URI.create("http://localhost:8080/subtasks");

        HttpResponse<String> postResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, postResponse.statusCode());


        Subtask fromManager = manager.getSubtaskByID(subtask.getId());

        assertEquals(1, manager.getAllSubtasks().size());

        assertEquals("New", fromManager.getName(), "Имя не обновилось");
        assertEquals(TaskStatus.IN_PROGRESS, fromManager.getStatus(), "Статус не обновился");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        int epicId = epic1.getId();

        Subtask subtask1 = new Subtask("S1", "D1", epicId);
        manager.addSubtask(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllSubtasks().size());

    }
}
