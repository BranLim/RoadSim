package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

public enum TextureType {

    TEXTURE_1D(GL_TEXTURE_1D),
    TEXTURE_2D(GL_TEXTURE_2D),
    TEXTURE_1D_ARRAY(GL_TEXTURE_1D_ARRAY),
    TEXTURE_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
    TEXTURE_CUBE_MAP(GL_TEXTURE_CUBE_MAP);

    private final int target;

    TextureType(int target) {
        this.target = target;
    }

    public int getType() {
        return target;
    }
};
