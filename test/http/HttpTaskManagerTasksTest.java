package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
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

public class HttpTaskManagerTasksTest {
    TaskManager manager;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client;

    public HttpTaskManagerTasksTest() {
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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        System.out.println("Sending JSON" + taskJson);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().minusDays(1));

        manager.addTask(task);
        manager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");


        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код");

        Task[] tasksFromServer = gson.fromJson(response.body(), Task[].class);

        assertEquals(2, tasksFromServer.length, "Некорректное количество задач");
        assertEquals("Testing task", tasksFromServer[0].getDescription(), "Некорректное описание");

    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);


        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> postResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, postResponse.statusCode());

        List<Task> tasks = manager.getAllTasks();
        int id = tasks.getFirst().getId();

        HttpResponse<String> getResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + id))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());



        assertEquals(200, getResponse.statusCode());

        Task fromGet = gson.fromJson(getResponse.body(), Task.class);

        assertEquals(id, fromGet.getId());
        assertEquals("Test 2", fromGet.getName());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task existing = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(existing);

        Task task = new Task("New", "Testing task 2",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.now());
        task.setId(existing.getId());
        String taskJson = gson.toJson(task);


        URI url = URI.create("http://localhost:8080/tasks");

        HttpResponse<String> postResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, postResponse.statusCode());


        Task fromManager = manager.getTaskByID(task.getId());

        assertEquals(1, manager.getAllTasks().size());

        assertEquals("New", fromManager.getName(), "Имя не обновилось");
        assertEquals(TaskStatus.IN_PROGRESS, fromManager.getStatus(), "Статус не обновился");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {

        Task existing = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(existing);

        URI url = URI.create("http://localhost:8080/tasks/"+existing.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllTasks().size());


    }
    
    




}
