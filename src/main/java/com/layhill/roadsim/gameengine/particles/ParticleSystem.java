package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.graphics.gl.GLResourceLoader;
import com.layhill.roadsim.gameengine.graphics.models.Camera;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleSystem {

    private final List<Particle> particles = new ArrayList<>();
    private ParticleRenderer particleRenderer;

    public ParticleSystem(GLResourceLoader resourceLoader) {
        particleRenderer = new ParticleRenderer(resourceLoader);
    }

    public void update() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            boolean isAlive = particle.update();
            if (!isAlive) {
                iterator.remove();
            }
        }
    }

    public void render(Camera camera) {
        particleRenderer.render(particles, camera);
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void dispose() {
        particleRenderer.dispose();
        particles.clear();
    }
}
