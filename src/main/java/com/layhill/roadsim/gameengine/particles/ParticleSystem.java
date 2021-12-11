package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.environments.FireParticleEmitter;
import com.layhill.roadsim.gameengine.environments.RainParticleEmitter;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ParticleSystem {

    private final List<ParticleEmitter> particleEmitters = new ArrayList<>();


    public ParticleSystem() {
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

    public void createFireParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture) {
        FireParticleEmitter emitter = new FireParticleEmitter(configuration, particleTexture);
        particleEmitters.add(emitter);
    }

    public void createRainParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture) {
        RainParticleEmitter emitter = new RainParticleEmitter(configuration, particleTexture);
        particleEmitters.add(emitter);
    }

    public void dispose() {
        particleEmitters.clear();
    }

    public List<ParticleEmitter> getEmitters() {
        return particleEmitters;
    }
}
