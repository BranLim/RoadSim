package com.layhill.roadsim.gameengine.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transformation {

    public static final float EPSILON = 0.000001f;

    public static Matrix4f createTransformationMatrix(Vector3f position, float rotateX, float rotateY, float rotateZ, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity()
                .translate(position, matrix)
                .rotate((float) java.lang.Math.toRadians(rotateX), new Vector3f(1.f, 0.f, 0.f), matrix)
                .rotate((float) java.lang.Math.toRadians(rotateY), new Vector3f(0.f, 1.f, 0.f), matrix)
                .rotate((float) java.lang.Math.toRadians(rotateZ), new Vector3f(0.f, 0.f, 1.f), matrix)
                .scale(new Vector3f(scale, scale, scale), matrix);

        return matrix;
    }

    public static Matrix4f createViewMatrix(Vector3f position, Quaternionf rotation){
        Matrix4f matrix = new Matrix4f();
        matrix.identity()
                .rotate(rotation)
                .translate(new Vector3f(position).negate());
        return matrix;
    }

    public static Quaternionf createLookAt(Vector3f position, Vector3f destination, Vector3f worldForward, Vector3f worldUp){
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
