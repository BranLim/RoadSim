package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.Time;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.*;

@Slf4j
public class ParticleEmitter {

    private String id;
    private List<Particle> particles = new ArrayList<>();
    private Vector3f position;
    private float defaultSpeed;
    private float timeToLive;
    private float particleTimeToLive;
    private float initialParticleSize;
    private float initialParticleRotation;
    private boolean affectedByGravity;
    private float gravityEffect;
    private GLTexture particleTexture;
    private int particlePerSeconds;

    private float elapsedTime = 0;

    private ParticleRenderer renderer;
    private Random random = new Random();

    protected ParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture, ParticleRenderer renderer) {
        id = UUID.randomUUID().toString();
        position = configuration.getPosition();
        defaultSpeed = configuration.getDefaultSpeed();
        timeToLive = configuration.getTimeToLive();
        particleTimeToLive = configuration.getParticleTimeToLive();
        initialParticleSize = configuration.getInitialParticleSize();
        initialParticleRotation = configuration.getInitialParticleRotation();
        affectedByGravity = configuration.isAffectedByGravity();
        gravityEffect = configuration.getGravityEffect();
        particlePerSeconds = configuration.getParticlePerSeconds();

        this.particleTexture = particleTexture;
        this.renderer = renderer;
    }

    public GLTexture getParticleTexture() {
        return particleTexture;
    }

    public boolean update() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            boolean isAlive = particle.update();
            if (!isAlive) {
                iterator.remove();
            }
        }
        elapsedTime += Time.getInstance().getDeltaTime();
        return elapsedTime < timeToLive;

    }

    public void generateNewParticles() {

        float delta = (float) Time.getInstance().getDeltaTime();
        float particlesToCreate = particlePerSeconds * delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;

        float particleActualTimeToLive = particleTimeToLive;
        float emitterRemainingTimeToLive = timeToLive - elapsedTime;
        if (emitterRemainingTimeToLive < particleTimeToLive) {
            particleActualTimeToLive = emitterRemainingTimeToLive;
        }
        float actualGravityEffect = affectedByGravity ? gravityEffect : 0.f;
        for (int i = 0; i < count; i++) {
            emitParticle(particleActualTimeToLive, actualGravityEffect);
        }
        if (Math.random() < partialParticle) {
            emitParticle(particleActualTimeToLive, actualGravityEffect);
        }

    }

    private void emitParticle(float particleActualTimeToLive, float actualGravityEffect) {

        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        float rotation = random.nextFloat() * 360.f;
        Vector3f velocity = new Vector3f(dirX, 1.0f, dirZ);
        velocity.normalize();
        velocity.mul(defaultSpeed);

        Particle particle = new Particle(position, velocity, particleActualTimeToLive, actualGravityEffect,
                rotation, initialParticleSize);
        particles.add(particle);
    }


    public void render(Camera camera) {
        renderer.render(particleTexture, particles, camera);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParticleEmitter that = (ParticleEmitter) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
