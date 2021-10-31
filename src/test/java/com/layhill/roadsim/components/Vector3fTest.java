package com.layhill.roadsim.components;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Vector3fTest {

    private static final float epsilon = 0.00001f;

    @Test
    void givenUnitVector_whenAddingAnotherUnitVector_thenNewVectorShouldBe2x2y2z() {
        Vector3f unitVector = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f newVector = new Vector3f(unitVector).add(unitVector);

        assertEquals(2.0f, newVector.getX(), epsilon);
        assertEquals(2.0f, newVector.getY(), epsilon);
        assertEquals(2.0f, newVector.getZ(), epsilon);
    }

    @Test
    void givenUnitVector_whenSubtractingAnotherUnitVector_thenNewVectorShouldBe0x0y0z() {
        Vector3f unitVector = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f newVector = new Vector3f(unitVector).subtract(unitVector);

        assertEquals(0.0f, newVector.getX(), epsilon);
        assertEquals(0.0f, newVector.getY(), epsilon);
        assertEquals(0.0f, newVector.getZ(), epsilon);
    }

    @Test
    void givenVectorOf2x2y2z_whenScalingBy2_thenNewVectorShouldBe4x4y4z() {
        Vector3f unitVector = new Vector3f(2.0f, 2.0f, 2.0f);
        Vector3f newVector = new Vector3f(unitVector).scale(2.0f);

        assertEquals(4.0f, newVector.getX(), epsilon);
        assertEquals(4.0f, newVector.getY(), epsilon);
        assertEquals(4.0f, newVector.getZ(), epsilon);
    }

    @Test
    void givenVectorOf2x2y2z_whenScalingXBy2_thenNewVectorShouldBe4x2y2z() {
        Vector3f vector = new Vector3f(2.0f, 2.0f, 2.0f);
        Vector3f newVector = new Vector3f(vector).scaleX(2.0f);

        assertEquals(4.0f, newVector.getX(), epsilon);
        assertEquals(2.0f, newVector.getY(), epsilon);
        assertEquals(2.0f, newVector.getZ(), epsilon);
    }
}
