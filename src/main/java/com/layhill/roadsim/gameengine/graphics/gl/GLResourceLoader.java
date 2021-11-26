package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.RawTexture;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.skybox.Skybox;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public final class GLResourceLoader {

    private static final class GLResourceLoaderHolder {
        private static final GLResourceLoader loader = new GLResourceLoader();
    }

    private static final int TRIANGLE_ATTRIBUTE_POSITION = 0;
    private static final int TEXTURE_COORDINATE_ATTRIBUTE_POSITION = 1;
    private static final int VERTEX_NORMAL_ATTRIBUTE_POSITION = 2;

    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textureIds = new ArrayList<>();

    private GLResourceLoader() {

    }

    public static GLResourceLoader getInstance() {
        return GLResourceLoaderHolder.loader;
    }

    public GLModel loadToVao(Mesh mesh) {
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
        return new GLModel(vaoId, mesh.getVertexCount(), attributes);
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

    public GLTexture loadTexture(RawTexture texture, int target) {

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
        return new GLTexture(textureId, target);
    }

    public Skybox loadCubeMapAsSkybox(Mesh mesh, RawTexture[] textures) {
        GLModel model = loadToVao(mesh);

        glBindVertexArray(model.getVaoId());
        int textureId = glGenTextures();
        textureIds.add(textureId);

        Skybox skybox = new Skybox(model.getVaoId(), textureId);
        skybox.setVertexCount(model.getVertexCount());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);

        int index = 0;
        for (RawTexture texture : textures) {
            int internalFormat = imageChannelToGLFormat(texture.getChannel());
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + index, 0, internalFormat, texture.getWidth(), texture.getHeight(), 0, internalFormat, GL_UNSIGNED_BYTE, texture.getImage());
            index++;
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        glBindVertexArray(0);

        return skybox;
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

    private int imageChannelToGLFormat(int channel) {
        return switch (channel) {
            case 3 -> GL_RGB;
            case 4 -> GL_RGBA;
            default -> throw new IllegalArgumentException("unknown channel");
        };
    }

}
