package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendSuccess(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201,-1);
        h.close();
    }

    protected void sendGood(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200,-1);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404,-1);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406,-1);
        h.close();
    }

    protected void sendServerError(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500,-1);
        h.close();
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(400,-1);
        h.close();
    }

    protected void handleException(HttpExchange h, Exception e) throws IOException{
        if (e instanceof manager.exceptions.NotFoundException) {
            sendNotFound(h);
        } else if (e instanceof IllegalStateException) {
            sendHasInteractions(h);
        } else if (e instanceof IllegalArgumentException
                || e instanceof com.google.gson.JsonSyntaxException) {
            sendBadRequest(h);
        }
        sendServerError(h);
    }
}
