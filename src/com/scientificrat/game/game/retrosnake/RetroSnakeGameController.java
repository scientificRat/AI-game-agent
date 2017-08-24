package com.scientificrat.game.game.retrosnake;

import com.google.gson.Gson;
import com.scientificrat.game.agent.RetroSnakeAgent;
import com.scientificrat.game.datastruct.JsonResponse;
import com.scientificrat.game.framework.UserCodeExecutor;
import com.scientificrat.game.server.AgentServer;

public class RetroSnakeGameController {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Parameter wrong, port number is required and only required");
            return;
        }
        AgentServer server;
        try {
            server = new AgentServer(Integer.parseInt(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Gson gson = new Gson();
        RetroSnakeAgent agent = new RetroSnakeAgent();
        // game start
        server.onCommand(0, (command, data) -> {
            int width = data.getAsJsonObject().get("width").getAsInt();
            int height = data.getAsJsonObject().get("height").getAsInt();
            // execute user code in time limit
            boolean ready = UserCodeExecutor.runMethod(2000, () -> agent.start(width, height));
            return gson.toJson(new JsonResponse<>(0, ready));
        });
        // take action
        server.onCommand(1, (command, data) -> {
            GameState gameState = gson.fromJson(data, GameState.class);
            // execute user code in time limit
            int action = UserCodeExecutor.runMethod(2000, () ->
                    agent.takeAction(gameState.getCurrDirection(), gameState.getSnake(), gameState.getFoodPosition()
                    ));
            return gson.toJson(new JsonResponse<>(0, action));
        });
        // start the server
        server.start();
    }
}
