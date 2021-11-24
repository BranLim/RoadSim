package com.layhill.roadsim.gameengine.io;

import com.layhill.roadsim.gameengine.graphics.RawTexture;
import org.lwjgl.BufferUtils;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.stb.STBImage.stbi_load;

public class TextureLoader {

    public static Optional<RawTexture> loadAsTextureFromFile(String filepath) {
        URL file = TextureLoader.class.getClassLoader().getResource(filepath);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(Objects.requireNonNull(file).getPath(), width, height, channels, 0);
        if (image == null) {
            return Optional.empty();
        }
        return Optional.of(new RawTexture(image, width.get(0), height.get(0), channels.get(0)));
    }
}