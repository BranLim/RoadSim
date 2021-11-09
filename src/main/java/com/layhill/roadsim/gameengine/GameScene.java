package com.layhill.roadsim.gameengine;

import com.layhill.roadsim.gameengine.graphics.*;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

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
            20.5f, -20.5f, 150.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.f, 0.f,//Bottom right in red
            -20.5f, 20.5f, 150.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.f, 1.f,//Top left in blue
            20.5f, 20.5f, 150.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.f, 1.f//Top right in green
            - 20.5f, -20.5f, 150.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.f, 0.f//Bottom left in yellow
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
        camera = new Camera(new Vector3f(), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 10.0f));
        camera.init();


        Texture texture = TextureFactory.loadAsTextureFromFile("assets/texture/bricks.png", GL_TEXTURE_2D).orElse(null);
        if (texture != null) {
            texture.generate();
            texture.bind();


        }


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
        int textureSize = 2;
        int vertexSizeBytes = (positionSize + colourSize + textureSize) * Float.BYTES;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colourSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, colourSize, GL_FLOAT, false, vertexSizeBytes, positionSize + colourSize * Float.BYTES);
        glEnableVertexAttribArray(2);
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
        shaderProgram.uploadMat4f("uProjection", camera.getProjection());
        shaderProgram.uploadMat4f("uView", camera.getView());

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
