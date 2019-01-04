package com.fomenko.game.Game;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Tank extends GameObject {
    private int index;

    public Tank(float x, float y, int index, int direction) {
        super(x, y, 64, 64, direction, 120);
        this.index = index;
    }


    public int getIndex() {
        return index;
    }

    public synchronized void update(float dt, ArrayList<Wall> walls) {
        for(Wall w : walls) {
            if(getRectangle().overlaps(w.getRectangle())) {
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
