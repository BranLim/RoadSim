package com.layhill.roadsim.gameengine.entities;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.Texture;
import com.layhill.roadsim.gameengine.graphics.TextureFactory;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;

import java.util.Optional;

public class TerrainGenerator {

    public static Terrain generate(int startingX, int startingZ) {
        int totalVertexCount = Terrain.VERTEX_COUNT_PER_SIDE * Terrain.VERTEX_COUNT_PER_SIDE;
        float[] vertices = new float[totalVertexCount * 3];
        float[] normals = new float[totalVertexCount * 3];
        float[] textureCoordinates = new float[totalVertexCount * 2];
        int[] vertexIndices = new int[6 * (Terrain.VERTEX_COUNT_PER_SIDE - 1) * (Terrain.VERTEX_COUNT_PER_SIDE - 1)];

        int vertexPointer = 0;
        for (int x = 0; x < Terrain.VERTEX_COUNT_PER_SIDE; x++) {
            for (int z = 0; z < Terrain.VERTEX_COUNT_PER_SIDE; z++) {
                vertices[vertexPointer * 3] = -(float) z / ((float) Terrain.VERTEX_COUNT_PER_SIDE - 1) * Terrain.SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = -(float) x / ((float) Terrain.VERTEX_COUNT_PER_SIDE - 1) * Terrain.SIZE;

                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;

                textureCoordinates[vertexPointer * 2] = (float) z / ((float) Terrain.VERTEX_COUNT_PER_SIDE - 1);
                textureCoordinates[vertexPointer * 2 + 1] = (float) x / ((float) Terrain.VERTEX_COUNT_PER_SIDE - 1);

                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < Terrain.VERTEX_COUNT_PER_SIDE; gz++) {
            for (int gx = 0; gx < Terrain.VERTEX_COUNT_PER_SIDE; gx++) {
                int topLeft = (gz * Terrain.VERTEX_COUNT_PER_SIDE) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * Terrain.VERTEX_COUNT_PER_SIDE) + gx;
                int bottomRight = bottomLeft + 1;

                vertexIndices[pointer++] = topLeft;
                vertexIndices[pointer++] = bottomLeft;
                vertexIndices[pointer++] = topRight;
                vertexIndices[pointer++] = topRight;
                vertexIndices[pointer++] = bottomLeft;
                vertexIndices[pointer++] = bottomRight;
            }
        }

        Mesh mesh = new Mesh(vertices, normals, textureCoordinates, vertexIndices);

        Optional<Texture> texture = TextureFactory.loadAsTextureFromFile("", 0);

        TexturedModel texturedModel = texture.map(value -> new TexturedModel(-1, mesh, value)).orElse(null);

        return new Terrain(startingX, startingZ, texturedModel);
    }
}
