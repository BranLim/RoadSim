package com.layhill.roadsim.gameengine;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class GameScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout(location=0) in vec3 aPos;\n" +
            "layout(location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID;
    private int fragmentID;
    private int shaderProgram;
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
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl \n\t Vertex shader compile failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            throw new RuntimeException("Unable to compile vertex shader");
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl \n\t Fragment shader compile failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            throw new RuntimeException("Unable to compile fragment shader");
        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl \n\t Shader link failed");
            System.out.println(glGetShaderInfoLog(shaderProgram, len));
            throw new RuntimeException("Unable to link vertex and fragment shader");
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

        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //Unbind everything after rendering
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);
    }
}
