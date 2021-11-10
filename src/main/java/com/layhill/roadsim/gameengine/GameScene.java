package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.*;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@Slf4j
public class GameScene extends Scene {

    private GameObject gameObject;

    public GameScene() {
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3f(), new Vector3f(0.0f, 10.0f, -10.0f), new Vector3f(0.0f, 0.0f, 1.0f));
        camera.init();

        gameObject = new GameObject();
        gameObject.init();
    }


    @Override
    public void update(double deltaTime) {
        gameObject.render(camera);
    }

    @Override
    public void cleanUp() {
        if (gameObject != null) {
            gameObject.cleanUp();
        }

    }


}
