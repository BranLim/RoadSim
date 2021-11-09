package com.layhill.roadsim.gameengine.graphics;

import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

@Slf4j
public class ShaderProgram {

    private int programId;
    private List<Shader> shaders = new ArrayList<>();
    private boolean loaded;

    public ShaderProgram() {

    }

    public void addShader(Shader shader) {
        if (shader == null) {
            return;
        }
        shaders.add(shader);
    }

    public void init() {
        compileShaders();
        createProgramAndAttachCompiledShaders();
        detachAndDeleteAllShaders();
        loaded = true;
    }

    public void start() {
        if (!loaded) {
            throw new IllegalStateException("Shader program not initialised or loaded.");
        }
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void dispose() {
        if (!loaded) {
            return;
        }
        loaded = false;
        stop();
        detachAndDeleteAllShaders();
        glDeleteProgram(programId);
        shaders.clear();
    }

    public void uploadMat4f(String varName, Matrix4f matrix) {
        int varLocation = glGetUniformLocation(programId, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(programId, varName);
        glUniform1i(varLocation, slot);
    }

    private void compileShaders() {
        for (Shader shader : shaders) {
            shader.compile();
        }
    }

    private void createProgramAndAttachCompiledShaders() {
        programId = glCreateProgram();
        for (Shader shader : shaders) {
            glAttachShader(programId, shader.id());
        }
        glLinkProgram(programId);
        int success = glGetProgrami(programId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(programId, GL_INFO_LOG_LENGTH);
            log.error("ERROR: defaultShader.glsl \n\t Shader link failed");
            log.error(glGetShaderInfoLog(programId, len));
        }
    }

    private void detachAndDeleteAllShaders() {
        for (Shader shader : shaders) {
            glDetachShader(programId, shader.id());
            glDeleteShader(shader.id());
        }
    }

}
