package com.layhill.roadsim.gameengine.graphics.lights;

import com.layhill.roadsim.gameengine.graphics.gl.objects.*;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30.GL_COMPARE_REF_TO_TEXTURE;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

public class PointLightShadowMap extends ShadowMap {

    private final List<TextureTarget> cubemapTextureTargets = List.of(TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_X,
            TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_X,
            TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_Y,
            TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_Y,
            TextureTarget.TEXTURE_CUBE_MAP_POSITIVE_Z,
            TextureTarget.TEXTURE_CUBE_MAP_NEGATIVE_Z);

    public PointLightShadowMap(int shadowResolution) {
        super(shadowResolution);
    }

    @Override
    protected void createShadowTexture() {
        TextureBuilder textureBuilder = new TextureBuilder()
                .generateTexture(TextureType.TEXTURE_CUBE_MAP)
                .bindTexture()
                .size(resolution, resolution);

        for (TextureTarget target : cubemapTextureTargets) {
            textureBuilder.using2DImageTexture(target, 0, GL_DEPTH_COMPONENT16, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        }

        GLTexture texture = textureBuilder.withTextureParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                .withTextureParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                .withTextureParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                .withTextureParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
                .withTextureParameter(GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
                .build();
        setTexture(texture);
    }
}
