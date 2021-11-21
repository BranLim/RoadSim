package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.entities.GameObject;
import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.Texture;

import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GlRenderer implements Renderer {

    private final List<ShaderProgram> shaderPrograms;

    public GlRenderer(List<ShaderProgram> shaderPrograms) {
        this.shaderPrograms = shaderPrograms;
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glClearColor(0.20f, 0.20f, 0.20f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void processEntities(long window, Map<TexturedModel, List<GameObject>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareForRendering(model);
            for (GameObject gameObject : entities.get(model)) {
                prepareEntity(gameObject);
                render(GL_TRIANGLES, model.getVertexCount());
            }
            unbindTexturedModel(model);
        }
    }

    private void prepareEntity(GameObject gameObject) {
        for(ShaderProgram shaderProgram: shaderPrograms){
            shaderProgram.loadModelTransformation(gameObject);
        }
    }

    private void prepareForRendering(TexturedModel texturedModel) {
        glBindVertexArray(texturedModel.getVaoId());
        for (int attribute : texturedModel.getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        Texture texture = texturedModel.getTexture();
        if (texture != null) {
            for(ShaderProgram shaderProgram: shaderPrograms){
                shaderProgram.uploadTexture("fTexture",0);
                shaderProgram.uploadFloat("uReflectivity",texture.getReflectivity());
                shaderProgram.uploadFloat("uShineDampen", texture.getShineDampener());
            }
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(texture.getGlTexTarget(), texture.getGlTexId());
        }
    }

    private void unbindTexturedModel(TexturedModel texturedModel) {
        Texture texture = texturedModel.getTexture();
        if (texture != null) {
            glBindTexture(texture.getGlTexTarget(), 0);
        }
        for (int attribute : texturedModel.getAttributes()) {
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
    public void dispose(Map<TexturedModel, List<GameObject>> entities) {

    }
}
