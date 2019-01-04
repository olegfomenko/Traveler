package com.fomenko.game;
import com.fomenko.game.Game.Ball;
import com.fomenko.game.Game.Tank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class Handler {
    private volatile HashMap<Integer, Tank> tanks;
    private volatile HashMap<Integer, Ball> balls;
    private DatagramSocket socket;
    private Thread getter;
    private volatile int check_code = 0, last_code = -1, last_update = -1;

    public static final int magicNumber = 1000000000;

    public static long last_got_packet = 0;

    public Handler(HashMap<Integer, Tank> tanks,HashMap<Integer, Ball> balls, DatagramSocket socket) {
        this.tanks = tanks;
        this.balls = balls;
        this.socket = socket;

        getter = new Thread(new Get());
        getter.start();
    }

    public static DatagramSocket createConnection(int port) throws SocketException {
        DatagramSocket socket = new DatagramSocket(port);
        return socket;
    }

    public void closeAll() {
        socket.close();
        getter.interrupt();
        System.out.println("Get thread has been interrupted!");
    }

    public void send(JSONObject request) {
        try {
            request.put("c", ++check_code);
            if(request.get("type").equals("UPDATE")) last_update = check_code;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new Send(request)).start();
    }

    class Get implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] buffer = new byte[1000000];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    buffer = packet.getData();
                    int last = 0;
                    for (; last < buffer.length; ++last) if (buffer[last] == 0) break;

                    String request = new String(buffer, 0, last);

                    JSONObject obj = new JSONObject(request);


                    if(obj.getString("type").equals("UPDATE")) {
                        if(last_code >= obj.getInt("c") || last_update > obj.getInt("c")) continue;
                        last_code = obj.getInt("c");

                        last_got_packet = System.currentTimeMillis();

                        JSONArray arr = obj.getJSONArray("TANKS");

                        for(int i = 0; i < arr.length(); ++i) {
                            try {
                                obj = arr.getJSONObject(i);
                                synchronized (tanks) {
                                    if(!tanks.containsKey(obj.getInt("i"))) {
                                        Tank tank = new Tank(Float.parseFloat(obj.getString("x")), Float.parseFloat(obj.getString("y")), obj.getInt("i"), obj.getInt("d"));
                                        tanks.put(obj.getInt("i"), tank);
                                    } else {
                                        Tank tank = tanks.get(obj.getInt("i"));
                                        tank.setX(Float.parseFloat(obj.getString("x")));
                                        tank.setY(Float.parseFloat(obj.getString("y")));
                                        tank.setDirection(obj.getInt("d"));
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        arr = new JSONObject(request).getJSONArray("BALLS");

                        for(int i = 0; i < arr.length(); ++i) {
                            try {
                                obj = arr.getJSONObject(i);
                                synchronized (balls) {
                                    if(!balls.containsKey(obj.getInt("i"))) {
                                        Ball ball = new Ball(Float.parseFloat(obj.getString("x")), Float.parseFloat(obj.getString("y")), obj.getInt("i"), obj.getInt("d"));
                                        balls.put(obj.getInt("i"), ball);
                                    } else {
                                        Ball ball = balls.get(obj.getInt("i"));
                                        ball.setX(Float.parseFloat(obj.getString("x")));
                                        ball.setY(Float.parseFloat(obj.getString("y")));
                                        ball.setDirection(obj.getInt("d"));
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class Send implements Runnable {
        private JSONObject request;
        public Send(JSONObject request) {
            this.request = request;
        }


        @Override
        public void run() {
            try {
                byte[] buffer = request.toString().getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(Main.address), Main.port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
