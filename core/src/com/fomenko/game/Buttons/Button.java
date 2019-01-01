package com.fomenko.game.Buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    private Texture button, button_press, button_pressed;
    private float width, height;
    private float x, y;
    private boolean press, pressed;

    public Button(Texture button, Texture button_press, Texture button_pressed, float width, float height, float x, float y) {
        this.button = button;
        this.button_press = button_press;
        this.button_pressed = button_pressed;

        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;
    }

    public void reposition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isPress() {
        return press;
    }

    public boolean isPressed() {
        return pressed;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public void setPress(boolean press) {
        this.press = press;
    }

    public Rectangle getButton() {
        return new Rectangle(x, y, width, height);
    }

    public void checkPress(Rectangle input) {
        if(getButton().overlaps(input)) press = true; else press = false;
    }

    public void checkPressed() {
        if(press) {
            press = false;
            pressed = !pressed;
        }
    }

    public void render(SpriteBatch sb) {
        if(press) sb.draw(button_press, x, y, width, height);
        else
        if(pressed) sb.draw(button_pressed, x, y, width, height);
        else sb.draw(button, x, y, width, height);
    }

    public void dispose() {
        button.dispose();
        button_press.dispose();

        try {
            button_pressed.dispose();
        } catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }
}
