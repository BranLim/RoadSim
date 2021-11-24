package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GlRenderer implements Renderer {

    private Vector3f sunDirection = new Vector3f(-40.f, 100.f, -30.f);
    private Vector3f sunColour = new Vector3f(1.f, 1.f, 1.f);

    public GlRenderer() {
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void processEntities(long window, Camera camera, List<Light> lights, Map<TexturedModel, List<Renderable>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareForRendering(camera, lights, model);
            for (Renderable gameObject : entities.get(model)) {
                prepareEntity(gameObject);
                render(GL_TRIANGLES, model.getRawModel().getVertexCount());
            }
            unbindTexturedModel(model);
        }
    }

    private void prepareEntity(Renderable renderableEntity) {
        Objects.requireNonNull(renderableEntity);
        ShaderProgram shaderProgram = renderableEntity.getTexturedModel().getMaterial().getShaderProgram();
        Matrix4f transformationMatrix = Transformation.createTransformationMatrix(renderableEntity.getPosition(),
                renderableEntity.getRotateX(), renderableEntity.getRotateY(), renderableEntity.getRotateZ(),
                renderableEntity.getScale());
        shaderProgram.loadModelTransformation(transformationMatrix);

    }

    private void prepareForRendering(Camera camera, List<Light> lights, TexturedModel texturedModel) {
        glBindVertexArray(texturedModel.getRawModel().getVaoId());
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        Material material = texturedModel.getMaterial();
        if (material != null) {
            ShaderProgram shaderProgram = material.getShaderProgram();
            shaderProgram.start();
            shaderProgram.loadCamera(camera);
            shaderProgram.loadGlobalLight(sunDirection, sunColour);
            for (Light light : lights) {
                shaderProgram.loadPositionalLight(light.getPosition(), light.getColour());
            }
            shaderProgram.uploadTexture("fTexture", 0);
            shaderProgram.uploadFloat("uReflectivity", material.getReflectivity());
            shaderProgram.uploadFloat("uShineDampen", material.getShineDampener());

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(material.getTexture().getTarget(), material.getTexture().getTextureId());
        }
    }

    private void unbindTexturedModel(TexturedModel texturedModel) {
        Material material = texturedModel.getMaterial();
        if (material != null) {
            glBindTexture(material.getTexture().getTarget(), 0);
            ShaderProgram shaderProgram = material.getShaderProgram();
            shaderProgram.stop();
        }
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glDisableVertexAttribArray(attribute);
        }
        glBindVertexArray(0);
    }

    private void render(int renderMode, int vertexCount) {
        glDrawElements(renderMode, vertexCount, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void show(long window) {
        glfwSwapBuffers(window);
    }

    @Override
    public void dispose(Map<TexturedModel, List<Renderable>> entities) {
        for (TexturedModel model : entities.keySet()) {
            ShaderProgram shaderProgram = model.getMaterial().getShaderProgram();
            shaderProgram.stop();
            shaderProgram.dispose();

        }
    }
}
