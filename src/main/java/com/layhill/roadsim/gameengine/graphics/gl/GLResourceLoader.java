package com.layhill.roadsim.gameengine.graphics.gl;

import com.layhill.roadsim.gameengine.data.Mesh;
import com.layhill.roadsim.gameengine.graphics.Texture;
import com.layhill.roadsim.gameengine.resources.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

public final class GLResourceLoader {

    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();

    public GLResourceLoader(){

    }

    public TexturedModel loadToVao(Mesh mesh, List<Texture> textures){
        return new TexturedModel(0, null, null);
    }


}
