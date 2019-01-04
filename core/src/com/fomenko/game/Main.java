package com.fomenko.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fomenko.game.States.GameStart;

public class Main extends ApplicationAdapter {
	private SpriteBatch sb;
	private GameStateManager gsm;
	public static float HEIGHT = 720, WIDTH = 400;
	public static final String address = "37.57.150.136";
	public static final int port = 5000;
	
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

		gsm.push(new GameStart(gsm));
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
	}
}
