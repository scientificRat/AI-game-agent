package com.scientificrat.game.agent;

import com.scientificrat.game.datastruct.Position;

import java.util.List;

public class RetroSnakeAgent {
    public boolean start(int width, int height) {
        System.out.println("start");
        return false;
    }

    public int takeAction(int currDirection, List<Position> snake, Position food) {
        System.out.println(food);
        return -1;
    }
}
