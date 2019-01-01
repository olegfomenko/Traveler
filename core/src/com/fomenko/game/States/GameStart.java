package com.fomenko.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.fomenko.game.Buttons.Button;
import com.fomenko.game.Game.Ball;
import com.fomenko.game.Game.Tank;
import com.fomenko.game.Game.Wall;
import com.fomenko.game.GameStateManager;
import com.fomenko.game.Handler;
import com.fomenko.game.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameStart extends State {

    private Texture tank;
    private Button startButton;
    private boolean pressed;
    private float x, y;

    public GameStart(GameStateManager gsm) {
        super(gsm);

        background = new Texture("bg.png");
        pressed = false;

        startButton = new Button(new Texture("startButton.png"),
                                    new Texture("startButton_press.png"),
                                    null,
                                    360, 90, Game.FIELD_WIDTH / 2 - 180, Game.FIELD_HEIGHT / 2 - 45);

        tank = new Texture("upwTank.png");
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.isTouched()) {
            float curX = Gdx.input.getX(), curY = Gdx.input.getY();

            Vector3 v = new Vector3(curX, curY, 0);
            camera.unproject(v);
            curX = v.x;
            curY = v.y;

            pressed = true;

            x = curX;
            y = curY;

            Rectangle input = new Rectangle(x, y, 1, 1);
            startButton.checkPress(input);

        } else {

            if(pressed) {
                if(startButton.isPress()) {
                    startButton.setPress(false);
                    createGame();
                }
            }

            pressed = false;
        }
    }


    public void createGame() {
        try {
            DatagramSocket socket = Handler.createConnection(Main.port);
            JSONObject obj = new JSONObject();
            obj.put("type", "CREATE");
            byte[] buffer = obj.toString().getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(Main.address), Main.port);
            socket.send(packet);

            buffer = new byte[1000000];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            int last = 0;
            for(; last < buffer.length; ++last) if(buffer[last] == 0) break;

            String s = new String(buffer, 0, last);
            obj = new JSONObject(s);

            int index = obj.getInt("i");
            JSONArray arr = obj.getJSONArray("TANKS");
            HashMap<Integer, Tank> tanks = new HashMap<Integer, Tank>();
            Tank t = null;

            for(int i = 0; i < arr.length(); ++i) {
                synchronized (tanks) {
                    obj = arr.getJSONObject(i);
                    Tank tank = new Tank(Float.parseFloat(obj.getString("x")),
                            Float.parseFloat(obj.getString("y")),
                            obj.getInt("i"), obj.getInt("d"));

                    tanks.put(obj.getInt("i"), tank);

                    if(obj.getInt("i") == index) {
                        t = tank;
                    }
                }
            }

            ArrayList<Wall> walls = new ArrayList<Wall>();
            arr = new JSONObject(s).getJSONArray("WALLS");

            for(int i = 0; i < arr.length(); ++i) {
                obj = arr.getJSONObject(i);
                walls.add(new Wall(Float.parseFloat(obj.getString("x")),
                        Float.parseFloat(obj.getString("y")),
                        Float.parseFloat(obj.getString("w")),
                        Float.parseFloat(obj.getString("h"))));
            }

            HashMap<Integer, Ball> balls = new HashMap<Integer, Ball>();
            arr = new JSONObject(s).getJSONArray("BALLS");

            for(int i = 0; i < arr.length(); ++i) {
                obj = arr.getJSONObject(i);
                Ball ball = new Ball(Float.parseFloat(obj.getString("x")),
                        Float.parseFloat(obj.getString("y")),
                        obj.getInt("i"),
                        obj.getInt("d"));

                balls.put(obj.getInt("i"), ball);
            }

            gsm.push(new Game(gsm, socket, tanks, balls, t, walls));

            System.out.print("SESSION CREATED!");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        sb.draw(background, 0, 0, Game.FIELD_WIDTH, Game.FIELD_HEIGHT);
        sb.draw(tank, Game.FIELD_WIDTH / 2 - 40, Game.FIELD_HEIGHT / 2 + 100, 80, 80);
        startButton.render(sb);

        sb.end();
    }

    @Override
    public void dispose() {
        startButton.dispose();
        tank.dispose();
    }
}
