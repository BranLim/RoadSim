package com.layhill.roadsim.gameengine.guis;

import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import org.joml.Vector2f;

import java.util.Objects;
import java.util.UUID;

public class GuiTexture{

    private final String id;
    private GLTexture texture;
    private Vector2f position;
    private Vector2f rotationInDegree;
    private Vector2f scale;

    public GuiTexture(GLTexture texture, Vector2f position, Vector2f rotationInDegree, Vector2f scale) {
        id = UUID.randomUUID().toString();
        this.texture = texture;
        this.position = position;
        this.rotationInDegree = rotationInDegree;
        this.scale = scale;
    }

    public String getId() {
        return id;
    }

    public GLTexture getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getRotationInDegree() {
        return rotationInDegree;
    }

    public Vector2f getScale() {
        return scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiTexture that = (GuiTexture) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
