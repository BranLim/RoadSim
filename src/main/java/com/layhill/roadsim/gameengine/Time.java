package com.layhill.roadsim.gameengine;

public class Time {

    public static final double NANO_TO_SECOND_MULTIPLIER = 1.0E-9;
    private double beginTime = 0;
    private double currentTime = 0;
    private double deltaTime;

    private static class TimeHolder {
        private static final Time time = new Time();
    }

    private Time() {
        beginTime = System.nanoTime() * NANO_TO_SECOND_MULTIPLIER;
    }

    public static Time getInstance() {
        return TimeHolder.time;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void tick() {
        beginTime = currentTime;
        currentTime = System.nanoTime() * NANO_TO_SECOND_MULTIPLIER;
        if (beginTime > 0.0) {
            deltaTime = currentTime - beginTime;
        }

    }


}
