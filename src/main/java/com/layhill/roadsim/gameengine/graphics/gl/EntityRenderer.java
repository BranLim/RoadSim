package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.entities.EntityShaderProgram;
import com.layhill.roadsim.gameengine.environments.Sun;
import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.lights.Light;
import com.layhill.roadsim.gameengine.graphics.lights.Spotlight;
import com.layhill.roadsim.gameengine.graphics.models.*;
import com.layhill.roadsim.gameengine.utils.Maths;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class EntityRenderer implements Renderer {

    public EntityRenderer() {

    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    @Override
    public void render( ViewSpecification viewSpecification, RendererData rendererData) {
        for (TexturedModel model : rendererData.getEntities().keySet()) {
            prepareForRendering(viewSpecification, rendererData.getSun(), rendererData.getLights(), model);
            for (Renderable gameObject : rendererData.getEntities().get(model)) {
                prepareEntity(gameObject);
                glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel(model);
        }
    }

    private void prepareEntity(Renderable renderableEntity) {
        Objects.requireNonNull(renderableEntity);
        ShaderProgram shaderProgram = renderableEntity.getTexturedModel().getMaterial().getShaderProgram();

        if (shaderProgram.getClass() == EntityShaderProgram.class) {
            Matrix4f transformationMatrix = Maths.createTransformationMatrix(renderableEntity.getPosition(),
                    renderableEntity.getRotateX(), renderableEntity.getRotateY(), renderableEntity.getRotateZ(),
                    renderableEntity.getScale());
            ((EntityShaderProgram) shaderProgram).loadModelTransformation(transformationMatrix);
        }
    }

    private void prepareForRendering(ViewSpecification viewSpecification, Sun sun, List<Light> lightsToProcess, TexturedModel texturedModel) {
        glBindVertexArray(texturedModel.getRawModel().getVaoId());
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        Material material = texturedModel.getMaterial();
        if (material != null) {
            ShaderProgram shaderProgram = material.getShaderProgram();
            if (shaderProgram.getClass() == EntityShaderProgram.class) {
                shaderProgram.start();
                EntityShaderProgram entityShaderProgram = (EntityShaderProgram) shaderProgram;
                entityShaderProgram.loadCamera(viewSpecification);

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
                entityShaderProgram.loadSun(sun.getDirection(), sun.getColour());

                material.getDiffuseMap().activate(GL_TEXTURE0);
                entityShaderProgram.loadTexture(0);

                shaderProgram.uploadFloat("uReflectivity", material.getReflectivity());
                shaderProgram.uploadFloat("uShineDampen", material.getShineDampener());
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
        for (TexturedModel model : rendererData.getEntities().keySet()) {
            ShaderProgram shaderProgram = model.getMaterial().getShaderProgram();
            shaderProgram.stop();
            shaderProgram.dispose();
        }
    }
}
