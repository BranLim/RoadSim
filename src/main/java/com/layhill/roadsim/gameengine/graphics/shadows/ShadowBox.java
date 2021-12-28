package com.layhill.roadsim.gameengine.graphics.shadows;

import org.joml.*;
import org.lwjgl.system.CallbackI;

import java.lang.Math;

public class ShadowBox {

    private final static float MAX_DISTANCE_FOR_SHADOW_CASTING = 150;
    private final static float OFFSET = 15;
    private Vector3f UP = new Vector3f(0, 1, 0);
    private Vector3f FORWARD = new Vector3f(0, 0, -1);
    private float x1;
    private float x2;
    private float y1;
    private float y2;
    private float z1;
    private float z2;
    private final Vector3f lightDirection = new Vector3f();
    private final Vector3f cameraPosition = new Vector3f();
    private final Quaternionf cameraOrientation = new Quaternionf();
    private float fov;
    private float aspectRatio;
    private float cameraFarPlane;
    private float cameraNearPlane;
    private float nearPlaneHeight;
    private float nearPlaneWidth;
    private float farPlaneHeight;
    private float farPlaneWidth;

    private Matrix4f projection = new Matrix4f();
    private Matrix4f view = new Matrix4f();

    public ShadowBox(Vector3f lightDirection, Vector3f cameraPosition, Quaternionf cameraOrientation, float fovInDegrees, float aspectRatio, float cameraZNear, float cameraZFar) {
        this.lightDirection.set(lightDirection);
        this.cameraPosition.set(cameraPosition);
        this.cameraOrientation.set(cameraOrientation);
        this.fov = fovInDegrees;
        this.aspectRatio = aspectRatio;
        this.cameraNearPlane = cameraZNear;
        this.cameraFarPlane = cameraZFar;
        calculatePlaneWidthsAndHeights();
    }

    public void update(Vector3f cameraPosition, Quaternionf cameraOrientation) {
        this.cameraPosition.set(cameraPosition);
        this.cameraOrientation.set(cameraOrientation);
        this.cameraOrientation.invert();
        Vector4f[] points = calculateFrustumVertices();

        boolean first = true;
        for (Vector4f point : points) {
            if (first) {
                x1 = point.x;
                x2 = point.x;
                y1 = point.y;
                y2 = point.y;
                z1 = point.z;
                z2 = point.z;
                first = false;
                continue;
            }
            if (point.x > x2) {
                x2 = point.x;
            } else if (point.x < x1) {
                x1 = point.x;
            }

            if (point.y > y2) {
                y2 = point.y;
            } else if (point.y < y1) {
                y1 = point.y;
            }

            if (point.z > z2) {
                z2 = point.z;
            } else if (point.z < z1) {
                z1 = point.z;
            }
        }
        z2 += OFFSET;
    }

    private void calculatePlaneWidthsAndHeights() {
        float theta = (float) Math.tan(Math.toRadians(fov));
        farPlaneWidth = MAX_DISTANCE_FOR_SHADOW_CASTING * theta;
        nearPlaneWidth = cameraNearPlane * theta;
        farPlaneHeight = farPlaneWidth / aspectRatio;
        nearPlaneHeight = nearPlaneWidth / aspectRatio;
    }

    public float getWidth() {
        return x2 - x1;
    }

    public float getHeight() {
        return y2 - y1;
    }

    public float getLength() {
        return z2 - z1;
    }

    public Vector3f getCentre() {
        float x = (x1 + x2) / 2f;
        float y = (y1 + y2) / 2f;
        float z = (z1 + z2) / 2f;
        return new Vector3f(x, y, z);
    }

    public Matrix4f calculateViewMatrix() {
        Vector3f normalizeLightDirection = new Vector3f(lightDirection);
        normalizeLightDirection.negate().normalize();
        float pitch = (float) Math.acos(new Vector2f(normalizeLightDirection.x, normalizeLightDirection.z).length());

        float yaw = (float) Math.toDegrees(Math.atan(normalizeLightDirection.x / normalizeLightDirection.z));
        yaw = normalizeLightDirection.z > 0 ? yaw - 180 : yaw;
        yaw = (float)-Math.toRadians(yaw);
        view.identity()
                .rotate(pitch, new Vector3f(1, 0, 0))
                .rotate(yaw, new Vector3f(0, 1, 0));

        Vector3f centre = getCentre();
        Matrix4f invertedLight = new Matrix4f();
        view.invert(invertedLight);
        Vector4f transformedCentre = invertedLight.transform(new Vector4f(centre, 1));
        view.translate(new Vector3f(transformedCentre.x, transformedCentre.y, transformedCentre.z).negate());
        return view;
    }

    public Matrix4f calculateProjectionMatrix() {
        return projection.
                identity()
                .ortho(x1, x2, y1, y2, z1, z2)
                .m33(1);
    }

    private Vector4f[] calculateFrustumVertices() {

        Vector3f up = new Vector3f();
        cameraOrientation.transform(UP, up);
        Vector3f forward = new Vector3f();
        cameraOrientation.transform(FORWARD, forward);
        Vector3f right = new Vector3f();
        forward.cross(up, right);
        Vector3f left = new Vector3f(-right.x, -right.y, -right.z);
        Vector3f down = new Vector3f(-up.x, -up.y, -up.z);

        Vector3f centreFar = new Vector3f(forward)
                .normalize(MAX_DISTANCE_FOR_SHADOW_CASTING)
                .add(cameraPosition);
        Vector3f centreNear = new Vector3f(forward)
                .normalize(cameraNearPlane)
                .add(cameraPosition);

        Vector3f farTop = new Vector3f(centreFar).add(new Vector3f(up).normalize(farPlaneHeight));
        Vector3f farBottom = new Vector3f(centreFar).add(new Vector3f(down).normalize(farPlaneHeight));

        Vector3f nearTop = new Vector3f(centreNear).add(new Vector3f(up).normalize(nearPlaneHeight));
        Vector3f nearBottom = new Vector3f(centreNear).add(new Vector3f(down).normalize(nearPlaneHeight));

        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, right, farPlaneWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, left, farPlaneWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, right, farPlaneWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, left, farPlaneWidth);

        points[4] = calculateLightSpaceFrustumCorner(nearTop, right, nearPlaneWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, left, nearPlaneWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, right, nearPlaneWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, left, nearPlaneWidth);

        return points;
    }

    private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
        Vector3f point = new Vector3f(startPoint).add(new Vector3f(direction.x, direction.y, direction.z).normalize(width));
        return calculateViewMatrix().transform(new Vector4f(point, 1f));
    }


}
