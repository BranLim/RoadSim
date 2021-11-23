package com.layhill.roadsim.gameengine.resources;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.TexturedModel;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class ResourceManager {

    private final Map<String, TexturedModel> texturedModels = new HashMap<>();
    private GLResourceLoader resourceLoader = new GLResourceLoader();

    public ResourceManager(){

    }


}
