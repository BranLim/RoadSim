package com.layhill.roadsim.gameengine.io;

import com.layhill.roadsim.gameengine.data.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeshLoader {

    private static final List<String> OBJ_DATA_PREFIXES = List.of("v", "vt", "vn", "vp", "f", "l");

    public static Mesh getMeshData() {
        List<Vector3f> meshData = List.of(
                new Vector3f(20.5f, -20.5f, 100.0f),
                new Vector3f(-20.5f, 20.5f, 100.0f),
                new Vector3f(20.5f, 20.5f, 100.0f),
                new Vector3f(-20.5f, -20.5f, 100.0f));
        List<Integer> meshIndices = List.of(
                2, 1, 0, // Top-right triangle
                0, 1, 3 //Bottom left triangle
        );
        return new Mesh(meshData, null, meshIndices);


    }

    public static Optional<Mesh> loadObjAsMesh(String file) throws IOException {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> vertexNormals = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Integer> vertexIndices = new ArrayList<>();

        Files.lines(Paths.get(file))
                .forEach(line -> {
                    String[] contents = line.split(" ");
                    if (OBJ_DATA_PREFIXES.stream().anyMatch(prefix -> contents[0].startsWith(prefix))) {
                        switch (contents[0].trim()) {
                            case "v" -> vertices.add(transformObj3DCoordinates(contents));
                            case "vn" -> vertexNormals.add(transformObj3DCoordinates(contents));
                        }
                    }

                });

        return Optional.of(new Mesh(vertices, vertexNormals, vertexIndices));
    }

    private static Vector3f transformObj3DCoordinates(String[] content) {
        if (content.length < 4) {
            return new Vector3f(0, 0, 0);
        }
        return new Vector3f(Float.parseFloat(content[1].trim()),
                Float.parseFloat(content[2].trim()),
                Float.parseFloat(content[3].trim()));
    }

}
