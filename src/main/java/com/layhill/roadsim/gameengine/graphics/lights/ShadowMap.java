package com.layhill.roadsim.gameengine.graphics.lights;

import com.layhill.roadsim.gameengine.graphics.gl.objects.FrameBuffer;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;

public abstract class ShadowMap {

    protected int size;
    private FrameBuffer frameBuffer;
    private GLTexture texture;

    protected ShadowMap(int size){
        this.size = size;
        createFrameBuffer();
    }

    public void bind(){
        frameBuffer.bind();
    }

    public void unbind(){
        frameBuffer.unbind();
    }

    public GLTexture getTexture(){
        return texture;
    }

    protected abstract void createFrameBuffer();

    public void dispose(){
        frameBuffer.dispose();
        texture.dispose();
    }

    protected void setFrameBuffer(FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    protected void setTexture(GLTexture texture) {
        this.texture = texture;
    }
}
