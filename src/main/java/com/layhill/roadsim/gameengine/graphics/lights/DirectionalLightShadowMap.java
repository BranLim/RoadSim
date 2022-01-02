package com.layhill.roadsim.gameengine.graphics.lights;

import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.gl.objects.TextureBuilder;
import com.layhill.roadsim.gameengine.graphics.gl.objects.TextureType;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30.GL_COMPARE_REF_TO_TEXTURE;

public class DirectionalLightShadowMap extends ShadowMap {

    public DirectionalLightShadowMap(int resolution) {
        super(resolution);
    }

    @Override
    protected void createShadowTexture() {

        GLTexture texture = new TextureBuilder().generateTexture(TextureType.TEXTURE_2D)
                .bindTexture()
                .size(resolution, resolution)
                .using2DImageTexture(0, GL_DEPTH_COMPONENT16, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null)
                .withTextureParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                .withTextureParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                .withTextureParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                .withTextureParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
                .withTextureParameter(GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
                .build();
        setTexture(texture);
    }
}
