package com.layhill.roadsim.gameengine.environments;

import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.particles.Particle;
import com.layhill.roadsim.gameengine.particles.ParticleEmitter;
import com.layhill.roadsim.gameengine.particles.ParticleEmitterConfiguration;
import org.joml.Vector3f;

import java.util.Random;

public class RainParticleEmitter extends ParticleEmitter {

    private Random random = new Random();

    public RainParticleEmitter(ParticleEmitterConfiguration configuration, GLTexture particleTexture) {
        super(configuration, particleTexture);
    }

    @Override
    protected void emitParticle(float particleActualTimeToLive, float actualGravityEffect) {

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
