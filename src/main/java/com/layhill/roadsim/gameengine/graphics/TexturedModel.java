package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;

import java.util.Objects;
import java.util.UUID;

public class TexturedModel {

    private String id;
    private GLModel rawModel;
    private Material material;

    public TexturedModel(GLModel rawModel, Material material) {
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

    public GLModel getRawModel() {
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
