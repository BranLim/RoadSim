package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.entities.EntityShaderProgram;
import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.models.Spotlight;
import com.layhill.roadsim.gameengine.particles.ParticleSystem;
import com.layhill.roadsim.gameengine.terrain.TerrainShaderProgram;
import com.layhill.roadsim.gameengine.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLRenderer implements Renderer {

    private Vector3f sunDirection = new Vector3f(-40.f, 1000.f, -30.f);
    private Vector3f sunColour = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final float SKY_RED = 0.05f;
    private static final float SKY_GREEN = 0.05f;
    private static final float SKY_BLUE = 0.05f;
    private final Vector3f fogColour = new Vector3f(0.3f, 0.3f, 0.3f);

    public GLRenderer() {

    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void render(long window, Camera camera, RendererData rendererData) {
        for (TexturedModel model :rendererData.getEntities().keySet()) {
            prepareForRendering(camera, rendererData.getLights(), model);
            for (Renderable gameObject : rendererData.getEntities().get(model)) {
                prepareEntity(gameObject);
                render(GL_TRIANGLES, model.getRawModel().getVertexCount());
            }
            unbindTexturedModel(model);
        }
    }

    private void prepareEntity(Renderable renderableEntity) {
        Objects.requireNonNull(renderableEntity);
        ShaderProgram shaderProgram = renderableEntity.getTexturedModel().getMaterial().getShaderProgram();

        if (shaderProgram.getClass() == EntityShaderProgram.class) {
            Matrix4f transformationMatrix = Transformation.createTransformationMatrix(renderableEntity.getPosition(),
                    renderableEntity.getRotateX(), renderableEntity.getRotateY(), renderableEntity.getRotateZ(),
                    renderableEntity.getScale());
            ((EntityShaderProgram) shaderProgram).loadModelTransformation(transformationMatrix);
        } else if (shaderProgram.getClass() == TerrainShaderProgram.class) {
            Matrix4f transformationMatrix = Transformation.createTransformationMatrix(renderableEntity.getPosition(),
                    0, 0, 0,
                    renderableEntity.getScale());
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
                    Light[] lights = getLights(lightsToProcess);
                    entityShaderProgram.loadLights(lights);

                    List<Spotlight> spotlights = getSpotlights(lightsToProcess);
                    if (spotlights.isEmpty()) {
                        entityShaderProgram.disableSpotlight();
                    } else {
                        entityShaderProgram.enableSpotlight();
                        entityShaderProgram.loadSpotlight(spotlights.get(0));
                    }
                }
                entityShaderProgram.loadSun(sunDirection, sunColour);
                entityShaderProgram.loadTexture(0);
                shaderProgram.uploadFloat("uReflectivity", material.getReflectivity());
                shaderProgram.uploadFloat("uShineDampen", material.getShineDampener());
            } else if (shaderProgram.getClass() == TerrainShaderProgram.class) {
                TerrainShaderProgram terrainShaderProgram = (TerrainShaderProgram) shaderProgram;
                terrainShaderProgram.loadSun(sunDirection, sunColour);
                terrainShaderProgram.loadCamera(camera);

                if (lightsToProcess != null && !lightsToProcess.isEmpty()) {
                    Light[] lights = getLights(lightsToProcess);
                    terrainShaderProgram.loadLights(lights);

                    List<Spotlight> spotlights = getSpotlights(lightsToProcess);
                    if (spotlights.isEmpty()) {
                        terrainShaderProgram.disableSpotlight();
                    } else {
                        terrainShaderProgram.enableSpotlight();
                        terrainShaderProgram.loadSpotlight(spotlights.get(0));
                    }
                }
                terrainShaderProgram.loadTexture(0);
                terrainShaderProgram.loadFogColour(fogColour);
                terrainShaderProgram.enableFog();
            }

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(material.getTexture().getTarget(), material.getTexture().getTextureId());
        }
    }

    private Light[] getLights(List<Light> lightsToProcess) {
        List<Light> filteredLights = lightsToProcess.stream()
                .filter(light -> light.getClass() != Spotlight.class)
                .toList();
        Light[] lights = new Light[filteredLights.size()];
        filteredLights.toArray(lights);
        return lights;
    }

    private List<Spotlight> getSpotlights(List<Light> lightsToProcess) {
        return lightsToProcess.stream()
                .filter(light -> light.getClass() == Spotlight.class)
                .map(light -> (Spotlight) light)
                .toList();
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
    public void dispose(RendererData rendererData) {
        for (TexturedModel model : rendererData.getEntities().keySet()) {
            ShaderProgram shaderProgram = model.getMaterial().getShaderProgram();
            shaderProgram.stop();
            shaderProgram.dispose();
        }
    }
}
