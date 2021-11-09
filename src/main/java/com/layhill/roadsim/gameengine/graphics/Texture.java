package com.layhill.roadsim.gameengine.graphics;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

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

    public void prepare(){
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    public void render(){
        switch (channel) {
            case 3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            case 4 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        }
    }

    public void unbind(){
        glBindTexture(target, 0);
    }

    public void dispose(){
        unbind();
        glDeleteTextures(texId);
    }
}
