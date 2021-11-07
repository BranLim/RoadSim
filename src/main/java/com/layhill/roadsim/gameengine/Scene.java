package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.graphics.Camera;

public abstract class Scene {

    protected Camera camera;

    public void init() {
    }

    public abstract void update(double deltaTime);
}
