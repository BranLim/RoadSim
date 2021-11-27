package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.SkyShaderProgram;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.skybox.Skybox;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class GLSkyRenderer {

    private static final String skyboxMesh = "assets/models/skybox.obj";
    private final static String[] skyboxTextures = {
            "assets/textures/Daylight_Box_Right.jpg",
            "assets/textures/Daylight_Box_Left.jpg",
            "assets/textures/Daylight_Box_Top.jpg",
            "assets/textures/Daylight_Box_Bottom.jpg",
            "assets/textures/Daylight_Box_Front.jpg",
            "assets/textures/Daylight_Box_Back.jpg"
    };

    private Skybox skybox;
    private Camera camera;

    public GLSkyRenderer() {
        GLResourceLoader glResourceLoader = GLResourceLoader.getInstance();
        skybox = glResourceLoader.loadSkybox(skyboxMesh, skyboxTextures);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void render() {
        glDepthFunc(GL_LEQUAL);
        glDisable(GL_CULL_FACE);

        int vaoId = skybox.getVaoId();
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        SkyShaderProgram skyboxShader = skybox.getShaderProgram();
        skyboxShader.start();
        skyboxShader.loadCamera(camera);
        skyboxShader.loadTexture(0);
        skyboxShader.loadFogColour(new Vector3f(0.2f, 0.2f, 0.2f));
        skyboxShader.enableFog();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skybox.getTextureId());
        glDrawElements(GL_TRIANGLES, skybox.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        skyboxShader.stop();
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);

        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public void dispose() {
        if (skybox != null) {
            skybox.getShaderProgram().dispose();
            glDeleteTextures(skybox.getTextureId());
            glDeleteVertexArrays(skybox.getVaoId());
        }
    }
}
