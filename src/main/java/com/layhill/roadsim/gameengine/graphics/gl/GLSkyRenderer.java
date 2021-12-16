package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.skybox.SkyShaderProgram;
import com.layhill.roadsim.gameengine.skybox.Skybox;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class GLSkyRenderer implements Renderer {

    public GLSkyRenderer() {

    }

    @Override
    public void prepare() {
        glDepthFunc(GL_LEQUAL);
        glDisable(GL_CULL_FACE);
    }

    @Override
    public void render( ViewSpecification viewSpecification, RendererData rendererData) {
        Skybox skybox = rendererData.getSkybox();
        int vaoId = skybox.getVaoId();
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        SkyShaderProgram skyboxShader = skybox.getShaderProgram();
        skyboxShader.start();
        skyboxShader.loadCamera(viewSpecification);
        skyboxShader.loadTexture(0);
        skyboxShader.loadFogColour(rendererData.getFogColour());
        skyboxShader.enableFog();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skybox.getTextureId());
        glDrawElements(GL_TRIANGLES, skybox.getVertexCount(), GL_UNSIGNED_INT, 0);

        endRendering(skybox);
    }

    private void endRendering(Skybox skybox){

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        skybox.getShaderProgram().stop();
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);

        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    @Override
    public void dispose(RendererData rendererData) {
        Skybox skybox = rendererData.getSkybox();
        if (skybox != null) {
            skybox.getShaderProgram().dispose();
            glDeleteTextures(skybox.getTextureId());
            glDeleteVertexArrays(skybox.getVaoId());
        }
    }
}
