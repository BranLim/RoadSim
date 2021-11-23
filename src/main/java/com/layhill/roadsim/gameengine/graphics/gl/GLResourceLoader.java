package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.entities.Material;
import com.layhill.roadsim.gameengine.graphics.Texture;
import com.layhill.roadsim.gameengine.resources.ResourceLoader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class GLResourceLoader {

    private static final int TRIANGLE_ATTRIBUTE_POSITION = 0;
    private static final int TEXTURE_COORDINATE_ATTRIBUTE_POSITION = 1;
    private static final int VERTEX_INDICES_ATTRIBUTE_POSITION = 2;

    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();

    public GLResourceLoader(){

    }

    public Optional<TexturedModel> loadToVao(Mesh mesh, List<Texture> textures){
        Objects.requireNonNull(mesh);
        List<Integer> attributes = new ArrayList<>();
        int vaoId = glGenVertexArrays();
        vaos.add(vaoId);

        glBindVertexArray(vaoId);

        int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        glBufferData(GL_ARRAY_BUFFER, mesh.verticesToFloatBuffer(), GL_STATIC_DRAW);
        glVertexAttribPointer(TRIANGLE_ATTRIBUTE_POSITION, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(TRIANGLE_ATTRIBUTE_POSITION);
        attributes.add(TRIANGLE_ATTRIBUTE_POSITION);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        glBufferData(GL_ARRAY_BUFFER, mesh.textureCoordinatesToFloatBuffer(), GL_STATIC_DRAW);
        glVertexAttribPointer(TEXTURE_COORDINATE_ATTRIBUTE_POSITION, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(TEXTURE_COORDINATE_ATTRIBUTE_POSITION);
        attributes.add(TEXTURE_COORDINATE_ATTRIBUTE_POSITION);

        bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        vbos.add(bufferId);

        glBufferData(GL_ARRAY_BUFFER, mesh.vertexIndicesToIntBuffer(), GL_STATIC_DRAW);
        glVertexAttribPointer(TEXTURE_COORDINATE_ATTRIBUTE_POSITION, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(VERTEX_INDICES_ATTRIBUTE_POSITION);
        attributes.add(VERTEX_INDICES_ATTRIBUTE_POSITION);

        glBindVertexArray(0);
        return Optional.of(new TexturedModel(vaoId, attributes , mesh, null));
    }


    public Material loadTexture(Texture texture){

        int textureId = glGenTextures();
        glBindTexture(texture.getGlTexTarget(), textureId);

        return new Material();

    }

}
