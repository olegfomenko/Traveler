package com.fomenko.game;
import com.fomenko.game.Game.Tank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


public class Handler {
    private volatile HashMap<Integer, Tank> tanks;
    private volatile LinkedList<Integer> codes;
    private DatagramSocket socket;
    private volatile static int check_code = 0;

    public Handler(HashMap<Integer, Tank> tanks, DatagramSocket socket) {
        this.tanks = tanks;
        this.socket = socket;
        codes = new LinkedList<Integer>();
        new Thread(new Get()).start();

    }

    public void send(JSONObject request) {
        try {
            request.put("check_code", check_code = (int)(Math.random() * 1000000000));
            //System.out.println("GENERATED = " + check_code);
            /*synchronized (codes) {
                codes.add(check_code);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Send(request)).start();
    }

    class Get implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (tanks) {
                    try {
                        byte[] buffer = new byte[1000];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        socket.receive(packet);

                        buffer = packet.getData();
                        int last = 0;
                        for (; last < buffer.length; ++last) if (buffer[last] == 0) break;

                        String request = new String(buffer, 0, last);

                        JSONObject obj = new JSONObject(request);

                        //System.out.println(codes + "                   CHECK = " + check_code + "   REQUES_CHECK = " + obj.getInt("check_code"));

                       /* if(!codes.contains(obj.getInt("check_code"))) continue;
                        while (true) {
                            if(codes.getFirst() == obj.getInt("check_code")) {
                                codes.removeFirst();
                                break;
                            }

                            if(codes.getFirst() < obj.getInt("check_code")) {
                                codes.removeFirst();
                            } else break;
                        }*/

                        //while (pq.size() > 0 && pq.peek() <= obj.getInt("check_code")) pq.remove();

                        if(obj.getString("type").equals("UPDATE")) {
                            if(check_code != obj.getInt("check_code")) continue;

                            JSONArray arr = obj.getJSONArray("TANKS");

                            for(int i = 0; i < arr.length(); ++i) {
                                obj = arr.getJSONObject(i);
                                Tank tank = tanks.get(obj.getInt("index"));
                                tank.setX(Float.parseFloat(obj.getString("x")));
                                tank.setY(Float.parseFloat(obj.getString("y")));
                                tank.setDirection(obj.getInt("direction"));
                            }
                        } else if(obj.getString("type").equals("ADD")) {
                            Tank tank = new Tank(Float.parseFloat(obj.getString("x")), Float.parseFloat(obj.getString("y")), obj.getInt("index"));
                            tank.setDirection(obj.getInt("direction"));
                            tanks.put(obj.getInt("index"), tank);
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
