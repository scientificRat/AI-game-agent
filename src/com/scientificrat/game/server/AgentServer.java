package com.scientificrat.game.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scientificrat.game.datastruct.JsonResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class AgentServer extends SimpleHttpServer {

    private ConcurrentHashMap<Integer, onCommandListener> onCommandListeners = new ConcurrentHashMap<>();

    public AgentServer(int port) throws IOException {
        super(port);
    }

    @Override
    public void start() {
        Gson gson = new Gson();
        super.get("/", body -> gson.toJson(new JsonResponse<>(403, "GET METHOD IS NOT PERMITTED")));
        super.post("/", body -> {
            JsonObject requestObject = new JsonParser().parse(new InputStreamReader(body)).getAsJsonObject();
            JsonElement command = requestObject.get("command");
            JsonElement data = requestObject.get("data");
            if (command == null || data == null) {
                return gson.toJson(new JsonResponse<>(400, "bad request"));
            }
            onCommandListener listener = onCommandListeners.getOrDefault(command.getAsInt(), null);
            if (listener == null) {
                return gson.toJson(new JsonResponse<>(500, "command not implement"));
            } else {
                return listener.onCommand(command.getAsInt(), data);
            }
        });
        super.start();
    }

    public AgentServer onCommand(int command, onCommandListener listener) {
        onCommandListeners.put(command, listener);
        return this;
    }

    public interface onCommandListener {
        String onCommand(int command, JsonElement data) throws InterruptedException, ExecutionException, TimeoutException;
    }
}
