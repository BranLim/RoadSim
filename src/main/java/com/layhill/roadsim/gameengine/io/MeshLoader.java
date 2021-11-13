package com.layhill.roadsim.gameengine.io;

import com.layhill.roadsim.gameengine.graphics.gl.MeshModel;
import org.joml.Vector3f;

import java.util.List;

public class MeshLoader {

    public static List<Vector3f> getMeshData() {
        return List.of(
                new Vector3f(20.5f, -20.5f, 100.0f),
                new Vector3f(-20.5f, 20.5f, 100.0f),
                new Vector3f(20.5f, 20.5f, 100.0f),
                new Vector3f(-20.5f, -20.5f, 100.0f));

    }

    public static List<Integer> getMeshDataIndices() {
        return List.of(
                2, 1, 0, // Top-right triangle
                0, 1, 3 //Bottom left triangle
        );
    }

    //public static MeshModel load

}
