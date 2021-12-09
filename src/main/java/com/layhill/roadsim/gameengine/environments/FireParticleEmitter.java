package com.layhill.roadsim.gameengine.environments;

import com.layhill.roadsim.gameengine.Time;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.particles.Particle;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.particles.ParticleEmitterConfiguration;
import com.layhill.roadsim.gameengine.particles.ParticleRenderer;
import org.joml.Vector3f;

import java.util.Random;

public class FireParticleEmitter extends ParticleEmitter {

    private final Random random = new Random();

    public FireParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture, ParticleRenderer renderer) {
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
}
