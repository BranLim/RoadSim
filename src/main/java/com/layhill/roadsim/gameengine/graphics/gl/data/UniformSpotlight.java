package com.layhill.roadsim.gameengine.graphics.gl.data;

import com.layhill.roadsim.gameengine.graphics.models.Spotlight;

public class UniformSpotlight extends Uniform {


    private UniformVector3f position;
    private UniformVector3f direction;
    private UniformVector3f colour;
    private UniformFloat cutOff;
    private UniformFloat outerCutOff;

    public UniformSpotlight(String name) {
        super(name);
        position = new UniformVector3f(name + ".position");
        direction = new UniformVector3f(name + ".direction");
        colour = new UniformVector3f(name + ".colour");
        cutOff = new UniformFloat(name + ".cutOff");
        outerCutOff = new UniformFloat(name + ".outerCutOff");
    }


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

    public UniformFloat getOuterCutOff() {
        return outerCutOff;
    }

    public void loadSpotlight(Spotlight spotlight) {

        position.load(spotlight.getPosition());
        direction.load(spotlight.getDirection());
        colour.load(spotlight.getColour());
        cutOff.load(spotlight.getRadius());
        outerCutOff.load(spotlight.getOuterRadius());
    }
}
