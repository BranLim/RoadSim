package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ParticleRenderer {

    private static final float[] QUAD = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};

    private GLModel model;
    private ParticleShaderProgram shader;


    public ParticleRenderer(GLResourceLoader loader) {
        model = loader.loadToVao(QUAD, 2);
        shader = ShaderFactory.createParticleShaderProgram();
    }

    public void render(GLTexture particleTexture, List<Particle> particles, Camera camera) {
        start(particleTexture, camera);
        for (Particle particle : particles) {
            updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), camera);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, model.getVertexCount());
        }
        end(particleTexture);
    }

    private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Camera camera) {
        Matrix4f viewMatrix = camera.getViewMatrix();

        Matrix3f upperLeft = new Matrix3f();
        viewMatrix.get3x3(upperLeft);

        Matrix4f modelTransformation = new Matrix4f();
        modelTransformation.identity()
                .translate(position, modelTransformation)
                .set3x3(upperLeft.transpose())
                .rotateZ((float) Math.toRadians(rotation), modelTransformation)
                .scale(scale, modelTransformation);

        Matrix4f modelViewMatrix = new Matrix4f();
        modelViewMatrix.identity()
                .set(viewMatrix)
                .mul(modelTransformation, modelViewMatrix);

        shader.loadModelTransformation(modelViewMatrix);
    }

    private void start(GLTexture particleTexture, Camera camera) {

        shader.start();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glDepthMask(false);

        glBindVertexArray(model.getVaoId());
        glEnableVertexAttribArray(0);
        if (particleTexture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(particleTexture.getTarget(), particleTexture.getTextureId());
            shader.loadTexture(0);
        }
        shader.loadCamera(camera);

    }

    private void end(GLTexture particleTexture) {

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        glDepthMask(true);
        glDisable(GL_BLEND);
        if (particleTexture != null) {
            glBindTexture(particleTexture.getTarget(), 0);
        }
        shader.stop();
    }

    public void dispose() {
        shader.dispose();
    }

}
