package com.fomenko.game.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tank {
    private Texture tank1, tank2, tank3, tank4;
    private volatile float width, height, x, y;
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

        tank1 = new Texture("upTank.png");
        tank2 = new Texture("downTank.png");
        tank3 = new Texture("leftTank.png");
        tank4 = new Texture("rightTank.png");

        //new Thread(new RandomUpdate(this)).start();
    }

    public int getDirection() {
        return direction;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public synchronized void setX(float x) {
        this.x = x;
    }

    public synchronized void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getIndex() {
        return index;
    }

    public synchronized void setDirection(int direction) {
        this.direction = direction;
    }

    public void update(float dt) {
        if(direction == 1) y += speed * dt / 1000.0;
        if(direction == 2) y -= speed * dt / 1000.0;
        if(direction == 3) x -= speed * dt / 1000.0;
        if(direction == 4) x += speed * dt / 1000.0;
    }

    public synchronized void render(SpriteBatch sb) {
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
    }


    class RandomUpdate implements Runnable {
        private Tank tank;
        private long last = System.currentTimeMillis(), cur, dt;

        public RandomUpdate(Tank tank) {
            this.tank = tank;
        }

        @Override
        public void run() {
            while(true) {
                synchronized(tank) {
                    //System.out.println(direction);
                    cur = System.currentTimeMillis();
                    dt = cur - last;

                    last = cur;

                    //System.out.println(direction);

                    if(direction == 1) y += speed * dt / 1000.0;
                    if(direction == 2) y -= speed * dt / 1000.0;
                    if(direction == 3) x -= speed * dt / 1000.0;
                    if(direction == 4) x += speed * dt / 1000.0;
                }
            }
        }
    }
}
