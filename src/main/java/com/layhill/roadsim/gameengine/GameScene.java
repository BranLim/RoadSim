package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.*;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

@Slf4j
public class GameScene extends Scene {

    private GameObject gameObject;

    public GameScene() {
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3f(0.0f,10.0f,-50.0f), new Vector3f(0.0f, 1.0f, 0), new Vector3f(0.0f, 0.0f, 5.0f));

        gameObject = new GameObject();
        gameObject.init();
    }

    @Override
    public void update(float deltaTime) {

        if (MouseListener.isActiveInWindow()){
            camera.turn(deltaTime);
            MouseListener.endFrame();
        }
        camera.move(deltaTime);
        gameObject.render(camera);
    }

    @Override
    public void cleanUp() {
        if (gameObject != null) {
            gameObject.cleanUp();
        }

    }


}
