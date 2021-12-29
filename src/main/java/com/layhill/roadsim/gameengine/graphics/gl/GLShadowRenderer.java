package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.*;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.shadows.ShadowShaderProgram;
import com.layhill.roadsim.gameengine.utils.Maths;
import org.joml.Matrix4f;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLShadowRenderer implements Renderer {

    public static final int SHADOW_MAP_SIZE = 4096;
    private ShadowShaderProgram shader;
    private GLResourceLoader loader;

    public GLShadowRenderer(GLResourceLoader loader) {
        this.loader = loader;
        shader = ShaderFactory.createShadowShaderProgram();
    }


    @Override
    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_DEPTH_BUFFER_BIT);

    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {
        shader.start();
        for (TexturedModel model : rendererData.getEntities().keySet()) {
            prepareForRendering(model);
            for (Renderable gameObject : rendererData.getEntities().get(model)) {
                prepareEntity(viewSpecification, gameObject);
                glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel(model);
        }
        shader.stop();
    }

    private void prepareForRendering(TexturedModel texturedModel) {
        glBindVertexArray(texturedModel.getRawModel().getVaoId());
        for (int attribute : texturedModel.getRawModel().getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
    }

    private void prepareEntity(ViewSpecification viewSpecification, Renderable renderableEntity) {
        Objects.requireNonNull(renderableEntity);
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(renderableEntity.getPosition(),
                renderableEntity.getRotateX(), renderableEntity.getRotateY(), renderableEntity.getRotateZ(),
                renderableEntity.getScale());

        Matrix4f mvp = new Matrix4f();
        mvp.set(viewSpecification.getProjectionMatrix()).mul(viewSpecification.getViewMatrix())
                .mul(transformationMatrix);
        shader.loadProjectionViewMatrix(mvp);
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
