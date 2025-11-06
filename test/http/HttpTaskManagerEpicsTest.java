package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest {
    TaskManager manager;
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer;
    Gson gson;
    HttpClient client;

    public HttpTaskManagerEpicsTest() {
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
    public void testAddEpic() throws IOException, InterruptedException {

        JsonObject jo = new JsonObject();
        jo.addProperty("name", "Name");
        jo.addProperty("description", "Description");

        String json = gson.toJson(jo);
        System.out.println("Sending JSON" + json);


        URI url = URI.create("http://localhost:8080/epics");

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
        ArrayList<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Name", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        Epic epic2 = new Epic("Name2", "Description2");

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код");

        Epic[] epicsFromServer = gson.fromJson(response.body(), Epic[].class);

        assertEquals(2, epicsFromServer.length, "Некорректное количество задач");
        assertEquals("Name1", epicsFromServer[0].getName(), "Некорректное количество задач");

    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        epic1.setId(5);

        manager.addEpic(epic1);

        URI url = URI.create("http://localhost:8080/epics/"+5);
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic fromGet = gson.fromJson(response.body(), Epic.class);

        assertEquals(5, fromGet.getId());
        assertEquals("Name1", fromGet.getName());
    }



    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {

        Epic epic1 = new Epic("Name1", "Description1");
        Epic epic2 = new Epic("Name2", "Description2");
        epic1.setId(5);

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getAllEpics().size());

    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");
        manager.addEpic(epic1);

        Subtask sb1 = new Subtask("N1", "D1", epic1.getId());
        Subtask sb2 = new Subtask("N2", "D2", epic1.getId());

        manager.addSubtask(sb1);
        manager.addSubtask(sb2);

        URI url = URI.create("http://localhost:8080/epics/"+epic1.getId()+"/subtasks");
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonArray subArr = gson.fromJson(response.body(), JsonArray.class);

        assertNotNull(subArr);
        assertEquals(2, subArr.size());


        JsonObject secondSub = subArr.get(1).getAsJsonObject();
        assertEquals("N2", secondSub.get("name").getAsString());


    }
}
