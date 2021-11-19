package com.layhill.roadsim.gameengine.utils;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Math {

    public static final float EPSILON = 0.000001f;

    public static Quaternionf lookAt(Vector3f position, Vector3f destination, Vector3f worldForward, Vector3f worldUp){
        Vector3f unitWorldUp = new Vector3f(worldUp).normalize();
        Vector3f unitWorldForward = new Vector3f(worldForward).normalize();

        Vector3f forwardVector = new Vector3f(destination).sub(position).normalize();

        float dotProduct = new Vector3f(unitWorldForward).dot(forwardVector);
        if (java.lang.Math.abs(dotProduct-(-1.0f))< EPSILON){
            return new Quaternionf(unitWorldUp.x, unitWorldUp.y, unitWorldUp.z, java.lang.Math.PI);
        }
        if (java.lang.Math.abs(dotProduct - (1.0f))< EPSILON){
            return new Quaternionf().identity();
        }

        float rotAngle = (float) java.lang.Math.acos(dotProduct);
        Vector3f rotAxis = new Vector3f(unitWorldForward).cross(forwardVector).normalize();

        return createFromAxisAngle(rotAxis, rotAngle);

    }

    private static Quaternionf createFromAxisAngle(Vector3f rotAxis, float rotAngle) {
        float halfAngle = rotAngle * .5f;
        float theta = (float)java.lang.Math.sin(halfAngle);
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.identity();
        quaternionf.x = rotAxis.x *theta;
        quaternionf.y = rotAxis.y *theta;
        quaternionf.z = rotAxis.z *theta;
        quaternionf.w = (float)java.lang.Math.cos(halfAngle);
        return quaternionf;

    }
}
