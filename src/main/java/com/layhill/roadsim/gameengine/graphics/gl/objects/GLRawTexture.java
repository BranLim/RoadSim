package com.layhill.roadsim.gameengine.graphics.gl.objects;

public class GLRawTexture {

    private int textureId;
    private int target;

    public GLRawTexture(int textureId, int target) {
        this.textureId = textureId;
        this.target = target;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getTarget() {
        return target;
    }
}
