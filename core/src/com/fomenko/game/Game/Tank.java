package com.fomenko.game.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Tank {
    private volatile float x, y;
    public static float width, height;
    public static final float speed = 120;
    private volatile int direction; // 1 - up   2 - down   3 - left   4 - right

    private int index;

    public Tank(float x, float y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;

        width = 64;
        height = 64;

        direction = 1;

        //new Thread(new RandomUpdate(this)).start();
    }

    public synchronized int getDirection() {
        return direction;
    }

    public synchronized void setDirection(int direction) {
        this.direction = direction;
    }

    public synchronized float getX() {
        return x;
    }

    public synchronized float getY() {
        return y;
    }

    public synchronized void setX(float x) {
        this.x = x;
    }

    public synchronized void setY(float y) {
        this.y = y;
    }

    public synchronized float getWidth() {
        return width;
    }

    public synchronized float getHeight() {
        return height;
    }

    public int getIndex() {
        return index;
    }


    public synchronized void update(float dt, ArrayList<Wall> walls) {
        for(Wall w : walls) {
            if(new Rectangle(x, y, width, height).overlaps(new Rectangle(w.getX(), w.getY(), w.getWidth(), w.getHeight()))) {
                switch (direction) {
                    case 1: direction = 2; break;
                    case 2: direction = 1; break;
                    case 3: direction = 4; break;
                    case 4: direction = 3; break;
                }
                break;
            }
        }

        synchronized (this) {
            if(direction == 1) y += speed * dt;
            if(direction == 2) y -= speed * dt;
            if(direction == 3) x -= speed * dt;
            if(direction == 4) x += speed * dt;
        }
    }
}
