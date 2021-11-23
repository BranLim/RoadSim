package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.graphics.models.Camera;

public abstract class Scene {

    protected Camera camera;

    public void init() {
    }

    public abstract void update(float deltaTime);

    public abstract void cleanUp();
}
