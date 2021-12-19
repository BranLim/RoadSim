package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.Time;
import com.layhill.roadsim.gameengine.graphics.RawTexture;
import com.layhill.roadsim.gameengine.graphics.Renderer;
import com.layhill.roadsim.gameengine.graphics.RendererData;
import com.layhill.roadsim.gameengine.graphics.ViewSpecification;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.utils.Maths;
import com.layhill.roadsim.gameengine.water.WaterFrameBuffer;
import com.layhill.roadsim.gameengine.water.WaterShaderProgram;
import com.layhill.roadsim.gameengine.water.WaterTile;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.Optional;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLWaterRenderer implements Renderer {

    private final static float[] QUAD_VERTICES = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
    private final static float WAVE_SPEED = 0.008f;

    private GLModel waterQuad;
    private GLTexture waterDuDv;
    private GLTexture waterNormalMap;
    private WaterShaderProgram shaderProgram;
    private float waveMove;

    public GLWaterRenderer(GLResourceLoader loader) {
        waterQuad = loader.loadToVao(QUAD_VERTICES, 2);
        Optional<RawTexture> textureOpt = TextureLoader.loadAsTextureFromFile("assets/textures/waterDuDv.png");
        if (textureOpt.isPresent()) {
            RawTexture rawTexture = textureOpt.get();
            waterDuDv = loader.load2DTexture(rawTexture, GL_TEXTURE_2D, true);
        }

        Optional<RawTexture> normalTextureOpt = TextureLoader.loadAsTextureFromFile("assets/textures/waterNormalMap.png");
        if (normalTextureOpt.isPresent()) {
            RawTexture rawTexture = normalTextureOpt.get();
            waterNormalMap = loader.load2DTexture(rawTexture, GL_TEXTURE_2D, true);
        }
        shaderProgram = ShaderFactory.createWaterShaderProgram();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void render(ViewSpecification viewSpecification, RendererData rendererData) {
        startRendering(viewSpecification, rendererData);
        for (WaterTile tile : rendererData.getWaterTiles()) {
            Matrix4f modelTransformation = Maths.createTransformationMatrix(new Vector3f(tile.getX(),
                    tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE * 10);
            shaderProgram.loadModelTransformation(modelTransformation);
            glDrawArrays(GL_TRIANGLES, 0, waterQuad.getVertexCount());
        }
        endRendering();
    }

    private void startRendering(ViewSpecification viewSpecification, RendererData rendererData) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        shaderProgram.start();
        shaderProgram.loadCamera(viewSpecification);
        shaderProgram.loadCameraPosition(rendererData.getCameraPosition());
        shaderProgram.loadNearPlane(rendererData.getNearPlane());
        shaderProgram.loadFarPlane(rendererData.getFarPlane());
        shaderProgram.loadSun(rendererData.getSun());

        glBindVertexArray(waterQuad.getVaoId());
        for (int attribute : waterQuad.getAttributes()) {
            glEnableVertexAttribArray(attribute);
        }
        waveMove+= WAVE_SPEED * Time.getInstance().getDeltaTime();
        waveMove %= 1;

        WaterFrameBuffer frameBuffer = rendererData.getWaterFrameBuffer();
        if (frameBuffer != null) {

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, frameBuffer.getReflectionTexture());

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, frameBuffer.getRefractionTexture());

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, waterDuDv.getTextureId());

            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, waterNormalMap.getTextureId());

            glActiveTexture(GL_TEXTURE4);
            glBindTexture(GL_TEXTURE_2D, frameBuffer.getRefractionDepthTexture());

            shaderProgram.loadReflectionTexture(0);
            shaderProgram.loadRefractionTexture(1);
            shaderProgram.loadDuDvTexture(2);
            shaderProgram.loadNormalMap(3);
            shaderProgram.loadDepthMap(4);
            shaderProgram.loadWaveOffset(waveMove);
        }

    }

    private void endRendering() {
        for (int attribute : waterQuad.getAttributes()) {
            glDisableVertexAttribArray(attribute);
        }
        glBindVertexArray(0);
        shaderProgram.stop();
        glDisable(GL_BLEND);
    }

    @Override
    public void dispose(RendererData rendererData) {

    }
}
