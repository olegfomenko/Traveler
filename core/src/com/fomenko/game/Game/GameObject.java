package com.fomenko.game.Game;


import com.badlogic.gdx.math.Rectangle;

public class GameObject {
    private volatile float x, y;
    private volatile int direction; // 1 - up   2 - down   3 - left   4 - right
    private volatile float width, height;
    private float speed;

    public static final float EPS = 3.0f;

    public GameObject(float x, float y, float width, float height, int direction, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.speed = speed;
    }

    public synchronized Rectangle getRectangle() {
        return new Rectangle(x, y, width, height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public synchronized void setX(float x) {
        if(Math.abs(this.x - x) > EPS) this.x = x;
    }

    public synchronized void setY(float y) {
        if(Math.abs(this.y - y) > EPS) this.y = y;
    }

    public synchronized float getX() {
        return x;
    }

    public synchronized float getY() {
        return y;
    }

    public synchronized int getDirection() {
        return direction;
    }

    public synchronized void setDirection(int direction) {
        this.direction = direction;
    }

    public synchronized void update(float dt) {
        if(direction == 1) y += speed * dt;
        if(direction == 2) y -= speed * dt;
        if(direction == 3) x -= speed * dt;
        if(direction == 4) x += speed * dt;
    }
}

