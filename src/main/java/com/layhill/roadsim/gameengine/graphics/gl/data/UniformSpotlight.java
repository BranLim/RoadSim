package com.layhill.roadsim.gameengine.graphics.gl.data;

import com.layhill.roadsim.gameengine.graphics.models.Spotlight;

public class UniformSpotlight extends Uniform {

    public UniformVector3f getPosition() {
        return position;
    }

    public UniformVector3f getDirection() {
        return direction;
    }

    public UniformVector3f getColour() {
        return colour;
    }

    public UniformFloat getCutOff() {
        return cutOff;
    }

    private UniformVector3f position;
    private UniformVector3f direction;
    private UniformVector3f colour;
    private UniformFloat cutOff;

    public UniformSpotlight(String name) {
        super(name);
        position = new UniformVector3f(name + ".position");
        direction = new UniformVector3f(name + ".direction");
        colour = new UniformVector3f(name + ".colour");
        cutOff = new UniformFloat(name + ".cutOff");

    }

    public void loadSpotlight(Spotlight spotlight) {

        position.load(spotlight.getPosition());
        direction.load(spotlight.getDirection());
        colour.load(spotlight.getColour());
        cutOff.load(spotlight.getRadius());
    }
}
