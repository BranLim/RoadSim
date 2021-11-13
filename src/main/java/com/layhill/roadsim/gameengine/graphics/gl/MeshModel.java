package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.Renderable;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class MeshModel implements Renderable {

    private List<Integer> vbos = new ArrayList<>();
    private int vaoId;
    private int attributePointerId;
    private int vertexCount;
    private boolean uploadedToGpu = false;
    private Mesh mesh;


    public MeshModel(int vaoId, int attributePointerId, Mesh mesh) {
        this.vaoId = vaoId;
        this.attributePointerId = attributePointerId;
        this.mesh = mesh;
        vertexCount = mesh.getVertexCount();
    }

    @Override
    public void uploadToGpu() {

        if (mesh.hasVertexIndices()){
            uploadMeshDataIndicesBuffer();
        }

        int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        FloatBuffer dataBuffer = mesh.verticesToFloatBuffer();
        glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(attributePointerId, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributePointerId);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        uploadedToGpu = true;
    }


    @Override
    public void render() {
        if (!uploadedToGpu) {
            return;
        }
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
    }

    public void dispose() {
        for (var vbo : vbos) {
            glDeleteBuffers(vbo);
        }
    }

    private void uploadMeshDataIndicesBuffer() {
        int bufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        IntBuffer buffer = mesh.vertexIndicesToIntBuffer();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

}
