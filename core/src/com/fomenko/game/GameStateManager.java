package com.fomenko.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fomenko.game.States.State;

import java.util.Stack;

public class GameStateManager {
    private Stack<State> stack;

    public GameStateManager() {
        stack = new Stack<State>();
    }

    public void push(State state) {
        stack.push(state);
    }

    public void remove() {
        stack.pop();
    }

    public void pop() {
        stack.peek().dispose();
        stack.pop();
    }

    public void update(float dt) {
        stack.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        stack.peek().render(sb);
    }

    public void dispose() {
        while (!stack.empty()) stack.pop().dispose();
    }
}
