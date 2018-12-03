package com.fomenko.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fomenko.game.States.State;

import java.util.EmptyStackException;
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
        try {
            stack.peek().update(dt);
        } catch (EmptyStackException e) {
            e.printStackTrace();
        }
    }

    public void render(SpriteBatch sb) {
        try {
            stack.peek().render(sb);
        } catch (EmptyStackException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        while (!stack.empty()) stack.pop().dispose();
    }
}
