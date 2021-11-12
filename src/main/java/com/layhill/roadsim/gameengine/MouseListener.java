package com.layhill.roadsim.gameengine;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {

    private double lastX = 0.0;
    private double lastY = 0.0;
    private double yPos = 0.0;
    private double xPos = 0.0;
    private double scrollY = 0.0;
    private double scrollX = 0.0;
    private boolean isDragging = false;
    private boolean hasMouseEntered = false;
    private final boolean[] mouseButtonPressed = new boolean[3];
    private boolean activeInWindow = false;

    private static final class MouseListenerHolder {
        private static final MouseListener mouseListener = new MouseListener();
    }

    private MouseListener() {
    }

    public static MouseListener getInstance() {
        return MouseListenerHolder.mouseListener;
    }

    public static void mousePositionChangedCallback(long window, double xPos, double yPos) {
        MouseListener currentInstance = getInstance();
        currentInstance.lastX = currentInstance.xPos;
        currentInstance.lastY = currentInstance.yPos;
        currentInstance.xPos = xPos;
        currentInstance.yPos = yPos;
        currentInstance.isDragging = currentInstance.mouseButtonPressed[0]
                || currentInstance.mouseButtonPressed[1]
                || currentInstance.mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int modifier) {
        MouseListener currentInstance = getInstance();
        if (button >= currentInstance.mouseButtonPressed.length) {
            return;
        }
        switch (action) {
            case GLFW_PRESS -> currentInstance.mouseButtonPressed[button] = true;
            case GLFW_RELEASE -> {
                currentInstance.mouseButtonPressed[button] = false;
                currentInstance.isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        MouseListener currentInstance = getInstance();
        currentInstance.scrollX = xOffset;
        currentInstance.scrollY = yOffset;
    }

    public static void endFrame() {
        MouseListener currentInstance = getInstance();
        currentInstance.scrollX = 0;
        currentInstance.scrollY = 0;
        currentInstance.lastX = currentInstance.xPos;
        currentInstance.lastY = currentInstance.yPos;
    }

    public static void mouseEnteredCallback(long window, boolean entered) {
        getInstance().hasMouseEntered = entered;
    }

    public static float getX() {
        return (float) getInstance().xPos;
    }

    public static float getY() {
        return (float) getInstance().yPos;
    }

    public static float getDeltaX() {
        return (float) (getInstance().lastX - getInstance().xPos);
    }

    public static float getDeltaY() {
        return (float) (getInstance().lastY - getInstance().yPos);
    }

    public static float getScrollX() {
        return (float) getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float) getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public static boolean hasMouseEntered() {
        return getInstance().hasMouseEntered;
    }

    public static boolean isMouseButtonPressed(int button) {
        if (button > getInstance().mouseButtonPressed.length) {
            return false;
        }
        return getInstance().mouseButtonPressed[button];
    }

    public static void setActiveInWindow() {
        getInstance().activeInWindow = true;
    }

    public static boolean isActiveInWindow() {
        return getInstance().activeInWindow;
    }
}
