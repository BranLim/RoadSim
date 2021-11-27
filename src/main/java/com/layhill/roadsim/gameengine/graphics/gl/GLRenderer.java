package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.EntityShaderProgram;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.TerrainShaderProgram;
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

public class GLRenderer implements Renderer {

    private GLSkyRenderer skyRenderer;
    private Vector3f sunDirection = new Vector3f(-40.f, 50.f, -30.f);
    private Vector3f sunColour = new Vector3f(1.f, 1.f, 1.f);

    public GLRenderer() {
        skyRenderer = new GLSkyRenderer();
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void render(long window, Camera camera, List<Light> lights, Map<TexturedModel, List<Renderable>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareForRendering(camera, lights, model);
            for (Renderable gameObject : entities.get(model)) {
                prepareEntity(gameObject);
                render(GL_TRIANGLES, model.getRawModel().getVertexCount());
            }
            unbindTexturedModel(model);
        }
        skyRenderer.setCamera(camera);
        skyRenderer.render();
    }

    private void prepareEntity(Renderable renderableEntity) {
        Objects.requireNonNull(renderableEntity);
        ShaderProgram shaderProgram = renderableEntity.getTexturedModel().getMaterial().getShaderProgram();
        Matrix4f transformationMatrix = Transformation.createTransformationMatrix(renderableEntity.getPosition(),
                renderableEntity.getRotateX(), renderableEntity.getRotateY(), renderableEntity.getRotateZ(),
                renderableEntity.getScale());
        if (shaderProgram.getClass() == EntityShaderProgram.class) {
            ((EntityShaderProgram) shaderProgram).loadModelTransformation(transformationMatrix);
        } else if (shaderProgram.getClass() == TerrainShaderProgram.class) {
            ((TerrainShaderProgram) shaderProgram).loadModelTransformation(transformationMatrix);
        }

    }

    private void prepareForRendering(Camera camera, List<Light> lightsToProcess, TexturedModel texturedModel) {
        glBindVertexArray(texturedModel.getRawModel().getVaoId());
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        Material material = texturedModel.getMaterial();
        if (material != null) {
            ShaderProgram shaderProgram = material.getShaderProgram();
            shaderProgram.start();
            if (shaderProgram.getClass() == EntityShaderProgram.class) {
                EntityShaderProgram entityShaderProgram = (EntityShaderProgram) shaderProgram;
                entityShaderProgram.loadCamera(camera);

                if (lightsToProcess != null && !lightsToProcess.isEmpty()) {
                    Light[] lights = new Light[lightsToProcess.size()];
                    lightsToProcess.toArray(lights);
                    entityShaderProgram.loadLights(lights);
                }
                entityShaderProgram.loadSun(sunDirection, sunColour);
                entityShaderProgram.loadTexture(0);
                shaderProgram.uploadFloat("uReflectivity", material.getReflectivity());
                shaderProgram.uploadFloat("uShineDampen", material.getShineDampener());
            } else if (shaderProgram.getClass() == TerrainShaderProgram.class) {
                TerrainShaderProgram terrainShaderProgram = (TerrainShaderProgram) shaderProgram;
                terrainShaderProgram.loadSun(sunDirection, sunColour);
                terrainShaderProgram.loadCamera(camera);
                terrainShaderProgram.loadTexture(0);
            }

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
        skyRenderer.dispose();
    }
}
