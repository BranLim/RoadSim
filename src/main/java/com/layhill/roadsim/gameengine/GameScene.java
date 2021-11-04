package com.layhill.roadsim.gameengine;

import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;

public class GameScene extends Scene {

    @Override
    public void init() {
        System.out.println("Game Scene Loaded");
    }

    @Override
    public void update(double deltaTime) {
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            Window.getInstance().changeScene(0);
        }
    }
}
