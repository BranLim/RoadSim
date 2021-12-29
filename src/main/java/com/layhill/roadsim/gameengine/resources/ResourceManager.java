package com.layhill.roadsim.gameengine.resources;

import com.layhill.roadsim.gameengine.graphics.RawTexture;
import com.layhill.roadsim.gameengine.graphics.mesh.Mesh;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@Slf4j
public final class ResourceManager {

    private final Map<String, TexturedModel> texturedModels = new HashMap<>();
    private GLResourceLoader resourceLoader = GLResourceLoader.getInstance();

    public ResourceManager() {

    }

    public TexturedModel loadTexturedModel(String modelFilePath, String textureFilePath, String referenceName) {
        Objects.requireNonNull(referenceName);
        Objects.requireNonNull(modelFilePath);
        Optional<Mesh> meshOpt = MeshLoader.loadObjAsMesh(modelFilePath);
        Optional<Material> materialOpt = getMaterialFromTexture(textureFilePath);
        if (meshOpt.isPresent() && materialOpt.isPresent()) {
            GLModel glModel = resourceLoader.loadToVao(meshOpt.get());
            TexturedModel texturedModel = new TexturedModel(glModel, materialOpt.orElse(null));
            texturedModels.put(referenceName, texturedModel);
            return texturedModel;
        }

        return null;
    }

    public TexturedModel loadTexturedModel(Mesh mesh, String textureFilePath, String referenceName) {
        Objects.requireNonNull(referenceName);
        Objects.requireNonNull(mesh);
        GLModel glModel = resourceLoader.loadToVao(mesh);
        Optional<Material> materialOpt = getMaterialFromTexture(textureFilePath);
        TexturedModel texturedModel = new TexturedModel(glModel, materialOpt.orElse(null));
        texturedModels.put(referenceName, texturedModel);
        return texturedModel;
    }

    private Optional<Material> getMaterialFromTexture(String textureFilePath) {

        Optional<RawTexture> textureOpt = TextureLoader.loadAsTextureFromFile(textureFilePath);
        if (textureOpt.isPresent()) {
            RawTexture rawTexture = textureOpt.get();
            GLTexture glTexture = resourceLoader.load2DTexture(rawTexture, GL_TEXTURE_2D, true, true, 16);
            return Optional.of(new Material(glTexture));
        }
        return Optional.empty();
    }

    public void dispose() {
        texturedModels.clear();
        resourceLoader.dispose();
    }

}
