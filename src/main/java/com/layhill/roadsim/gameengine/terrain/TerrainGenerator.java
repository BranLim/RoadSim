package com.layhill.roadsim.gameengine.terrain;

import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.resources.ResourceManager;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

@Slf4j
public class TerrainGenerator {

    private static final float MAX_HEIGHT = 40;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

    public static Terrain generateTerrain(ResourceManager resourceManager, int worldX, int worldZ, String heightMapFile) {

        int VERTEX_COUNT = Terrain.VERTEX_COUNT_PER_SIDE;
        BufferedImage heightMap = loadHeightMap(heightMapFile);
        if (heightMap != null) {
            VERTEX_COUNT = heightMap.getHeight();
        }
        float[][] terrainHeight = new float[VERTEX_COUNT][VERTEX_COUNT];

        Mesh mesh = TerrainGenerator.generateTerrainMesh(VERTEX_COUNT, heightMap, terrainHeight);

        TexturedModel terrainModel = resourceManager.loadTexturedModel(mesh, "assets/textures/grass_texture.jpg", "Terrain");
        terrainModel.getMaterial().attachShaderProgram(ShaderFactory.createTerrainShaderProgram());

        return new Terrain(worldX, worldZ, terrainModel, terrainHeight);
    }

    private static BufferedImage loadHeightMap(String heightMapFile) {
        BufferedImage image = null;
        URL filePath = TerrainGenerator.class.getClassLoader().getResource(heightMapFile);
        Objects.requireNonNull(filePath);
        try {
            String path = filePath.getPath();
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            log.error("Cannot load height map : {} ", heightMapFile);
        }
        return image;
    }

    private static Mesh generateTerrainMesh(int vertexCount, BufferedImage heightMap, float[][] terrainHeight) {

        int totalVertexCount = vertexCount * vertexCount;
        float[] vertices = new float[totalVertexCount * 3];
        float[] normals = new float[totalVertexCount * 3];
        float[] textureCoordinates = new float[totalVertexCount * 2];
        int[] vertexIndices = new int[6 * (vertexCount - 1) * (vertexCount * 1)];

        int vertexPointer = 0;
        for (int x = 0; x < vertexCount; x++) {
            for (int z = 0; z < vertexCount; z++) {
                float height = heightMap == null ? 1 : getHeight(x, z, heightMap);

                vertices[vertexPointer * 3] = (float) z / ((float) vertexCount - 1) * Terrain.SIZE;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) x / ((float) vertexCount - 1) * Terrain.SIZE;

                terrainHeight[x][z] = height;

                Vector3f normal = heightMap == null ? new Vector3f(0.f, 1.f, 0.f) : calculateNormal(x,z, heightMap);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                textureCoordinates[vertexPointer * 2] = (float) z / ((float) vertexCount - 1);
                textureCoordinates[vertexPointer * 2 + 1] = (float) x / ((float) vertexCount - 1);

                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < vertexCount - 1; gz++) {
            for (int gx = 0; gx < vertexCount - 1; gx++) {
                int topLeft = (gz * vertexCount) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * vertexCount) + gx;
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

    private static float getHeight(int x, int y, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()) {
            return 0;
        }
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOUR / 2.f;
        height /= MAX_PIXEL_COLOUR / 2.f;
        height *= MAX_HEIGHT;
        return height;
    }

    private static Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightL = getHeight(x - 1, z, image);
        float heightR = getHeight(x + 1, z, image);
        float heightD = getHeight(x, z - 1, image);
        float heightU = getHeight(x, z + 1, image);

        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }
}
