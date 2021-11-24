package com.layhill.roadsim.gameengine.terrain;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.resources.ResourceManager;

import java.util.List;

public class TerrainGenerator {

    public static List<Terrain> generateTerrains(ResourceManager resourceManager) {

        Mesh mesh = TerrainGenerator.generateTerrainMesh();
        TexturedModel terrainModel = resourceManager.loadTexturedModel(mesh, "assets/textures/grass_texture.jpg", "Terrain");
        terrainModel.getMaterial().attachShaderProgram(ShaderFactory.createTerrainShaderProgram());

        return List.of(new Terrain(0, 0, terrainModel),
                new Terrain(0, 1, terrainModel),
                new Terrain(1, 0, terrainModel),
                new Terrain(1, 1, terrainModel));
    }

    public static Mesh generateTerrainMesh() {
        int totalVertexCount = Terrain.VERTEX_COUNT_PER_SIDE * Terrain.VERTEX_COUNT_PER_SIDE;
        float[] vertices = new float[totalVertexCount * 3];
        float[] normals = new float[totalVertexCount * 3];
        float[] textureCoordinates = new float[totalVertexCount * 2];
        int[] vertexIndices = new int[6 * (Terrain.VERTEX_COUNT_PER_SIDE - 1) * (Terrain.VERTEX_COUNT_PER_SIDE * 1)];

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
        for (int gz = 0; gz < Terrain.VERTEX_COUNT_PER_SIDE - 1; gz++) {
            for (int gx = 0; gx < Terrain.VERTEX_COUNT_PER_SIDE - 1; gx++) {
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
        return new Mesh(vertices, normals, textureCoordinates, vertexIndices);
    }
}
