package com.fomenko.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.fomenko.game.Game.Ball;
import com.fomenko.game.Game.Tank;
import com.fomenko.game.Game.Wall;
import com.fomenko.game.States.Game;

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

public class Main extends ApplicationAdapter {
	private SpriteBatch sb;
	private GameStateManager gsm;
	public static float HEIGHT = 720, WIDTH = 400;
	public static String address = "192.168.0.100";
	public static final int port = 5000;
	private DatagramSocket socket;
	
	@Override
	public void create () {
		sb = new SpriteBatch();
		gsm = new GameStateManager();

		/*масштабирование*/
		HEIGHT = (float)Gdx.graphics.getHeight();
		WIDTH = (float)Gdx.graphics.getWidth();

		float k = HEIGHT / 850;

		HEIGHT /= k;
		WIDTH /= k;

		try {
			socket = new DatagramSocket(port);
			JSONObject obj = new JSONObject();
			obj.put("type", "CREATE");
			byte[] buffer = obj.toString().getBytes();

			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(address), port);
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
	public void render () {
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(sb);
	}
	
	@Override
	public void dispose () {
		sb.dispose();
		gsm.dispose();
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
