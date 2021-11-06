package com.layhill.roadsim.gameengine;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL20.*;


public class GameScene extends Scene {

    private String vertexShaderSrc = "#version 320 core\n" +
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
    private String fragmentShaderSrc = "#version 320 core\n" +
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

    private float[] vertexArray = {
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
    };

    private int[] elementArray = {};

    public GameScene() {
    }

    @Override
    public void init() {
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl \n\t Vertex shader compile failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl \n\t Fragment shader compile failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
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
        }
    }

    @Override
    public void update(double deltaTime) {
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            Window.getInstance().changeScene(0);
        }
    }
}
