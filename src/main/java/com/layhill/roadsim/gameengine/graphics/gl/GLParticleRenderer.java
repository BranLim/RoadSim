package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.particles.Particle;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.particles.ParticleShaderProgram;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

@Slf4j
public class GLParticleRenderer implements Renderer {

    private static final float[] QUAD = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static final int MAX_PARTICLE_INSTANCES = 500; //More means more memory bandwidth requirement
    private static final int INSTANCE_DATA_LENGTH = 16; //4f*4

    private GLModel model;
    private ParticleShaderProgram shader;
    private GLResourceLoader loader;
    private int vbo;
    private FloatBuffer renderData = BufferUtils.createFloatBuffer(MAX_PARTICLE_INSTANCES * INSTANCE_DATA_LENGTH);
    private int dataPointer = 0;

    private Matrix4f modelTransformation = new Matrix4f();
    private Matrix3f transposedViewMatrixWithoutTranslation = new Matrix3f();

    public GLParticleRenderer(GLResourceLoader loader) {
        this.loader = loader;
        model = loader.loadToVao(QUAD, 2);
        vbo = loader.createUpdateableVbo(INSTANCE_DATA_LENGTH * MAX_PARTICLE_INSTANCES);

        loader.addInstanceAttribute(model.getVaoId(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
        loader.addInstanceAttribute(model.getVaoId(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
        loader.addInstanceAttribute(model.getVaoId(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
        loader.addInstanceAttribute(model.getVaoId(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);

        model.addAttributes(1, 2, 3, 4);

        shader = ShaderFactory.createParticleShaderProgram();
    }

    @Override
    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {
        long startTime = System.currentTimeMillis();
        Matrix4f viewMatrix = viewSpecification.getViewMatrix();
        viewMatrix.get3x3(transposedViewMatrixWithoutTranslation);
        transposedViewMatrixWithoutTranslation.transpose();

        for (ParticleEmitter emitter : rendererData.getEmitters()) {
            dataPointer = 0;
            List<Particle> particles = emitter.getParticles();
            float[] data = new float[particles.size() * INSTANCE_DATA_LENGTH];
            startRendering(emitter.getParticleTexture(), viewSpecification);
            for (Particle particle : particles) {
                updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), viewMatrix, data);
            }
            loader.updateVbo(vbo, data, renderData);
            glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, model.getVertexCount(), particles.size());
            endRendering(emitter.getParticleTexture());
        }
        long endTime = System.currentTimeMillis();
        log.info(String.format("Particle rendering time: %d ms", (endTime - startTime)));
    }

    private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix, float[] data) {

        modelTransformation.identity()
                .translate(position, modelTransformation)
                .set3x3(transposedViewMatrixWithoutTranslation)
                .rotateZ((float) Math.toRadians(rotation), modelTransformation)
                .scale(scale, modelTransformation);

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity()
                .set(viewMatrix)
                .mul(modelTransformation, modelViewMatrix);
        storeMatrixData(modelViewMatrix, data);
    }

    private void storeMatrixData(Matrix4f viewModelMatrix, float[] data) {

        //column 1
        data[dataPointer++] = viewModelMatrix.m00();
        data[dataPointer++] = viewModelMatrix.m01();
        data[dataPointer++] = viewModelMatrix.m02();
        data[dataPointer++] = viewModelMatrix.m03();

        //column 2
        data[dataPointer++] = viewModelMatrix.m10();
        data[dataPointer++] = viewModelMatrix.m11();
        data[dataPointer++] = viewModelMatrix.m12();
        data[dataPointer++] = viewModelMatrix.m13();

        //column3
        data[dataPointer++] = viewModelMatrix.m20();
        data[dataPointer++] = viewModelMatrix.m21();
        data[dataPointer++] = viewModelMatrix.m22();
        data[dataPointer++] = viewModelMatrix.m23();

        //column4
        data[dataPointer++] = viewModelMatrix.m30();
        data[dataPointer++] = viewModelMatrix.m31();
        data[dataPointer++] = viewModelMatrix.m32();
        data[dataPointer++] = viewModelMatrix.m33();
    }

    private void startRendering(GLTexture particleTexture, ViewSpecification viewSpecification) {

        shader.start();
        shader.loadCamera(viewSpecification);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glDepthMask(false);

        glBindVertexArray(model.getVaoId());
        for (int attribute : model.getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        if (particleTexture != null) {
            particleTexture.activate(GL_TEXTURE0);
            shader.loadTexture(0);
        }
    }

    private void endRendering(GLTexture particleTexture) {

        for (int attribute : model.getAttributes()) {
            glDisableVertexAttribArray(attribute);
        }
        glBindVertexArray(0);

        glDepthMask(true);
        glDisable(GL_BLEND);
        if (particleTexture != null) {
            particleTexture.unbind();
        }
        shader.stop();
    }


    @Override
    public void dispose(RendererData rendererData) {
        shader.dispose();
    }
}
