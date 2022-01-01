package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL13.*;

public class GLTexture {

    private int textureId;
    private int target;
    private int width;
    private int height;

    public GLTexture(int textureId, int target, int width, int height) {
        this.textureId = textureId;
        this.target = target;
        this.width = width;
        this.height = height;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getTarget() {
        return target;
    }

    public void activate(int textureUnit){
        glActiveTexture(textureUnit);
        bind();
    }

    public void bind(){
        glBindTexture(target, textureId);
    }

    public void unbind(){
        glBindTexture(target, 0);
    }

    public void dispose() {
        glDeleteTextures(textureId);
    }
}
