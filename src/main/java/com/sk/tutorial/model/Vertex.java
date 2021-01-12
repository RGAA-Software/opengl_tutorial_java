package com.sk.tutorial.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector2f texCoords;
    public Vector3f tangent;
    public Vector3f bitangent;
    public Vector3f color;
    public int belongToTriangleIndex;
    public boolean modify = false;

    // from left,bottom
    public int xMeshIndex;
    public int yMeshIndex;

    public Vertex() {}

    public Vertex(Vector3f pos, Vector3f c) {
        position = pos;
        color = c;
    }

    public Vertex(Vector3f pos, Vector3f c, Vector2f uv) {
        position = pos;
        color = c;
        texCoords = uv;
    }
}
