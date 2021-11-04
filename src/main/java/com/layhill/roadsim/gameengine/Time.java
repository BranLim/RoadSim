package com.layhill.roadsim.gameengine;

public class Time {

    private double beginTime = 0;
    private double currentTime = 0;
    private double deltaTime;

    private static class TimeHolder {
        private static final Time time = new Time();
    }

    private Time() {
        beginTime = System.nanoTime() * 1.0E-9;
    }

    public static Time getInstance(){
        return TimeHolder.time;
    }

    public double getDeltaTime(){
        return deltaTime;
    }

    public double getCurrentTime(){
        return currentTime;
    }

    public void tick() {
        beginTime = currentTime;
        currentTime = System.nanoTime() * 1.0E-9;
        deltaTime = currentTime - beginTime;
    }


}
