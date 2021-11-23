package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.graphics.Texture;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLRawModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLRawTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public final class GLResourceLoader {

    private static final int TRIANGLE_ATTRIBUTE_POSITION = 0;
    private static final int TEXTURE_COORDINATE_ATTRIBUTE_POSITION = 1;
    private static final int VERTEX_NORMAL_ATTRIBUTE_POSITION = 2;

    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textureIds = new ArrayList<>();

    public GLResourceLoader() {

    }

    public GLRawModel loadToVao(Mesh mesh) {
        Objects.requireNonNull(mesh);
        List<Integer> attributes = new ArrayList<>();
        int vaoId = glGenVertexArrays();
        vaos.add(vaoId);
        glBindVertexArray(vaoId);

        storeIndicesBuffer(mesh.vertexIndicesToIntBuffer());

        storeDataInAttributeList(TRIANGLE_ATTRIBUTE_POSITION, 3, mesh.verticesToFloatBuffer());
        attributes.add(TRIANGLE_ATTRIBUTE_POSITION);

        storeDataInAttributeList(TEXTURE_COORDINATE_ATTRIBUTE_POSITION, 2, mesh.textureCoordinatesToFloatBuffer());
        attributes.add(TEXTURE_COORDINATE_ATTRIBUTE_POSITION);

        storeDataInAttributeList(VERTEX_NORMAL_ATTRIBUTE_POSITION, 3, mesh.vertexNormalsToFloatBuffer());
        attributes.add(VERTEX_NORMAL_ATTRIBUTE_POSITION);

        glBindVertexArray(0);
        return new GLRawModel(vaoId, mesh.getVertexCount(), attributes);
    }

    private void storeDataInAttributeList(int attributeIndex, int dataWidth, FloatBuffer data) {
        int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeIndex, dataWidth, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeIndicesBuffer(IntBuffer data) {
        int bufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }

    public GLRawTexture loadTexture(Texture texture, int target) {

        int textureId = glGenTextures();
        glBindTexture(target, textureId);
        textureIds.add(textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        switch (texture.getChannel()) {
            case 3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, texture.getWidth(), texture.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, texture.getImage());
            case 4 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getImage());
        }

        glBindTexture(target, 0);
        return new GLRawTexture(textureId, target);
    }

    public void dispose() {

        for (Integer vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (Integer vbo : vbos) {
            glDeleteBuffers(vbo);
        }
        for (int texId : textureIds) {
            glDeleteTextures(texId);
        }
    }

}
