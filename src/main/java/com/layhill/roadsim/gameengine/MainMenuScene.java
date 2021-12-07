package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.input.KeyListener;
import org.lwjgl.glfw.GLFW;

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
