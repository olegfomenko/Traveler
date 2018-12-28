package com.fomenko.game.Game;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Ball extends GameObject {
    private int index;
    public static final long DELTA = 5000;
    private static int last = 0;

    public Ball(float x, float y, int index, int direction) {
        super(x, y, 15, 15, direction,180);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public synchronized void update(float dt, ArrayList<Wall> walls) {
        for(Wall w : walls) {
            if(new Rectangle(getX(), getY(), getWidth(), getHeight()).overlaps(new Rectangle(w.getX(), w.getY(), w.getWidth(), w.getHeight()))) {
                switch (getDirection()) {
                    case 1: setDirection(2); break;
                    case 2: setDirection(1); break;
                    case 3: setDirection(4); break;
                    case 4: setDirection(3); break;
                }
                break;
            }
        }

        super.update(dt);
    }
}