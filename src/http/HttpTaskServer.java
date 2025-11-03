package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = Managers.getDefault();
        this.gson = buildGson();
        server.createContext("/tasks", new TaskHandler(manager, gson));
    }

    public void start() {
        server.start();
        System.out.println("Server started, port: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped, port: " + PORT);
    }

    private static Gson buildGson() {
        return TimeAdapters
                .bothAdapters(
                        new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting())
                .create();
    }


    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }
}
