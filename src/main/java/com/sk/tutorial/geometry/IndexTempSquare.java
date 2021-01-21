package com.sk.tutorial.geometry;

import com.sk.tutorial.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class IndexTempSquare {

    public IndexTempSquare(int w, int h) {
         vertices = new Vertex[w+1][h+1];
         centerVertices = new Vertex[w][h];
         width = w;
         height = h;
    }

    public Vertex[][] vertices;
    public Vertex[][] centerVertices;
    public int width;
    public int height;


}
