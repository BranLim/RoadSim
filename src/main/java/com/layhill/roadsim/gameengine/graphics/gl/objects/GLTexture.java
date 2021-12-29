package com.layhill.roadsim.gameengine.graphics.gl.objects;

import static org.lwjgl.opengl.GL13.*;

public class GLTexture {

    private int textureId;
    private int target;

    public GLTexture(int textureId, int target) {
        this.textureId = textureId;
        this.target = target;
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
}
