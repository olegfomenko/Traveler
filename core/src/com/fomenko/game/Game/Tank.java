package com.fomenko.game.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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


    public synchronized void update(float dt) {
        synchronized (this) {
            if(direction == 1) y += speed * dt;
            if(direction == 2) y -= speed * dt;
            if(direction == 3) x -= speed * dt;
            if(direction == 4) x += speed * dt;
        }
    }

   /* public synchronized void render(SpriteBatch sb) {
        switch (direction) {
            case 1: sb.draw(tank1, x, y, width, height); break;
            case 2: sb.draw(tank2, x, y, width, height); break;
            case 3: sb.draw(tank3, x, y, width, height); break;
            case 4: sb.draw(tank4, x, y, width, height); break;
        }
    }

    public void dispose() {
        tank1.dispose();
        tank2.dispose();
        tank3.dispose();
        tank4.dispose();
    }*/
}
