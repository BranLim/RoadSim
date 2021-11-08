package com.layhill.roadsim.gameengine.graphics;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;

public class Texture {

    private int texId;
    private int target;
    private int width;
    private int height;
    private int channel;
    private ByteBuffer image;

    protected Texture(ByteBuffer image, int width, int height, int channel, int target) {
        this.width = width;
        this.height = height;
        this.image = image;
        this.channel = channel;
        this.target = target;
    }

    public void generate(){
        texId = glGenTextures();
    }

    public void bind(){
        glBindTexture(target, texId);
    }

    public void unbind(){
        glBindTexture(target, 0);
    }

}
