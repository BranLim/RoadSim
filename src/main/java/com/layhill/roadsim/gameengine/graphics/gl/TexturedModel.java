package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLRawModel;

import java.util.Objects;
import java.util.UUID;

public class TexturedModel {

    private String id;
    private GLRawModel rawModel;
    private Material material;

    public TexturedModel(GLRawModel rawModel, Material material) {
        id = UUID.randomUUID().toString();
        this.rawModel = rawModel;
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public String getId() {
        return id;
    }

    public GLRawModel getRawModel() {
        return rawModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TexturedModel that = (TexturedModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
