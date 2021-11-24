package com.layhill.roadsim.gameengine.graphics.models;

import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;

import java.util.UUID;

public class Material {
    private String id;
    private MaterialType materialType;
    private float reflectivity;
    private float shineDampener;
    private boolean emissive;
    private float transparency;
    private GLTexture texture;
    private GLTexture specularMap;
    private ShaderProgram shaderProgram;

    private Material(){
        id = UUID.randomUUID().toString();
    }

    public Material(GLTexture texture) {
        this();
        this.texture = texture;
        reflectivity = 1.f;
        shineDampener = 1.f;
        emissive = false;
    }

    public Material(GLTexture texture, GLTexture specularMap){
        this();
        this.texture = texture;
        this.specularMap = specularMap;
    }

    public String getId() {
        return id;
    }

    public GLTexture getTexture() {
        return texture;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public float getShineDampener() {
        return shineDampener;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public void setShineDampener(float shineDampener) {
        this.shineDampener = shineDampener;
    }
}
