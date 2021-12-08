package com.layhill.roadsim.gameengine.particles;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

@Builder
@Getter
public class ParticleEmitterConfiguration {
    private Vector3f position;
    private float defaultSpeed;
    private float timeToLive;
    private float particleTimeToLive;
    private float initialParticleSize;
    private float initialParticleRotation;
    private boolean affectedByGravity;
    private float gravityEffect;
    private int particlePerSeconds;
}
