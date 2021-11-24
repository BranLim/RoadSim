package com.layhill.roadsim.gameengine.graphics.gl.objects;

import java.util.List;
import java.util.UUID;

public class GLModel {
    private String id;
    private int vaoId;
    private int vertexCount;
    private List<Integer> attributes ;

    public GLModel(int vaoId, int vertexCount, List<Integer> attributes) {
        this.id = UUID.randomUUID().toString();
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public List<Integer> getAttributes() {
        return attributes;
    }
}
