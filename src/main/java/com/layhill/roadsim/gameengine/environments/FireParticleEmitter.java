package com.layhill.roadsim.gameengine.environments;

import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.particles.Particle;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.particles.ParticleEmitterConfiguration;
import org.joml.Vector3f;

import java.util.Random;

public class FireParticleEmitter extends ParticleEmitter {

    private final Random random = new Random();

    public FireParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture) {
        super(configuration, particleTexture);
    }

    @Override
    protected void emitParticle(float particleActualTimeToLive, float actualGravityEffect) {

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
