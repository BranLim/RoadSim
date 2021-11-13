package com.layhill.roadsim.gameengine.data;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class Mesh {

    private List<Vector3f> vertices;
    private List<Vector3f> vertexNormals;
    private List<Integer> vertexIndices;
    private int vertexCount;

    public Mesh(List<Vector3f> vertices, List<Vector3f> vertexNormals, List<Integer> vertexIndices) {
        this.vertices = vertices;
        this.vertexNormals = vertexNormals;
        this.vertexIndices = vertexIndices;
        if (vertexIndices == null) {
            vertexCount = vertices.size() * 3;
        } else {
            vertexCount = vertexIndices.size();
        }
    }

    public FloatBuffer verticesToFloatBuffer() {
        return pointsToFloatBuffer(vertices);
    }

    public FloatBuffer vertexNormalsToFloatBuffer() {
        return pointsToFloatBuffer(vertexNormals);
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

    public int getVertexCount() {
        return vertexCount;
    }

    public boolean hasVertexNormals(){
        return vertexNormals !=null && !vertexNormals.isEmpty();
    }

    public boolean hasVertexIndices(){
        return vertexIndices !=null && !vertexIndices.isEmpty();
    }
}
