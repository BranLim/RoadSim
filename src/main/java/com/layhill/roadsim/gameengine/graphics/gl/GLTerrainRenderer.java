package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.environments.Sun;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.lights.Light;
import com.layhill.roadsim.gameengine.graphics.lights.Spotlight;
import com.layhill.roadsim.gameengine.graphics.models.*;
import com.layhill.roadsim.gameengine.terrain.TerrainShaderProgram;
import com.layhill.roadsim.gameengine.utils.Maths;
import com.layhill.roadsim.gameengine.water.WaterRenderingStage;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLTerrainRenderer implements Renderer {

    public GLTerrainRenderer() {

    }

    @Override
    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {
        for (TexturedModel model : rendererData.getTerrains().keySet()) {
            prepareForRendering(viewSpecification, model, rendererData);
            for (Renderable gameObject : rendererData.getTerrains().get(model)) {
                prepareEntity(gameObject);
                glDrawElements(GL_TRIANGLES,  model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel(model);
        }
    }

    private void prepareEntity(Renderable renderableEntity) {
        Objects.requireNonNull(renderableEntity);
        ShaderProgram shaderProgram = renderableEntity.getTexturedModel().getMaterial().getShaderProgram();

        if (shaderProgram.getClass() == TerrainShaderProgram.class) {
            Matrix4f transformationMatrix = Maths.createTransformationMatrix(renderableEntity.getPosition(),
                    0, 0, 0,
                    renderableEntity.getScale());
            ((TerrainShaderProgram) shaderProgram).loadModelTransformation(transformationMatrix);
        }
    }

    private void prepareForRendering(ViewSpecification viewSpecification, TexturedModel texturedModel, RendererData rendererData) {
        glBindVertexArray(texturedModel.getRawModel().getVaoId());
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        Material material = texturedModel.getMaterial();
        if (material != null) {
            ShaderProgram shaderProgram = material.getShaderProgram();
            if (shaderProgram.getClass() == TerrainShaderProgram.class) {
                shaderProgram.start();

                TerrainShaderProgram terrainShaderProgram = (TerrainShaderProgram) shaderProgram;
                Sun sun = rendererData.getSun();
                terrainShaderProgram.loadSun(sun.getDirection(), sun.getColour());
                terrainShaderProgram.loadCamera(viewSpecification);

                if (rendererData.getLights() != null && !rendererData.getLights().isEmpty()) {
                    Light[] lights = getLights(rendererData.getLights());
                    terrainShaderProgram.loadLights(lights);

                    List<Spotlight> spotlights = getSpotlights(rendererData.getLights());
                    if (spotlights.isEmpty()) {
                        terrainShaderProgram.disableSpotlight();
                    } else {
                        terrainShaderProgram.enableSpotlight();
                        terrainShaderProgram.loadSpotlight(spotlights.get(0));
                    }
                }

                material.getDiffuseMap().activate(GL_TEXTURE0);
                terrainShaderProgram.loadTexture(0);

                terrainShaderProgram.loadFogColour(rendererData.getFogColour());
                terrainShaderProgram.enableFog();
                if (rendererData.isToRenderWater() && rendererData.getWaterRenderingStage() != WaterRenderingStage.END) {
                    terrainShaderProgram.loadClipPlane(rendererData.getClipPlane());
                }

                if(rendererData.isToRenderShadow()) {
                    glActiveTexture(GL_TEXTURE1);
                    glBindTexture(GL_TEXTURE_2D, rendererData.getShadowFrameBuffer().getDepthBuffer());
                    terrainShaderProgram.loadShadowMapResolution(rendererData.getShadowMapResolution());
                    terrainShaderProgram.loadShadowDistance(rendererData.getShadowDistance());
                    terrainShaderProgram.loadShadowMapSpace(rendererData.getToShadowMapSpace());
                    terrainShaderProgram.loadShadowMap(1);
                }
            }
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
            material.getDiffuseMap().unbind();
            ShaderProgram shaderProgram = material.getShaderProgram();
            shaderProgram.stop();
        }
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glDisableVertexAttribArray(attribute);
        }
        glBindVertexArray(0);
    }


    @Override
    public void dispose(RendererData rendererData) {
        for (TexturedModel model : rendererData.getTerrains().keySet()) {
            ShaderProgram shaderProgram = model.getMaterial().getShaderProgram();
            shaderProgram.stop();
            shaderProgram.dispose();
        }
    }
}
