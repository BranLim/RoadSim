package com.layhill.roadsim.gameengine.graphics.gl;

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

    private int vaoId;
    private int attributePointerId;
    private int vertexCount;
    private boolean uploadedToGpu = false;

    private List<Vector3f> meshData;
    private List<Integer> meshDataIndices;
    private List<Integer> vbos = new ArrayList<>();

    public MeshModel(int vaoId, int attributePointerId, List<Vector3f> meshData, List<Integer> meshDataIndices) {
        this.vaoId = vaoId;
        this.attributePointerId = attributePointerId;
        this.meshData = meshData;
        this.meshDataIndices = meshDataIndices;
        vertexCount = meshDataIndices.size();
    }

    @Override
    public void uploadToGpu() {

        uploadMeshDataIndicesBuffer();

        int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        FloatBuffer dataBuffer = meshDataToFloatBuffer();
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

        IntBuffer buffer = dataToIntBuffer();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private FloatBuffer meshDataToFloatBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(meshData.size() * 3);
        for (var position : meshData) {
            buffer.put(position.x);
            buffer.put(position.y);
            buffer.put(position.z);
        }
        buffer.flip();
        return buffer;
    }

    private IntBuffer dataToIntBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(meshDataIndices.size());
        buffer.put(meshDataIndices.stream().mapToInt(i -> i).toArray());
        buffer.flip();
        return buffer;
    }
}
