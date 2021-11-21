package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.gl.Uniform;
import com.layhill.roadsim.gameengine.graphics.gl.UniformMatrix4f;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

@Slf4j
public class ShaderProgram {

    private int programId;
    private List<Shader> shaders = new ArrayList<>();
    private List<Uniform> uniformData = new ArrayList<>();
    private boolean initialised;
    private UniformMatrix4f projection = new UniformMatrix4f("uProjection");
    private UniformMatrix4f view = new UniformMatrix4f("uView");
    private UniformMatrix4f modelTransformation = new UniformMatrix4f("uTransformation");

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
        setupUniformData(projection, view, modelTransformation);
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

    private void setupUniformData(Uniform... uniforms) {
        uniformData.addAll(List.of(uniforms));
        for (var uniform : uniformData) {
            uniform.getUniformLocation(programId);
        }
    }

    public void uploadFloat(String varName, float value) {
        int varLocation = glGetUniformLocation(programId, varName);
        glUniform1f(varLocation, value);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(programId, varName);
        glUniform1i(varLocation, slot);
    }

    public void uploadVec3f(String varName, Vector3f value) {
        int varLocation = glGetUniformLocation(programId, varName);
        glUniform3f(varLocation, value.x, value.y, value.z);
    }

    public void uploadMat4f(String varName, Matrix4f matrix) {
        int varLocation = glGetUniformLocation(programId, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void loadCamera(Camera camera) {
        if (!initialised || camera == null) {
            return;
        }
        projection.load(camera.getProjectionMatrix());
        view.load(camera.getViewMatrix());
    }

    public void loadModelTransformation(GameObject gameObject){
        if (!initialised || gameObject == null) {
            return;
        }
        modelTransformation.load(gameObject.getTransformationMatrix());
    }

}
