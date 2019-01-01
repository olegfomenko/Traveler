package com.fomenko.game.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fomenko.game.GameStateManager;

import static com.fomenko.game.Main.HEIGHT;
import static com.fomenko.game.Main.WIDTH;
import static com.fomenko.game.States.Game.FIELD_HEIGHT;
import static com.fomenko.game.States.Game.FIELD_WIDTH;


public class State {
    protected GameStateManager gsm;
    protected Texture background;
    protected OrthographicCamera camera;

    public State(GameStateManager gsm) {
        this.gsm = gsm;

        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.position.set(FIELD_WIDTH / 2, FIELD_HEIGHT / 2, 0);
        camera.update();
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch sb) {

    }

    public void dispose() {

    }
}
