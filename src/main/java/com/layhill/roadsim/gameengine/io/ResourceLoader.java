package com.layhill.roadsim.gameengine.io;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourceLoader {

    public static byte[] getResourceAsBytes(String filename) throws IOException {
        URL file = ResourceLoader.class.getClassLoader().getResource(filename);
        Path filePath = Paths.get(Objects.requireNonNull(file).getPath());
        return Files.readAllBytes(filePath);

    }
}
