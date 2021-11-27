package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

@Slf4j
public class Shader {

    private final String shaderFile;
    private int shaderId = -1;
    private final String source;
    private final int shaderType;

    protected Shader(String filename, String source, int shaderType) {
        this.shaderFile = filename;
        this.source = source;
        this.shaderType = shaderType;
    }

    protected void compile() {
        shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);
    }

    public int id() {
        return shaderId;
    }

    public String getShaderFile() {
        return shaderFile;
    }
}
