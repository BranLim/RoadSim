package com.layhill.roadsim.gameengine.resources;

import com.layhill.roadsim.gameengine.graphics.models.Mesh;
import com.layhill.roadsim.gameengine.graphics.models.Material;
import com.layhill.roadsim.gameengine.graphics.Texture;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLModel;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@Slf4j
public final class ResourceManager {

    private final Map<String, TexturedModel> texturedModels = new HashMap<>();
    private GLResourceLoader resourceLoader = new GLResourceLoader();

    public ResourceManager() {

    }

    public TexturedModel loadTexturedModel(String modelFilePath, String textureFilePath, String referenceName) {
        Optional<Mesh> meshOpt = MeshLoader.loadObjAsMesh(modelFilePath);
        Optional<Texture> textureOpt = TextureLoader.loadAsTextureFromFile(textureFilePath);
        if (meshOpt.isPresent() && textureOpt.isPresent()) {
            GLModel rawModel = resourceLoader.loadToVao(meshOpt.get());
            Texture texture = textureOpt.get();

            GLTexture rawTexture = resourceLoader.loadTexture(texture, GL_TEXTURE_2D);
            Material material = new Material(rawTexture);
            TexturedModel texturedModel = new TexturedModel(rawModel, material);
            texturedModels.put(referenceName, texturedModel);
            return texturedModel;
        }

        return null;
    }

    public void dispose(){
        texturedModels.clear();
        resourceLoader.dispose();
    }

}
