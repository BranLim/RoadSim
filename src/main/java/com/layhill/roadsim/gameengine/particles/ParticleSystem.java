package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.graphics.RawTexture;
import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import com.layhill.roadsim.gameengine.io.TextureLoader;
import com.layhill.roadsim.gameengine.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class ParticleSystem {

    private final List<ParticleEmitter> particleEmitters = new ArrayList<>();
    private ParticleRenderer particleRenderer;

    public ParticleSystem(ParticleRenderer particleRenderer) {
        this.particleRenderer = particleRenderer;
    }

    public void update() {
        Iterator<ParticleEmitter> iterator = particleEmitters.iterator();
        while (iterator.hasNext()) {
            ParticleEmitter particleEmitter = iterator.next();
            boolean isAlive = particleEmitter.update();
            if (isAlive) {
                particleEmitter.generateNewParticles();
            } else {
                iterator.remove();
            }
        }
    }

    public void render(Camera camera) {
        for (ParticleEmitter emitter : particleEmitters) {
            emitter.render(camera);
        }
    }


    public void createParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture) {
        ParticleEmitter emitter = new ParticleEmitter(configuration, particleTexture, particleRenderer);
        particleEmitters.add(emitter);
    }

    public void dispose() {
        particleRenderer.dispose();
        particleEmitters.clear();
    }
}
