package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.utils.Transformation;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterShaderProgram;
import com.layhill.roadsim.gameengine.water.WaterTile;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLWaterRenderer implements Renderer {

    private final static float[] QUAD_VERTICES = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
    private GLModel waterQuad;
    private WaterShaderProgram shaderProgram;

    public GLWaterRenderer(GLResourceLoader loader) {
        waterQuad = loader.loadToVao(QUAD_VERTICES, 2);
        shaderProgram = ShaderFactory.createWaterShaderProgram();
    }

    @Override
    public void prepare() {
    }

    @Override
    public void render(long window, Camera camera, RendererData rendererData) {
        startRendering(camera, rendererData);
        for (WaterTile tile : rendererData.getWaterTiles()) {
            Matrix4f modelTransformation = Transformation.createTransformationMatrix(new Vector3f(tile.getX(),
                    tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE * 10);
            shaderProgram.loadModelTransformation(modelTransformation);
            glDrawArrays(GL_TRIANGLES, 0, waterQuad.getVertexCount());
        }
        endRendering();
    }

    private void startRendering(Camera camera, RendererData rendererData) {
        shaderProgram.start();
        shaderProgram.loadCamera(camera);

        glBindVertexArray(waterQuad.getVaoId());
        for (int attribute : waterQuad.getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        WaterFrameBuffer frameBuffer = rendererData.getWaterFrameBuffer();
        if (frameBuffer != null) {

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, frameBuffer.getReflectionTexture());

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, frameBuffer.getRefractionTexture());

            shaderProgram.loadReflectionTexture(0);
            shaderProgram.loadRefractionTexture(1);
        }
    }

    private void endRendering() {
        for (int attribute : waterQuad.getAttributes()) {
            glDisableVertexAttribArray(attribute);
        }
        glBindVertexArray(0);
        shaderProgram.stop();
    }

    @Override
    public void dispose(RendererData rendererData) {

    }
}
