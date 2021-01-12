package com.sk.tutorial.geometry;

import com.sk.tutorial.model.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Square {

    public List<Vertex> vertices = new ArrayList<>();
    public Map<Integer, List<Vertex>> triangleMapping = new HashMap<>();


    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addTriangleMapping(int index, Vertex a1, Vertex a2, Vertex a3) {
        List<Vertex> vertices = new ArrayList<>();
        vertices.add(a1);
        vertices.add(a2);
        vertices.add(a3);
        triangleMapping.put(index, vertices);
    }
}
