package com.layhill.roadsim.gameengine.graphics.gl.shaders;

import com.layhill.roadsim.gameengine.entities.EntityShaderProgram;
import com.layhill.roadsim.gameengine.graphics.shadows.ShadowShaderProgram;
import com.layhill.roadsim.gameengine.guis.GuiShaderProgram;
import com.layhill.roadsim.gameengine.particles.ParticleShaderProgram;
import com.layhill.roadsim.gameengine.terrain.TerrainShaderProgram;
import com.layhill.roadsim.gameengine.water.WaterShaderProgram;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

@Slf4j
public class ShaderFactory {

    public static Optional<Shader> loadShaderFromFile(String filename) throws IOException {
        URL file = ShaderFactory.class.getClassLoader().getResource(filename);
        Path filePath = Paths.get(Objects.requireNonNull(file).getPath());
        String shaderFileContent = new String(Files.readAllBytes(filePath));
        log.info("Loaded shader file: {}", filename);
        String[] splitString = shaderFileContent.split("(#type)( )+([a-zA-Z]+)");
        if (splitString.length == 0) {
            log.error("Invalid shader file format");
            return Optional.empty();
        }

        int startIndexOfTypeDef = shaderFileContent.indexOf("#type") + 6;
        int endIndexOfTypeDef = shaderFileContent.indexOf("\n", startIndexOfTypeDef);
        StringBuilder shaderSource = new StringBuilder();
        String shaderType = shaderFileContent.substring(startIndexOfTypeDef, endIndexOfTypeDef).trim();
        switch (shaderType.toLowerCase(Locale.ROOT)) {
            case "vertex" -> {
                shaderSource.append(splitString[1]);
                return Optional.of(new Shader(filename, shaderSource.toString(), GL_VERTEX_SHADER));
            }
            case "fragment" -> {
                shaderSource.append(splitString[1]);
                return Optional.of(new Shader(filename, shaderSource.toString(), GL_FRAGMENT_SHADER));
            }
            default -> {
                log.error("Invalid shader type: {}", shaderType);
                return Optional.empty();
            }
        }
    }

    public static ShaderProgram createTerrainShaderProgram() {
        TerrainShaderProgram shaderProgram = new TerrainShaderProgram();
        try {
            loadAndAttachShaders(shaderProgram, "assets/shaders/terrain_vertex.glsl", "assets/shaders/terrain_fragment.glsl");
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }

    public static ShaderProgram createDefaultShaderProgram() {
        EntityShaderProgram shaderProgram = new EntityShaderProgram();
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/entity_vertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/entity_fragment.glsl").orElse(null);

            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }

    public static ParticleShaderProgram createParticleShaderProgram() {
        ParticleShaderProgram shaderProgram = new ParticleShaderProgram();
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/particle_vertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/particle_fragment.glsl").orElse(null);

            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }

    public static WaterShaderProgram createWaterShaderProgram() {

        WaterShaderProgram shaderProgram = new WaterShaderProgram();
        try {
            Shader vertexShader = ShaderFactory.loadShaderFromFile("assets/shaders/water_vertex.glsl").orElse(null);
            Shader fragmentShader = ShaderFactory.loadShaderFromFile("assets/shaders/water_fragment.glsl").orElse(null);

            shaderProgram.addShader(vertexShader);
            shaderProgram.addShader(fragmentShader);
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }

    public static ShadowShaderProgram createShadowShaderProgram() {
        ShadowShaderProgram shaderProgram = new ShadowShaderProgram();
        try {
            loadAndAttachShaders(shaderProgram, "assets/shaders/shadow_vertex.glsl", "assets/shaders/shadow_fragment.glsl");
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }

    public static GuiShaderProgram createGuiShaderProgram() {
        GuiShaderProgram shaderProgram = new GuiShaderProgram();
        try {
            loadAndAttachShaders(shaderProgram, "assets/shaders/gui_vertex.glsl", "assets/shaders/gui_fragment.glsl");
            shaderProgram.init();
        } catch (IOException e) {
            log.error("Error loading shader from file", e);
        }
        return shaderProgram;
    }

    private static void loadAndAttachShaders(ShaderProgram shaderProgram, String... shaderFiles) throws IOException {
        for (var shaderFile : shaderFiles) {
            ShaderFactory.loadShaderFromFile(shaderFile).ifPresentOrElse(shaderProgram::addShader, null);
        }
    }
}
