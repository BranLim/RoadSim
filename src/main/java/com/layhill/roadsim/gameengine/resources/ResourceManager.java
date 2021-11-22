package com.layhill.roadsim.gameengine.resources;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import com.layhill.roadsim.gameengine.io.MeshLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class ResourceManager {

    private final Map<String, Mesh> meshes = new HashMap<>();
    private GLResourceLoader resourceLoader = new GLResourceLoader();

    public ResourceManager(){

    }

    public Mesh loadObjMesh(String filePath, String referenceName) {

        Optional<Mesh> loadedMesh = MeshLoader.loadObjAsMesh(filePath);
        if (loadedMesh.isEmpty()) {
            return null;
        }
        Mesh rawMesh = loadedMesh.get();
        meshes.put(referenceName, rawMesh);
        return rawMesh;
    }

    public Mesh getMesh(String referenceName){
        return meshes.get(referenceName);
    }


    public TexturedModel loadModelToGpu(String meshReferenceName){
        Mesh mesh =meshes.get(meshReferenceName);
        return resourceLoader.loadToVao(mesh, null );
    }
}
