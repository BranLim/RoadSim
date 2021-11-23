package com.layhill.roadsim.gameengine.graphics;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    private int width;
    private int height;
    private int channel;
    private ByteBuffer image;

    public Texture(ByteBuffer image, int width, int height, int channel) {
        this.width = width;
        this.height = height;
        this.image = image;
        this.channel = channel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getImage(){
        return image;
    }

    public int getChannel() {
        return channel;
    }
}
