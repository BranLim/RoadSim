package com.layhill.roadsim.gameengine.graphics.lights;

import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.gl.objects.TextureBuilder;
import com.layhill.roadsim.gameengine.graphics.gl.objects.TextureType;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;

public class DirectionalLightShadowMap extends ShadowMap {

    public DirectionalLightShadowMap(int size) {
        super(size);
    }


    @Override
    protected void createFrameBuffer() {

        GLTexture texture = new TextureBuilder().generateTexture(TextureType.TEXTURE_2D)
                .bindTexture()
                .using2DImageTexture(0, GL_DEPTH_COMPONENT16,0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null)
                .withTextureParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                .withTextureParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                .build();
        setTexture(texture);
//setFrameBuffer();

    }
}
