package com.layhill.roadsim.gameengine.data;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class Mesh {

    private List<Vector3f> vertices;
    private List<Vector3f> vertexNormals;
    private List<Integer> verticesIndex;

    public Mesh(List<Vector3f> vertices, List<Vector3f> vertexNormals, List<Integer> verticesIndex) {
        this.vertices = vertices;
        this.vertexNormals = vertexNormals;
        this.verticesIndex = verticesIndex;
    }

    public FloatBuffer verticesToFloatBuffer() {
        return pointsToFloatBuffer(vertices);
    }

    public FloatBuffer vertexNormalsToFloatBuffer() {
        return pointsToFloatBuffer(vertexNormals);
    }

    public IntBuffer vertexIndicesToIntBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(verticesIndex.size());
        buffer.put(verticesIndex.stream().mapToInt(i -> i).toArray());
        buffer.flip();
        return buffer;
    }

    private FloatBuffer pointsToFloatBuffer(List<Vector3f> points) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(points.size() * 3);
        for (var point : points) {
            buffer.put(point.x);
            buffer.put(point.y);
            buffer.put(point.z);
        }
        buffer.flip();
        return buffer;
    }
}
