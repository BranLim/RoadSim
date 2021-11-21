package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.graphics.RenderingManager;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Platform;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Slf4j
public class Window {
    private int width;
    private int height;
    private String title;
    private long glfwWindow;
    private Time time;
    private Scene currentScene;

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

    public void changeScene(int sceneSelection) {
        switch (sceneSelection) {
            case 0 -> {
                currentScene = new MainMenuScene();
                currentScene.init();
            }
            case 1 -> {
                currentScene = new GameScene(new RenderingManager(glfwWindow));
                currentScene.init();
            }
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        time = Time.getInstance();

        if (!glfwInit()) {
            throw new IllegalStateException("Cannot initialise GLFW");
        }

        glfwWindow = createPlatformSpecificWindow();
        registerInputCallback();

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        changeScene(1);
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
        glfwSetCursorEnterCallback(glfwWindow, MouseListener::mouseEnteredCallback);
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePositionChangedCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
    }

    private void loop() {
        time.tick();
        while (!glfwWindowShouldClose(glfwWindow)) {

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(glfwWindow, true);
            }
            handleMouseInput();

            if (time.getCurrentTime() > 0.0) {
                log.info("Delta time: {}", time.getDeltaTime());
                log.info("Framerate: {}", 1.0f / time.getDeltaTime());
            }

            if (currentScene != null) {
                currentScene.update((float) time.getDeltaTime());
            }

            glfwPollEvents();
            time.tick();
        }
    }

    private void handleMouseInput() {
        if (MouseListener.hasMouseEntered()
                && glfwGetWindowAttrib(glfwWindow, GLFW_HOVERED) == GLFW_TRUE) {
            if (MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                MouseListener.setActiveInWindow();
                glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                if (glfwRawMouseMotionSupported()) {
                    glfwSetInputMode(glfwWindow, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
                }
            }

        }
    }

    private void cleanup() {
        if (currentScene != null) {
            currentScene.cleanUp();
        }
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
