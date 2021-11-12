package com.layhill.roadsim.gameengine;

import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;

public class MainMenuScene extends Scene{

    @Override
    public void init() {
        System.out.println("Main Menu Scene Loaded");
    }

    @Override
    public void update(float deltaTime) {
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT) ){
            Window.getInstance().changeScene(1);
        }
    }

    @Override
    public void cleanUp() {

    }
}
