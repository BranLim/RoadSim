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
    private GLTexture diffuseMap;
    private GLTexture normalMap;
    private GLTexture specularMap;
    private ShaderProgram shaderProgram;

    private Material(){
        id = UUID.randomUUID().toString();
    }

    public Material(GLTexture diffuseMap) {
        this();
        this.diffuseMap = diffuseMap;
        reflectivity = 0.f;
        shineDampener = 0.f;
        emissive = false;
    }

    public Material(GLTexture diffuseMap, GLTexture normalMap, GLTexture specularMap, ShaderProgram shaderProgram, float reflectivity, float shineDampener){
        this();
        this.diffuseMap = diffuseMap;
        this.normalMap = normalMap;
        this.specularMap = specularMap;
        this.shaderProgram = shaderProgram;
        this.reflectivity = reflectivity;
        this.shineDampener = shineDampener;
    }

    public String getId() {
        return id;
    }

    public GLTexture getDiffuseMap() {
        return diffuseMap;
    }

    public GLTexture getNormalMap(){
        return normalMap;
    }
    public GLTexture getSpecularMap(){
        return specularMap;
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

    public void attachShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public float getTransparency() {
        return transparency;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }
}
