package com.layhill.roadsim.gameengine.graphics.gl.objects;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureBuilder {

    private TextureType textureType;
    private int textureId;
    private int width;
    private int height;
    private boolean isBuilding;

    public TextureBuilder() {

    }

    public TextureBuilder generateTexture(TextureType textureType) {
        if (isBuilding) {
            throw new IllegalStateException("Still building texture");
        }
        isBuilding = true;
        textureId = glGenTextures();
        this.textureType = textureType;
        return this;
    }

    public TextureBuilder bindTexture() {
        checkIsBuilding();
        glBindTexture(textureType.getType(), textureId);
        return this;
    }

    public TextureBuilder size(int width, int height) {
        checkIsBuilding();
        this.width = width;
        this.height = height;
        return this;
    }

    public TextureBuilder using2DImageTexture(int levelOfDetail, int internalFormat, int border, int format, int type, @Nullable ByteBuffer pixels) {
        checkIsBuilding();
        glTexImage2D(textureType.getType(), levelOfDetail, internalFormat, width, height, border, format, type, pixels);
        return this;
    }

    public TextureBuilder withTextureParameter(int parameterName, int value) {
        checkIsBuilding();
        glTexParameteri(textureType.getType(), parameterName, value);
        return this;
    }

    public TextureBuilder withTextureParameter(int parameterName, float value) {
        checkIsBuilding();
        glTexParameterf(textureType.getType(), parameterName, value);
        return this;
    }

    public TextureBuilder withTextureParameter(int parameterName, int[] values) {
        checkIsBuilding();
        glTexParameteriv(textureType.getType(), parameterName, values);
        return this;
    }

    public TextureBuilder withTextureParameter(int parameterName, float[] values) {
        checkIsBuilding();
        glTexParameterfv(textureType.getType(), parameterName, values);
        return this;
    }

    public TextureBuilder generateMipmap() {
        checkIsBuilding();
        glGenerateMipmap(textureType.getType());
        return this;
    }

    public GLTexture build() {
        return new GLTexture(textureType.getType(), textureId, width, height);
    }

    private void checkIsBuilding() {
        if (!isBuilding) {
            throw new IllegalStateException("Texture is not under construction");
        }
    }
}
