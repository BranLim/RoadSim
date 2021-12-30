package com.layhill.roadsim.gameengine.guis;

import com.layhill.roadsim.gameengine.graphics.gl.data.UniformMatrix4f;
import com.layhill.roadsim.gameengine.graphics.gl.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class GuiShaderProgram extends ShaderProgram {

    private UniformMatrix4f transformation = new UniformMatrix4f("uTransformation");

    public GuiShaderProgram(){
        super.addUniform(transformation);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
    }

    public void loadTransformation(Matrix4f transformation){
        this.transformation.load(transformation);
    }
}
