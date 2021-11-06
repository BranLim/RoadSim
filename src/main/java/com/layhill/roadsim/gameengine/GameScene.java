package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.graphics.Shader;
import com.layhill.roadsim.gameengine.graphics.ShaderFactory;
import com.layhill.roadsim.gameengine.graphics.ShaderProgram;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

@Slf4j
public class GameScene extends Scene {

    private ShaderProgram shaderProgram;
    private int vaoID;
    private int vboID;
    private int eboID;

    private float[] squareVertexAndColourArray = {
            0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //Bottom right in red
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, //Top left in blue
            0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, //Top right in green
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, //Bottom left in yellow
    };

    //In counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // Top-right triangle
            0, 1, 3 //Bottom left triangle
    };

    public GameScene() {
    }

    @Override
    public void init() {
        loadAndCompileShaders();
        FloatBuffer vertexBuffer = generateVAO();
        generateVBO(vertexBuffer);
        generateEBO();
        sendDataToGpu();
    }

    private void loadAndCompileShaders() {
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplevertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/simplefragment.glsl").orElse(null);

            shaderProgram = new ShaderProgram();
            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();

        } catch (IOException e) {
            log.error("Error loading shader from file");
        }
    }

    private void sendDataToGpu() {
        int positionSize = 3;
        int colourSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colourSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colourSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    private FloatBuffer generateVAO() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(squareVertexAndColourArray.length);
        vertexBuffer.put(squareVertexAndColourArray).flip();
        return vertexBuffer;
    }

    private void generateVBO(FloatBuffer vertexBuffer) {
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
    }

    private void generateEBO() {
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    }

    @Override
    public void update(double deltaTime) {

        shaderProgram.start();
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //Unbind everything after rendering
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        shaderProgram.stop();
    }
}
