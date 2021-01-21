package com.sk.tutorial.geometry;

import com.sk.tutorial.model.Vertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempSquare {

    public static class Triangle {
        public Vertex v1;
        public Vertex v2;
        public Vertex v3;
    }

    public static class OneTempSquare {
        public int x;
        public int y;
        public Triangle left;
        public Triangle right;
        public Triangle top;
        public Triangle bottom;
        public float centerColor;
    }

    public OneTempSquare[][] squares;

    public TempSquare(int w, int h) {
        squares = new OneTempSquare[w][h];
    }

    public void addTempSquare(int x, int y, OneTempSquare square) {
        square.x = x;
        square.y = y;
        squares[x][y] = square;
    }
}
