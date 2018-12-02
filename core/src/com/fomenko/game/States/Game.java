package com.fomenko.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.fomenko.game.Game.Tank;
import com.fomenko.game.GameStateManager;
import com.fomenko.game.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;


public class Game extends State {
    private Tank tank;
    private volatile HashMap<Integer,  Tank> tanks;
    boolean pressed;
    private float x, y;
    private float minDY = 20, minDX = 20, needDY = 50, needDX = 50;
    public static final float FIELD_WIDTH = 3000, FIELD_HEIGHT = 3000;
    private DatagramSocket socket;

    private int index;

    private Handler handler;

    public Game(GameStateManager gsm, DatagramSocket socket, HashMap<Integer, Tank> tanks, Tank tank) {
        super(gsm);
        this.socket = socket;
        this.tanks = tanks;
        this.tank = tank;

        background = new Texture("back.png");
        pressed = false;

        handler = new Handler(tanks, socket);

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
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type", "UPDATE");
                            obj.put("index", tank.getIndex());
                            obj.put("direction", 4);
                            handler.send(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type", "UPDATE");
                            obj.put("index", tank.getIndex());
                            obj.put("direction", 3);
                            handler.send(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                 }

                if(dy >= needDY && dy > dx) {
                    if(y > curY) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type", "UPDATE");
                            obj.put("index", tank.getIndex());
                            obj.put("direction", 2);
                            handler.send(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type", "UPDATE");
                            obj.put("index", tank.getIndex());
                            obj.put("direction", 1);
                            handler.send(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);

        sb.begin();
        sb.draw(background, 0, 0, FIELD_WIDTH, FIELD_HEIGHT);
        for (Tank t : tanks.values()) {
            t.render(sb);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        for(Tank t : tanks.values()) t.dispose();
        //handler.send("STOP");
    }
}
