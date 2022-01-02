package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.*;

public enum TextureTarget {

    TEXTURE_1D(GL_TEXTURE_1D),
    TEXTURE_2D(GL_TEXTURE_2D),
    TEXTURE_CUBE_MAP_POSITIVE_X(GL_TEXTURE_CUBE_MAP_POSITIVE_X),
    TEXTURE_CUBE_MAP_NEGATIVE_X(GL_TEXTURE_CUBE_MAP_NEGATIVE_X),
    TEXTURE_CUBE_MAP_POSITIVE_Y(GL_TEXTURE_CUBE_MAP_POSITIVE_Y),
    TEXTURE_CUBE_MAP_NEGATIVE_Y(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y),
    TEXTURE_CUBE_MAP_POSITIVE_Z(GL_TEXTURE_CUBE_MAP_POSITIVE_Z),
    TEXTURE_CUBE_MAP_NEGATIVE_Z(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);

    private final int target;

    TextureTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}

