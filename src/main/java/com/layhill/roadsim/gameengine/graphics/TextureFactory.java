package com.layhill.roadsim.gameengine.graphics;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.stb.STBImage.stbi_load;

public class TextureFactory {

    public static Optional<Texture> loadAsTextureFromFile(String filepath, int textureTarget) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
        if (image == null) {
            return Optional.empty();
        }
        return Optional.of(new Texture(image, width.get(0), height.get(0), channels.get(0), textureTarget));

    }
}