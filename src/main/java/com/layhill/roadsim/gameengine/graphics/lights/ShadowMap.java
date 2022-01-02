package com.layhill.roadsim.gameengine.graphics.lights;

import com.layhill.roadsim.gameengine.graphics.FrameBufferMode;
import com.layhill.roadsim.gameengine.graphics.gl.objects.FrameBuffer;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;

public abstract class ShadowMap {

    protected int resolution;
    private FrameBuffer frameBuffer;
    private GLTexture texture;

    protected ShadowMap(int resolution){
        this.resolution = resolution;
        createFrameBuffer();
    }

    public void bind(){
        frameBuffer.bind();
    }

    public void bind(FrameBufferMode mode){
        frameBuffer.bind(mode);
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

    public void resize(int shadowResolution) {
        this.resolution = shadowResolution;
        dispose();
        createFrameBuffer();
    }
}
