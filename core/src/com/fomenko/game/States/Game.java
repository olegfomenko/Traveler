package com.fomenko.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.fomenko.game.Game.Tank;
import com.fomenko.game.Game.Wall;
import com.fomenko.game.GameStateManager;
import com.fomenko.game.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;


public class Game extends State {
    private Tank tank;
    private Texture tank1, tank2, tank3, tank4;
    private Texture wall;

    private volatile HashMap<Integer,  Tank> tanks;
    private ArrayList<Wall> walls;
    private boolean pressed;
    private float x, y;
    private float minDY = 20, minDX = 20, needDY = 50, needDX = 50;
    public static final float FIELD_WIDTH = 3000, FIELD_HEIGHT = 3000;
    private DatagramSocket socket;

    private Handler handler;

    public Game(GameStateManager gsm, DatagramSocket socket, HashMap<Integer, Tank> tanks, Tank tank, ArrayList<Wall> walls) {
        super(gsm);
        this.socket = socket;
        this.tanks = tanks;
        this.tank = tank;
        this.walls = walls;

        background = new Texture("back.png");
        pressed = false;

        handler = new Handler(tanks, socket);

        tank1 = new Texture("upTank.png");
        tank2 = new Texture("downTank.png");
        tank3 = new Texture("leftTank.png");
        tank4 = new Texture("rightTank.png");

        wall = new Texture("wall.png");
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.isTouched()) {
            float curX = Gdx.input.getX(), curY = Gdx.input.getY();

            Vector3 v = new Vector3(curX, curY, 0);
            camera.unproject(v);
            curX = v.x;
            curY = v.y;

            if(pressed) {
                float dx = Math.abs(curX - x);
                float dy = Math.abs(curY - y);

                if(dx >= needDX && dx > dy) {
                    if(x < curX) {
                        changeDirection(4);
                    } else {
                        changeDirection(3);
                    }
                 }

                if(dy >= needDY && dy > dx) {
                    if(y > curY) {
                        changeDirection(2);
                    } else {
                        changeDirection(1);
                    }
                }

            } else {
                pressed = true;
                x = curX;
                y = curY;
            }

        } else {
            pressed = false;
        }

        synchronized (tanks) {
            ArrayList<Tank> lt = new ArrayList<Tank>(tanks.values());
            for(int i = 0; i < lt.size(); ++i) {
                lt.get(i).update(dt, walls);
            }
        }

        try {
            JSONObject request = new JSONObject();
            request.put("type", "GET");
            request.put("index", tank.getIndex());
            handler.send(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        camera.position.set(tank.getX() + tank.getWidth() / 2, tank.getY() + tank.getHeight() / 2, 0);
        camera.update();
    }

    private void changeDirection(int direction) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "UPDATE");
            obj.put("index", tank.getIndex());
            obj.put("direction", direction);
            handler.send(obj);

            tank.setDirection(direction);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);

        sb.begin();
        sb.draw(background, 0, 0, FIELD_WIDTH, FIELD_HEIGHT);

        synchronized (tanks) {
            ArrayList<Tank> lt = new ArrayList<Tank>(tanks.values());
            for(int i = 0; i < lt.size(); ++i) {
                drawTank(lt.get(i), sb);
            }
        }

        for (Wall w : walls) drawWall(w, sb);

        sb.end();
    }

    private void drawTank(Tank t, SpriteBatch sb) {
        synchronized (t) {
            switch (t.getDirection()) {
                case 1: sb.draw(tank1, t.getX(), t.getY(), t.width, t.height); break;
                case 2: sb.draw(tank2, t.getX(), t.getY(), t.width, t.height); break;
                case 3: sb.draw(tank3, t.getX(), t.getY(), t.width, t.height); break;
                case 4: sb.draw(tank4, t.getX(), t.getY(), t.width, t.height); break;
            }
        }
    }

    private void drawWall(Wall w, SpriteBatch sb) {
        sb.draw(wall, w.getX(), w.getY(), w.getWidth(), w.getHeight());
    }

    @Override
    public void dispose() {
        background.dispose();
        //for(Tank t : tanks.values()) t.dispose();
        //handler.send("STOP");

        tank1.dispose();
        tank2.dispose();
        tank3.dispose();
        tank4.dispose();
    }
}
