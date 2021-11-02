package com.layhill.roadsim.gameengine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static final  int MAX_NUM_OF_KEYS_SUPPORTED = 350;
    private static final class KeyListenerHolder {
        private static final KeyListener keyListener = new KeyListener();
    }

    private boolean keyPressed[] = new boolean[MAX_NUM_OF_KEYS_SUPPORTED];

    private KeyListener() {
    }

    public static KeyListener getInstance() {
        return KeyListenerHolder.keyListener;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int modifier){
        if (key >= MAX_NUM_OF_KEYS_SUPPORTED){
            return;
        }
        switch(action){
            case GLFW_PRESS -> getInstance().keyPressed[key] = true;
            case GLFW_RELEASE -> getInstance().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed (int key){
        if (key >= MAX_NUM_OF_KEYS_SUPPORTED){
            return false;
        }
        return getInstance().keyPressed[key];
    }

}
