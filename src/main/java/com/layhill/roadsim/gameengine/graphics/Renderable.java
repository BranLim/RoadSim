package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import org.joml.Vector3f;

public interface Renderable {

    TexturedModel getTexturedModel();

    Vector3f getPosition();

    float getRotateX();

    float getRotateY();

    float getRotateZ();

    float getScale();
}
