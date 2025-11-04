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

public class HttpTaskManagerPrioritizedTest {
    TaskManager manager;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client;

    public HttpTaskManagerPrioritizedTest() {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task middle = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(middle);

        Task early = new Task("Test2", "Testing task2",
                TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().minusDays(1));
        manager.addTask(early);

        Task middle2 = new Task("Middle2", "Testing task3",
                TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.now().plusHours(2));
        manager.addTask(middle2);

        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        Subtask latest = new Subtask("N1", "D1", epic1.getId(), TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(5));
        manager.addSubtask(latest);

        URI url = URI.create("http://localhost:8080/tasks/prioritized");
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonArray arr = gson.fromJson(response.body(), JsonArray.class);

        assertNotNull(arr, "Список не возвращается");
        assertEquals(4, arr.size(), "Некорректное количество элементов в истории");

        assertEquals("Middle2", arr.get(2).getAsJsonObject().get("name").getAsString());

    }
}
