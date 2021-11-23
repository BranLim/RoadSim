package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

@Slf4j
public class Shader {

    private int shaderId = -1;
    private final String source;
    private final int shaderType;

    protected Shader(String source, int shaderType) {
        this.source = source;
        this.shaderType = shaderType;
    }

    protected void compile() {
        shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH);
            log.error("ERROR: defaultShader.glsl \n\t Vertex shader compile failed");
            log.error(glGetShaderInfoLog(shaderId, len));
            throw new RuntimeException("Unable to compile vertex shader");
        }
    }

    public int id() {
        return shaderId;
    }
}
