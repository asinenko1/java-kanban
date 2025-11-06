package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;


import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    public static final int PORT = 8080;

    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = Managers.getDefault();
        this.gson = buildGson();
        server.createContext("/tasks", new TaskHandler(manager, gson));
        server.createContext("/epics", new EpicHandler(manager, gson));
        server.createContext("/subtasks", new SubtaskHandler(manager, gson));
        server.createContext("/tasks/history", new HistoryHandler(manager, gson));
        server.createContext("/tasks/prioritized", new PrioritizedHandler(manager, gson));
    }

    public static Gson getGson() {return buildGson();}

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
        TaskManager manager = new InMemoryTaskManager();
        new HttpTaskServer(manager).start();
    }
}
