package com.layhill.roadsim.gameengine.environments;

import com.layhill.roadsim.gameengine.Time;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.particles.Particle;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.particles.ParticleEmitterConfiguration;
import com.layhill.roadsim.gameengine.particles.ParticleRenderer;

import org.joml.Vector3f;

import java.util.Random;

public class RainParticleEmitter extends ParticleEmitter {

    private Random random = new Random();

    public RainParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture, ParticleRenderer renderer) {
        super(configuration, particleTexture, renderer);
    }

    @Override
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

        float xOffset = random.nextFloat(-1000f, 1000f);
        float zOffset = random.nextFloat(-1000f, 1000f);

        Vector3f particlePosition = new Vector3f(position);
        particlePosition.add(xOffset, position.y, zOffset);

        Vector3f velocity = new Vector3f(0, -1.0f, 0);
        velocity.normalize();
        velocity.mul(defaultSpeed);

        Particle particle = new Particle(particlePosition, velocity, particleActualTimeToLive, actualGravityEffect,
                initialParticleRotation, initialParticleSize);
        particles.add(particle);
    }
}
