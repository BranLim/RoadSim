package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.Time;
import com.layhill.roadsim.gameengine.graphics.gl.objects.GLTexture;
import com.layhill.roadsim.gameengine.graphics.models.Camera;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import java.util.*;

@Slf4j
public abstract class ParticleEmitter {

    private String id;
    protected List<Particle> particles = new ArrayList<>();
    protected Vector3f position;
    protected float defaultSpeed;
    protected float timeToLive;
    protected float particleTimeToLive;
    protected float initialParticleSize;
    protected float initialParticleRotation;
    protected boolean affectedByGravity;
    protected float gravityEffect;
    protected GLTexture particleTexture;
    protected int particlePerSeconds;

    protected float elapsedTime = 0;

    private ParticleRenderer renderer;


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

    public abstract void generateNewParticles() ;


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
