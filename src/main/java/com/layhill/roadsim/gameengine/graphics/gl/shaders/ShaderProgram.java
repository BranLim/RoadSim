package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import com.layhill.roadsim.gameengine.graphics.gl.data.Uniform;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

@Slf4j
public abstract class ShaderProgram {

    private int programId;
    private List<Shader> shaders = new ArrayList<>();
    private List<Uniform> uniformData = new ArrayList<>();
    private boolean initialised;

    public ShaderProgram() {

    }

    public void addShader(Shader shader) {
        if (shader == null) {
            return;
        }
        shaders.add(shader);
    }

    public void init() {

        programId = glCreateProgram();
        compileShaders();
        attachCompiledShaders();
        detachAndDeleteAllShaders();
        bindAttributes();
        setupUniformData();
        initialised = true;
    }

    public void start() {
        if (!initialised) {
            throw new IllegalStateException("Shader program not initialised or loaded.");
        }
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void dispose() {
        if (!initialised) {
            return;
        }
        initialised = false;
        stop();
        detachAndDeleteAllShaders();
        glDeleteProgram(programId);
        shaders.clear();
    }


    private void compileShaders() {
        for (Shader shader : shaders) {
            shader.compile();

            if (glGetShaderi(shader.id(), GL_COMPILE_STATUS) == GL_FALSE) {
                int len = glGetShaderi(shader.id(), GL_INFO_LOG_LENGTH);
                log.error("ERROR: {} \n\t Shader compile failed", shader.getShaderFile());
                log.error(glGetShaderInfoLog(shader.id(), len));
                throw new RuntimeException("Unable to compile shader");
            }
        }
    }

    private void attachCompiledShaders() {
        for (Shader shader : shaders) {
            glAttachShader(programId, shader.id());
        }
        glLinkProgram(programId);
        int success = glGetProgrami(programId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(programId, GL_INFO_LOG_LENGTH);
            log.error("ERROR: Shader link failed");
            log.error(glGetShaderInfoLog(programId, len));
        }
    }

    private void detachAndDeleteAllShaders() {
        for (Shader shader : shaders) {
            glDetachShader(programId, shader.id());
            glDeleteShader(shader.id());
        }
    }

    protected void setupUniformData() {
        for (var uniform : uniformData) {
            uniform.getUniformLocation(programId);
        }
    }

    protected void addUniform(Uniform... uniforms) {
        uniformData.addAll(List.of(uniforms));
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName){
        glBindAttribLocation(programId, attribute, variableName);
    }

    public void uploadFloat(String varName, float value) {
        int varLocation = glGetUniformLocation(programId, varName);
        glUniform1f(varLocation, value);
    }

}
