package com.layhill.roadsim.gameengine.skybox;

import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;

import java.io.IOException;

public class Skybox {

    private int vaoId;
    private int textureId;
    private int vertexCount;
    private SkyShaderProgram shaderProgram;

    public Skybox(int vaoId, int textureId) {
        this.vaoId = vaoId;
        this.textureId = textureId;

        try {
            shaderProgram = new SkyShaderProgram();
            shaderProgram.addShader(ShaderFactory.loadShaderFromFile("assets/shaders/skybox_vertex.glsl").get());
            shaderProgram.addShader(ShaderFactory.loadShaderFromFile("assets/shaders/skybox_fragment.glsl").get());
            shaderProgram.init();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load shader");
        }
    }

    public int getVaoId(){
        return vaoId;
    }

    public int getTextureId() {
        return textureId;
    }

    public SkyShaderProgram getShaderProgram() {
        return shaderProgram;
    }


    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }


}
