package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.entities.EntityShaderProgram;
import com.layhill.roadsim.gameengine.graphics.Renderable;
import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Light;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.models.Spotlight;
import com.layhill.roadsim.gameengine.graphics.models.Sun;
import com.layhill.roadsim.gameengine.graphics.shadows.ShadowShaderProgram;
import com.layhill.roadsim.gameengine.utils.Maths;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLShadowRenderer implements Renderer {

    private ShadowShaderProgram shader;
    private GLResourceLoader loader;

    public GLShadowRenderer(GLResourceLoader loader) {
        this.loader = loader;
        shader = ShaderFactory.createShadowShaderProgram();
    }


    @Override
    public void prepare() {

    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {
        shader.start();
        for (TexturedModel model : rendererData.getEntities().keySet()) {
            prepareForRendering(viewSpecification,model);
            for (Renderable gameObject : rendererData.getEntities().get(model)) {
                prepareEntity(gameObject);
                glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel(model);
        }
        shader.stop();
    }

    private void prepareForRendering(ViewSpecification viewSpecification,TexturedModel texturedModel) {
        glBindVertexArray(texturedModel.getRawModel().getVaoId());
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        Matrix4f projectionViewMatrix = new Matrix4f();
        projectionViewMatrix.set(viewSpecification.getProjectionMatrix()).mul(viewSpecification.getViewMatrix());

        shader.loadProjectionViewMatrix(projectionViewMatrix);
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

    private void unbindTexturedModel(TexturedModel texturedModel) {
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glDisableVertexAttribArray(attribute);
        }
        glBindVertexArray(0);
    }



    @Override
    public void dispose(RendererData rendererData) {
        shader.dispose();
    }
}
