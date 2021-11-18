package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TexturedModel implements Renderable {

    private int vaoId;
    private int vertexCount;
    private boolean uploadedToGpu = false;
    private Mesh mesh;
    private Texture texture;

    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> attributes = new ArrayList<>();

    public TexturedModel(int vaoId, Mesh mesh, Texture texture) {
        this.vaoId = vaoId;
        this.mesh = mesh;
        this.texture = texture;
        vertexCount = mesh.getVertexCount();
    }

    @Override
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
        uploadedToGpu = true;
    }


    @Override
    public void render() {
        if (!uploadedToGpu) {
            return;
        }
        glBindVertexArray(vaoId);
        for (var attribute : attributes) {
            glEnableVertexAttribArray(attribute);
        }
       if (texture!=null){
           glActiveTexture(GL_TEXTURE0);
           texture.bind();
       }
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        for (var attribute : attributes) {
            glDisableVertexAttribArray(attribute);
        }

        if (texture != null) {
            texture.unbind();
        }
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

}
