package com.fomenko.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.fomenko.game.Buttons.Button;
import com.fomenko.game.GameStateManager;

public class GameOver extends State {

    private Texture lose;
    private Button mainButton;
    private boolean pressed;
    private float x, y;

    public GameOver(GameStateManager gsm) {
        super(gsm);

        background = new Texture("bg.png");
        pressed = false;

        mainButton = new Button(new Texture("mainButton.png"),
                new Texture("mainButton_press.png"),
                null,
                360, 90, Game.FIELD_WIDTH / 2 - 180, Game.FIELD_HEIGHT / 2 - 45);

        lose = new Texture("lose.png");
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
            mainButton.checkPress(input);

        } else {

            if(pressed) {
                if(mainButton.isPress()) {
                    mainButton.setPress(false);
                    gsm.pop();
                }
            }

            pressed = false;
        }
    }


    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        sb.draw(background, 0, 0, Game.FIELD_WIDTH, Game.FIELD_HEIGHT);
        sb.draw(lose, Game.FIELD_WIDTH / 2 - 180, Game.FIELD_HEIGHT / 2 + 90, 360, 90);
        mainButton.render(sb);

        sb.end();
    }

    @Override
    public void dispose() {
        mainButton.dispose();
        lose.dispose();
    }
}
