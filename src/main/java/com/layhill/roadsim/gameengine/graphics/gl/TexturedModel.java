package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class TexturedModel {

    private String id;
    private int vaoId;
    private int vertexCount;
    private boolean uploadedToGpu = false;
    private Mesh mesh;
    private Texture texture;

    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> attributes = new ArrayList<>();

    public TexturedModel(int vaoId, Mesh mesh, Texture texture) {
        id = UUID.randomUUID().toString();
        this.vaoId = vaoId;
        this.mesh = mesh;
        this.texture = texture;
        vertexCount = mesh.getVertexCount();
    }

    public Texture getTexture() {
        return texture;
    }

    public String getId() {
        return id;
    }

    public int getVaoId() {
        return vaoId;
    }

    public List<Integer> getAttributes(){
        return attributes;
    }

    public int getVertexCount(){
        return vertexCount;
    }

    public void uploadToGpu() {

        if (mesh.hasVertexIndices()) {
            uploadMeshDataIndicesBuffer();
        }

        int attributePointerId = 0;
        int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        FloatBuffer dataBuffer = mesh.verticesToFloatBuffer();
        glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(attributePointerId, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributePointerId);

        attributes.add(attributePointerId);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        if (texture != null) {
            attributePointerId = 1;

            int textureBufferId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, textureBufferId);
            vbos.add(textureBufferId);

            glBufferData(GL_ARRAY_BUFFER, mesh.textureCoordinatesToFloatBuffer(), GL_STATIC_DRAW);
            glVertexAttribPointer(attributePointerId, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(attributePointerId);
            attributes.add(attributePointerId);

            texture.generate();
            texture.bind();
            texture.prepare();
            texture.unbind();
        }

        attributePointerId = 2;
        int normalBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalBufferId);
        vbos.add(normalBufferId);

        FloatBuffer normalBuffer = mesh.vertexNormalsToFloatBuffer();
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributePointerId, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributePointerId);
        attributes.add(attributePointerId);

        uploadedToGpu = true;
    }

    public void dispose() {
        for (var vbo : vbos) {
            glDeleteBuffers(vbo);
        }
        if (texture != null) {
            texture.dispose();
        }
    }

    private void uploadMeshDataIndicesBuffer() {
        int bufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        IntBuffer buffer = mesh.vertexIndicesToIntBuffer();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
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
