package com.layhill.roadsim.gameengine.io;

import com.layhill.roadsim.gameengine.data.Mesh;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MeshLoader {

    private static final List<String> OBJ_DATA_PREFIXES = List.of("v", "vt", "vn", "vp", "f", "l");

    public static Optional<Mesh> loadObjAsMesh(String file) throws IOException {
        URL filePath = ResourceLoader.class.getClassLoader().getResource(file);
        List<Vector3f> processedVertices = new ArrayList<>();

        List<Vector3f> preprocessedVertexNormals = new ArrayList<>();
        List<Vector2f> preprocessedTextureCoords = new ArrayList<>();
        List<String> preprocessedMeshFaceMappings = new ArrayList<>();
        try (var lines = Files.lines(Paths.get(filePath.getPath()))) {
            lines.forEach(line -> {
                try {
                    if (OBJ_DATA_PREFIXES.stream().anyMatch(line::startsWith)) {
                        String[] contents = line.split(" ");
                        switch (contents[0].trim()) {
                            case "v" -> processedVertices.add(transformObj3DCoordinates(contents));
                            case "vn" -> preprocessedVertexNormals.add(transformObj3DCoordinates(contents));
                            case "vt" -> preprocessedTextureCoords.add(transformObj2DCoordinates(contents));
                            case "f" -> preprocessedMeshFaceMappings.add(line.substring(line.indexOf("f") + 2));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Invalid entry in obj file: {}", file);
                    log.error(e.getMessage());
                }
            });
        }

        float[] sortedVertexNormals = new float[processedVertices.size() * 3];
        float[] sortedTextureCoordinates = new float[processedVertices.size() * 2];
        List<Integer> vertexIndices = generateVertexIndicesAndSortVertexNormalsAndTextureCoordinates(preprocessedVertexNormals,
                preprocessedTextureCoords, preprocessedMeshFaceMappings, sortedVertexNormals, sortedTextureCoordinates);

        List<Vector3f> postProcessedVertexNormals = new ArrayList<>();
        List<Vector2f> postProcessedTextureCoords = new ArrayList<>();

        for (int i = 0; i < sortedVertexNormals.length; i += 3) {
            postProcessedVertexNormals.add(new Vector3f(sortedVertexNormals[i], sortedVertexNormals[i + 1], sortedVertexNormals[i + 2]));
        }

        for (int i = 0; i < sortedTextureCoordinates.length; i += 2) {
            postProcessedTextureCoords.add(new Vector2f(sortedTextureCoordinates[i], sortedTextureCoordinates[i + 1]));
        }

        return Optional.of(new Mesh(processedVertices, postProcessedVertexNormals, postProcessedTextureCoords, vertexIndices));
    }

    private static List<Integer> generateVertexIndicesAndSortVertexNormalsAndTextureCoordinates(List<Vector3f> preprocessedVertexNormals,
                                                                                                List<Vector2f> preprocessedTextureCoords,
                                                                                                List<String> preprocessedMeshFaceMappings,
                                                                                                float[] sortedVertexNormals,
                                                                                                float[] sortedTextureCoordinates) {
        List<Integer> vertexIndices = new ArrayList<>();
        for (String mappingLine : preprocessedMeshFaceMappings) {
            String[] perVertexMappings = mappingLine.split(" ");
            for (String mapping : perVertexMappings) {
                String[] vertexMappingComponents = mapping.split("/");
                int vertexPointer = getVertexPointerBasedOnVertexMapping(vertexMappingComponents[0]);
                vertexIndices.add(vertexPointer);
                extractTextureCoordinates(vertexMappingComponents[1], preprocessedTextureCoords, vertexPointer, sortedTextureCoordinates);
                extractVertexNormal(vertexMappingComponents[2], preprocessedVertexNormals, vertexPointer, sortedVertexNormals);
            }
        }
        return vertexIndices;
    }

    private static void extractTextureCoordinates(String vertexMappingComponent,
                                                  List<Vector2f> preprocessedTextureCoords,
                                                  int vertexPointer,
                                                  float[] sortedTextureCoordinates) {
        Vector2f currentTexCoord = preprocessedTextureCoords.get(Integer.parseInt(vertexMappingComponent) - 1);
        sortedTextureCoordinates[vertexPointer * 2] = currentTexCoord.x;
        sortedTextureCoordinates[vertexPointer * 2 + 1] = 1 - currentTexCoord.y;
    }

    private static void extractVertexNormal(String vertexMappingComponent,
                                            List<Vector3f> preprocessedVertexNormals,
                                            int vertexPointer,
                                            float[] sortedVertexNormals) {
        Vector3f currentNormal = preprocessedVertexNormals.get(Integer.parseInt(vertexMappingComponent) - 1);
        sortedVertexNormals[vertexPointer * 3] = currentNormal.x;
        sortedVertexNormals[vertexPointer * 3 + 1] = currentNormal.y;
        sortedVertexNormals[vertexPointer * 3 + 2] = currentNormal.z;
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

    private static int getVertexPointerBasedOnVertexMapping(String vertexMappingComponent) {
        return Integer.parseInt(vertexMappingComponent) - 1;
    }
}
