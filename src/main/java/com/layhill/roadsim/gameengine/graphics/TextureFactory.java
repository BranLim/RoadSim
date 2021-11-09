package com.layhill.roadsim.gameengine.graphics;

import com.layhill.roadsim.gameengine.io.ResourceLoader;
import org.lwjgl.BufferUtils;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class TextureFactory {

    public static Optional<Texture> loadAsTextureFromFile(String filepath, int textureTarget) {
        URL file = ResourceLoader.class.getClassLoader().getResource(filepath);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(Objects.requireNonNull(file).getPath(), width, height, channels, 0);
        if (image == null) {
            return Optional.empty();
        }
        stbi_image_free(image);
        return Optional.of(new Texture(image, width.get(0), height.get(0), channels.get(0), textureTarget));

    }
}