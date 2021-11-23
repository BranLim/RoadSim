package com.layhill.roadsim.gameengine.graphics.models;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Mesh {

    private List<Vector3f> vertices;
    private List<Vector3f> vertexNormals;
    private List<Vector2f> textureCoordinates;
    private List<Integer> vertexIndices;
    private int vertexCount;

    public Mesh(List<Vector3f> vertices, List<Vector3f> vertexNormals, List<Vector2f> textureCoordinates,
                List<Integer> vertexIndices) {
        this.vertices = vertices;
        this.vertexNormals = vertexNormals;
        this.textureCoordinates = textureCoordinates;
        this.vertexIndices = vertexIndices;
        if (vertexIndices == null) {
            vertexCount = vertices.size() * 3;
        } else {
            vertexCount = vertexIndices.size();
        }
    }

    public Mesh(float[] vertices, float[] normals, float[] textureCoordinates, int[] vertexIndices) {
        this.vertices = new ArrayList<>();
        vertexNormals = new ArrayList<>();
        this.textureCoordinates = new ArrayList<>();

        for (int i = 0; i < vertices.length; i += 3) {
            this.vertices.add(new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
        }

        for (int i = 0; i < normals.length; i += 3) {
            vertexNormals.add(new Vector3f(normals[i], normals[i + 1], normals[i + 2]));
        }

        for (int i = 0; i < textureCoordinates.length; i += 2) {
            this.textureCoordinates.add(new Vector2f(textureCoordinates[i], textureCoordinates[i + 1]));
        }

        if (vertexIndices != null) {
            this.vertexIndices = Arrays.stream(vertexIndices).boxed().collect(Collectors.toList());
        }
    }

    public FloatBuffer verticesToFloatBuffer() {
        return convert3DPointsToFloatBuffer(vertices);
    }

    public FloatBuffer textureCoordinatesToFloatBuffer() {
        return convert2DPointsToFloatBuffer(textureCoordinates);
    }

    private FloatBuffer convert2DPointsToFloatBuffer(List<Vector2f> textureCoordinates) {

        FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(textureCoordinates.size() * 2);
        for (var textureCoordinate : textureCoordinates) {
            uvBuffer.put(textureCoordinate.x);
            uvBuffer.put(textureCoordinate.y);
        }
        uvBuffer.flip();
        return uvBuffer;
    }

    public FloatBuffer vertexNormalsToFloatBuffer() {
        return convert3DPointsToFloatBuffer(vertexNormals);
    }

    public IntBuffer vertexIndicesToIntBuffer() {
        if (vertexIndices == null) {
            return BufferUtils.createIntBuffer(0);
        }
        IntBuffer buffer = BufferUtils.createIntBuffer(vertexIndices.size());
        buffer.put(vertexIndices.stream().mapToInt(i -> i).toArray());
        buffer.flip();
        return buffer;
    }

    private FloatBuffer convert3DPointsToFloatBuffer(List<Vector3f> points) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(points.size() * 3);
        for (var point : points) {
            buffer.put(point.x);
            buffer.put(point.y);
            buffer.put(point.z);
        }
        buffer.flip();
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public boolean hasVertexNormals() {
        return vertexNormals != null && !vertexNormals.isEmpty();
    }

    public boolean hasVertexIndices() {
        return vertexIndices != null && !vertexIndices.isEmpty();
    }
}
