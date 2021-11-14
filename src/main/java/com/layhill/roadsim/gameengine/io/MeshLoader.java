package com.layhill.roadsim.gameengine.io;

import com.layhill.roadsim.gameengine.data.Mesh;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
        List<Vector3f> preprocessedVertices = new ArrayList<>();
        List<Vector3f> preprocessedVertexNormals = new ArrayList<>();
        List<Vector2f> preprocessedTextureCoords = new ArrayList<>();
        List<Integer> vertexIndices = new ArrayList<>();
        List<String> preprocessedMeshFaceMappings = new ArrayList<>();

        List<Vector3f> postProcessedVertices = new ArrayList<>();
        List<Vector3f> postProcessedVertexNormals = new ArrayList<>();
        List<Vector2f> postProcessedTextureCoords = new ArrayList<>();


        try (var lines = Files.lines(Paths.get(file))) {
            lines.forEach(line -> {
                try {
                    String[] contents = line.split(" ");
                    if (OBJ_DATA_PREFIXES.stream().anyMatch(prefix -> contents[0].startsWith(prefix))) {
                        switch (contents[0].trim()) {
                            case "v" -> preprocessedVertices.add(transformObj3DCoordinates(contents));
                            case "vn" -> preprocessedVertexNormals.add(transformObj3DCoordinates(contents));
                            case "vt" -> preprocessedTextureCoords.add(transformObj2DCoordinates(contents));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Invalid entry in obj file: {}", file);
                    log.error(e.getMessage());
                }
            });
        }
        return Optional.of(new Mesh(postProcessedVertices, postProcessedVertices, vertexIndices));
    }

    private static Vector2f transformObj2DCoordinates(String[] contents) {
        if (contents.length < 3) {
            throw new IllegalArgumentException(String.format("Line content { %s } is invalid", String.join("", contents)));
        }
        return new Vector2f(Float.parseFloat(contents[1].trim()), Float.parseFloat(contents[2].trim()));
    }

    private static Vector3f transformObj3DCoordinates(String[] contents) {
        if (contents.length < 4) {
            throw new IllegalArgumentException(String.format("Line content { %s } is invalid", String.join("", contents)));
        }
        return new Vector3f(Float.parseFloat(contents[1].trim()),
                Float.parseFloat(contents[2].trim()),
                Float.parseFloat(contents[3].trim()));
    }

}
