package com.layhill.roadsim.gameengine.utils;

import org.joml.*;

import java.lang.Math;

public class Maths {

    public static final float EPSILON = 0.000001f;
    /*
    See @link https://en.wikipedia.org/wiki/Barycentric_coordinate_system
    * */
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static float getPitchInRadian(Quaternionf quaternion) {
        Vector3f eulerAngles = new Vector3f();
        quaternion.getEulerAnglesXYZ(eulerAngles);
        return eulerAngles.x;

    }

    public static float getYawInRadian(Quaternionf quaternion) {
        Vector3f eulerAngles = new Vector3f();
        quaternion.getEulerAnglesXYZ(eulerAngles);
        return eulerAngles.y;
    }

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

    public static Matrix4f createViewMatrix(Vector3f position, Quaternionf rotation) {
        return new Matrix4f().
                identity()
                .rotate(rotation)
                .translate(-position.x, -position.y, -position.z);

    }

    public static Matrix4f createViewMatrix(Vector3f position, float rotateXInDeg, float rotateYInDeg, float rotateZInDeg) {
        return new Matrix4f().identity()
                .rotate((float) Math.toRadians(rotateXInDeg), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotateYInDeg), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rotateZInDeg), new Vector3f(0, 0, 1))
                .translate(-position.x, -position.y, -position.z);

    }

    public static Quaternionf createLookAt(Vector3f position, Vector3f destination, Vector3f worldForward, Vector3f worldUp) {
        Vector3f unitWorldUp = new Vector3f(worldUp).normalize();
        Vector3f unitWorldForward = new Vector3f(worldForward).normalize();

        Vector3f forwardVector = new Vector3f(destination).sub(position).normalize();

        float dotProduct = new Vector3f(unitWorldForward).dot(forwardVector);
        if (java.lang.Math.abs(dotProduct - (-1.0f)) < EPSILON) {
            return new Quaternionf(unitWorldUp.x, unitWorldUp.y, unitWorldUp.z, java.lang.Math.PI);
        }
        if (java.lang.Math.abs(dotProduct - (1.0f)) < EPSILON) {
            return new Quaternionf().identity();
        }

        float rotAngle = (float) java.lang.Math.acos(dotProduct);
        Vector3f rotAxis = new Vector3f(unitWorldForward).cross(forwardVector).normalize();

        return createFromAxisAngle(rotAxis, rotAngle);

    }

    private static Quaternionf createFromAxisAngle(Vector3f rotAxis, float rotAngle) {
        float halfAngle = rotAngle * .5f;
        float theta = (float) java.lang.Math.sin(halfAngle);
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.identity();
        quaternionf.x = rotAxis.x * theta;
        quaternionf.y = rotAxis.y * theta;
        quaternionf.z = rotAxis.z * theta;
        quaternionf.w = (float) java.lang.Math.cos(halfAngle);
        return quaternionf;

    }

    public static Matrix4f createXZReflectionViewMatrix(Vector3f position, Quaternionf orientation){
        Quaternionf reflectionOrientation = new Quaternionf(orientation);
        AxisAngle4f axis = new AxisAngle4f(reflectionOrientation);
        axis.x *= -1;
        axis.z *= -1;
        reflectionOrientation.set(axis);
        return createViewMatrix(position, reflectionOrientation);
    }
}
