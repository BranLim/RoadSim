package com.layhill.roadsim.gameengine.particles;

import com.layhill.roadsim.gameengine.Time;
import com.layhill.roadsim.gameengine.physics.Physics;
import org.joml.Vector3f;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float timeToLive;
    private float gravityEffect;
    private float rotation;
    private float scale;

    private float elapsedTime = 0;
    private Vector3f distanceChanged = new Vector3f();

    public Particle(Vector3f position, Vector3f velocity, float timeToLive, float gravityEffect, float rotation, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.timeToLive = timeToLive;
        this.gravityEffect = gravityEffect;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public boolean update() {
        velocity.y += Physics.GRAVITY * gravityEffect * Time.getInstance().getDeltaTime();
        distanceChanged.set(velocity);
        distanceChanged.mul((float) Time.getInstance().getDeltaTime());
        position.add(distanceChanged);
        elapsedTime += Time.getInstance().getDeltaTime();
        return elapsedTime < timeToLive;
    }
}
