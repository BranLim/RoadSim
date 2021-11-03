package com.layhill.roadsim.gameengine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Platform;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width;
    private int height;
    private String title;
    private long glfwWindow;
    private Time time;

    private Window() {
        width = 1920;
        height = 1080;
        title = "Road Sim";
    }

    private static final class WindowHolder {
        private static final Window window = new Window();
    }

    public static Window getInstance() {
        return WindowHolder.window;
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Cannot initialise GLFW");
        }

        glfwWindow = createPlatformSpecificWindow();
        registerInputCallback();

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
    }

    private long createPlatformSpecificWindow() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        }

        long glfwWindowPtr = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindowPtr == NULL) {
            throw new IllegalStateException("Cannot create GLFW window");
        }
        return glfwWindowPtr;
    }

    private void registerInputCallback() {
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePositionChangedCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
    }

    private void loop() {
        time = Time.getInstance();
        while (!glfwWindowShouldClose(glfwWindow)) {

            glClearColor(0.80f, 0.80f, 0.80f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(glfwWindow, true);
            }
            if (time.getCurrentTime() > 0 ){
                System.out.println(String.format("Delta time: %f", time.getDeltaTime()));
                System.out.println(String.format("Framerate: %f", 1.0f/time.getDeltaTime()));
            }
            if (MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                System.out.println("Mouse button 1 is pressed");
            }

            glfwSwapBuffers(glfwWindow);
            glfwPollEvents();
            time.mark();
        }
    }

    private void cleanup() {
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
