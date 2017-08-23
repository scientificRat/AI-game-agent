package com.scientificrat.game.server.retrosnake;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scientificrat.game.agent.RetroSnakeAgent;
import com.scientificrat.game.datastruct.JsonResponse;
import com.scientificrat.game.framework.UserCodeExecutor;
import com.scientificrat.game.http.SimpleHttpServer;

import java.io.IOException;
import java.io.InputStreamReader;

public class RetroSnakeGameAgentServer extends SimpleHttpServer {

    public RetroSnakeGameAgentServer(int port) throws IOException {
        super(port);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Parameter wrong, port number is required");
            return;
        }
        RetroSnakeGameAgentServer server;
        try {
            server = new RetroSnakeGameAgentServer(Integer.parseInt(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        server.get("/", body -> "GET METHOD IS NOT PERMITTED!");
        server.post("/", body -> {
            Gson gson = new Gson();
            JsonObject requestObject = new JsonParser().parse(new InputStreamReader(body)).getAsJsonObject();
            JsonElement command = requestObject.get("command");
            JsonElement data = requestObject.get("data");
            if (command == null || data == null) {
                return "error";
            }
            RetroSnakeAgent agent = new RetroSnakeAgent();
            switch (command.getAsInt()) {
                case 0:
                    // game start
                    int width = data.getAsJsonObject().get("width").getAsInt();
                    int height = data.getAsJsonObject().get("height").getAsInt();
                    // execute user code in time limit
                    boolean ready = UserCodeExecutor.runMethod(2000, () -> agent.start(width, height));
                    return gson.toJson(new JsonResponse<>(0, ready));
                case 1:
                    // send back action
                    GameState gameState = gson.fromJson(data, GameState.class);
                    // execute user code in time limit
                    int action = UserCodeExecutor.runMethod(2000, () ->
                            agent.takeAction(gameState.getCurrDirection(), gameState.getSnake(), gameState.getFoodPosition()
                            ));
                    return gson.toJson(new JsonResponse<>(0, action));
                default:
                    return gson.toJson(new JsonResponse<>(-1, "not implemented"));
            }
        });
        // start the server
        server.start();
    }
}
