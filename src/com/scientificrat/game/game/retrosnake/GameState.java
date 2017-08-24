package com.scientificrat.game.game.retrosnake;

import com.scientificrat.game.datastruct.Position;

import java.util.List;

public class GameState {
    private int currDirection;
    private List<Position> snake;
    private Position foodPosition;

    public int getCurrDirection() {
        return currDirection;
    }

    public GameState setCurrDirection(int currDirection) {
        this.currDirection = currDirection;
        return this;
    }

    public List<Position> getSnake() {
        return snake;
    }

    public GameState setSnake(List<Position> snake) {
        this.snake = snake;
        return this;
    }

    public Position getFoodPosition() {
        return foodPosition;
    }

    public GameState setFoodPosition(Position foodPosition) {
        this.foodPosition = foodPosition;
        return this;
    }
}
