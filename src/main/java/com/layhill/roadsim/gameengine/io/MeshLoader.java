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

        List<Vector3f> processedVertices = new ArrayList<>();

        List<Vector3f> preprocessedVertexNormals = new ArrayList<>();
        List<Vector2f> preprocessedTextureCoords = new ArrayList<>();
        List<String> preprocessedMeshFaceMappings = new ArrayList<>();
        try (var lines = Files.lines(Paths.get(file))) {
            lines.forEach(line -> {
                try {
                    if (OBJ_DATA_PREFIXES.stream().anyMatch(line::startsWith)) {
                        String[] contents = line.split(" ");
                        switch (contents[0].trim()) {
                            case "v" -> processedVertices.add(transformObj3DCoordinates(contents));
                            case "vn" -> preprocessedVertexNormals.add(transformObj3DCoordinates(contents));
                            case "vt" -> preprocessedTextureCoords.add(transformObj2DCoordinates(contents));
                            case "f" -> preprocessedMeshFaceMappings.add(line.substring(line.indexOf("f") + 1));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Invalid entry in obj file: {}", file);
                    log.error(e.getMessage());
                }
            });
        }

        float[] sortedVertexNormals = new float[preprocessedVertexNormals.size()*3];
        float[] sortedTextureCoordinates = new float[preprocessedTextureCoords.size()*2];
        List<Integer> vertexIndices = new ArrayList<>();
        for (String mappingLine : preprocessedMeshFaceMappings) {
            String [] perVertexMappings = mappingLine.split(" ");
            for(String mapping: perVertexMappings){
                int vertexPointer = getVertexPointerBasedOnVertexMapping(mapping);
                vertexIndices.add(vertexPointer);

            }

        }

        List<Vector3f> postProcessedVertexNormals = new ArrayList<>();
        List<Vector2f> postProcessedTextureCoords = new ArrayList<>();
        return Optional.of(new Mesh(processedVertices, postProcessedVertexNormals, vertexIndices));
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

    private static int getVertexPointerBasedOnVertexMapping(String vertexMappings) {
        String[] vertexMappingComponents = vertexMappings.split("/");
        return Integer.parseInt(vertexMappingComponents[0]) - 1;
    }
}
