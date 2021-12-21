package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.RawTexture;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

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
    private final List<Integer> frameBuffers = new ArrayList<>();
    private final List<Integer> renderBuffers = new ArrayList<>();

    private GLResourceLoader() {

    }

    public static GLResourceLoader getInstance() {
        return GLResourceLoaderHolder.loader;
    }

    public GLModel loadToVao(Mesh mesh) {
        Objects.requireNonNull(mesh);
        List<Integer> attributes = new ArrayList<>();

        int vaoId = generateAndBindVao();

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

    public GLModel loadToVao(float[] vertices, int dimension) {
        int vaoId = generateAndBindVao();

        List<Integer> attributes = new ArrayList<>();
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        storeDataInAttributeList(TRIANGLE_ATTRIBUTE_POSITION, dimension, verticesBuffer);
        attributes.add(TRIANGLE_ATTRIBUTE_POSITION);

        glBindVertexArray(0);

        return new GLModel(vaoId, (vertices.length / dimension), attributes);
    }

    private int generateAndBindVao() {
        int vaoId = glGenVertexArrays();
        vaos.add(vaoId);
        glBindVertexArray(vaoId);
        return vaoId;
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

    public GLTexture load2DTexture(RawTexture texture, int target, boolean enableMipmap) {

        int textureId = glGenTextures();
        glBindTexture(target, textureId);
        textureIds.add(textureId);

        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        switch (texture.getChannel()) {
            case 3 -> glTexImage2D(target, 0, GL_RGB, texture.getWidth(), texture.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, texture.getImage());
            case 4 -> glTexImage2D(target, 0, GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getImage());
        }

        if (enableMipmap) {
            glGenerateMipmap(target);
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(target, GL_TEXTURE_LOD_BIAS, -0.2f);
        }

        glBindTexture(target, 0);
        return new GLTexture(textureId, target);
    }

    public Skybox loadSkybox(String meshFile, String[] texturesFiles) {

        Optional<Mesh> mesh = MeshLoader.loadObjAsMesh(meshFile);
        if (mesh.isEmpty()) {
            throw new IllegalArgumentException(String.format("Mesh: %s not found", meshFile));
        }
        GLModel model = loadToVao(mesh.get());

        glBindVertexArray(model.getVaoId());

        int textureId = glGenTextures();
        textureIds.add(textureId);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);

        Skybox skybox = new Skybox(model.getVaoId(), textureId);
        skybox.setVertexCount(model.getVertexCount());

        int index = 0;
        for (String textureFile : texturesFiles) {
            Optional<RawTexture> textureData = TextureLoader.loadAsTextureFromFile(textureFile);
            if (textureData.isPresent()) {
                int internalFormat = imageChannelToGLFormat(textureData.get().getChannel());
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + index, 0, internalFormat,
                        textureData.get().getWidth(), textureData.get().getHeight(), 0, internalFormat,
                        GL_UNSIGNED_BYTE, textureData.get().getImage());
                index++;
            }
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

    public void addInstanceAttribute(int vao, int vbo, int attribute, int dataSize, int instanceDataLength, int offset) {
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(attribute, dataSize, GL_FLOAT, false, instanceDataLength * Float.BYTES, (long) offset * Float.BYTES);
        glVertexAttribDivisor(attribute, 1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public int createUpdateableVbo(int floatCount) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (long) floatCount * Float.BYTES, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (long) buffer.capacity() * Float.BYTES, GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int createFrameBuffer() {
        int frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        frameBuffers.add(frameBuffer);
        return frameBuffer;
    }

    public void drawToEmptyAttachment(){
        glDrawBuffer(GL_NONE);
    }

    public void drawToColourAttachment(){
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
    }

    public int createTextureAttachment(int width, int height) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        textureIds.add(textureId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureId, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureId;
    }

    public int createDepthBufferAttachment(int width, int height) {
        int depthBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        renderBuffers.add(depthBuffer);

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        return depthBuffer;
    }

    public void bindFrameBuffer(int frameBuffer, int width, int height) {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }

    public int createDepthTextureAttachment(int width, int height) {
        int textureId = create2DTexture();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, textureId, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }

    public int createShadowDepthTextureAttachment(int width, int height){
        int textureId = create2DTexture();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, textureId, 0);

        return textureId;
    }

    private int create2DTexture() {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        textureIds.add(textureId);
        return textureId;
    }

    public void unbindFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void unbindFrameBuffer(int width, int height) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
    }

    public void dispose() {

        for (Integer vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (Integer vbo : vbos) {
            glDeleteBuffers(vbo);
        }
        for (Integer texId : textureIds) {
            glDeleteTextures(texId);
        }
        for (Integer renderBuffer : renderBuffers) {
            glDeleteRenderbuffers(renderBuffer);
        }
        for (Integer frameBuffer : frameBuffers) {
            glDeleteFramebuffers(frameBuffer);
        }
    }

    private int imageChannelToGLFormat(int channel) {
        return switch (channel) {
            case 1 -> GL_R;
            case 3 -> GL_RGB;
            case 4 -> GL_RGBA;
            default -> throw new IllegalArgumentException("unknown channel");
        };
    }

}
