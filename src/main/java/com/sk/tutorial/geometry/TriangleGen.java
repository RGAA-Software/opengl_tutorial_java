package com.sk.tutorial.geometry;

import com.sk.tutorial.base.Color4F;
import com.sk.tutorial.base.ColorExt;
import com.sk.tutorial.model.Vertex;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class TriangleGen {

    public static final Vector3f LIGHT_GREEN = new Vector3f(0.4f, 0.9f, 0.0f);
    public static final Vector3f RED = new Vector3f(1.0f, 0.2f, 0.2f);

    public static Square genTrianglesForImage(int sizePerSide, boolean randomColor) {
        float pieceSize = 2.0f / sizePerSide;
        float startX = -1.0f;
        float startY = -1.0f;
        float uvPiceSize = 1.0f / sizePerSide;

        Square square = new Square();
        Random random = new Random();

        int triangleIndex = 0;
        for (int x = 0; x < sizePerSide - 1; x++) {
            for (int y = 0; y < sizePerSide - 1; y++) {
                float t1X = startX + x * pieceSize;
                float t1Y = startY + y * pieceSize;
                float uv1X = x * uvPiceSize;
                float uv1Y = y * uvPiceSize;
                Vector3f color = LIGHT_GREEN;//new Vector3f(0, 0, 0);

                if (randomColor) {
                    color = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
                }
                Vertex vertex1 = new Vertex(new Vector3f(t1X, t1Y, 0), new Vector3f(color), new Vector2f(uv1X, uv1Y));
                vertex1.belongToTriangleIndex = triangleIndex;
                vertex1.xMeshIndex = x;
                vertex1.yMeshIndex = y;
                square.addVertex(vertex1);

                float t2X = startX + (x + 1) * pieceSize;
                float t2Y = t1Y;
                float uv2X = (x + 1) * uvPiceSize;
                float uv2Y = uv1Y;
                if (randomColor) {
                    color =new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
                }
                Vertex vertex2 = new Vertex(new Vector3f(t2X, t2Y, 0), new Vector3f(color), new Vector2f(uv2X, uv2Y));
                vertex2.belongToTriangleIndex = triangleIndex;
                vertex2.xMeshIndex = x+1;
                vertex2.yMeshIndex = y;
                square.addVertex(vertex2);

                float t3X = t1X;
                float t3Y = startY + (y + 1) * pieceSize;
                float uv3X = uv1X;
                float uv3Y = (y + 1) * uvPiceSize;
                if (randomColor) {
                    color =new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
                }
                Vertex vertex3 = new Vertex(new Vector3f(t3X, t3Y, 0), new Vector3f(color), new Vector2f(uv3X, uv3Y));
                vertex3.belongToTriangleIndex = triangleIndex;
                vertex3.xMeshIndex = x;
                vertex3.yMeshIndex = y+1;
                square.addVertex(vertex3);
                square.addTriangleMapping(triangleIndex, vertex1, vertex2, vertex3);

                triangleIndex++;
                float t4X = t2X;
                float t4Y = t2Y;
                float uv4X = uv2X;
                float uv4Y = uv2Y;
                if (randomColor) {
                    color =new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
                }
                Vertex vertex4 = new Vertex(new Vector3f(t4X, t4Y, 0), new Vector3f(color), new Vector2f(uv4X, uv4Y));
                vertex4.belongToTriangleIndex = triangleIndex;
                vertex4.xMeshIndex = x+1;
                vertex4.yMeshIndex = y;
                square.addVertex(vertex4);

                float t5X = startX + (x + 1) * pieceSize;
                float t5Y = startY + (y + 1) * pieceSize;
                float uv5X = (x + 1) * uvPiceSize;
                float uv5Y = (y + 1) * uvPiceSize;
                if (randomColor) {
                    color =new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
                }
                Vertex vertex5 = new Vertex(new Vector3f(t5X, t5Y, 0), new Vector3f(color), new Vector2f(uv5X, uv5Y));
                vertex5.belongToTriangleIndex = triangleIndex;
                vertex5.xMeshIndex = x+1;
                vertex5.yMeshIndex = y+1;
                square.addVertex(vertex5);

                float t6X = t3X;
                float t6Y = t3Y;
                float uv6X = uv3X;
                float uv6Y = uv3Y;
                if (randomColor) {
                    color =new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
                }
                Vertex vertex6 = new Vertex(new Vector3f(t6X, t6Y, 0), new Vector3f(color), new Vector2f(uv6X, uv6Y));
                vertex6.belongToTriangleIndex = triangleIndex;
                vertex6.xMeshIndex = x;
                vertex6.yMeshIndex = y+1;
                square.addVertex(vertex6);
                square.addTriangleMapping(triangleIndex, vertex4, vertex6, vertex6);
            }
        }
        return square;
    }

    public static Square modifyVertexColor(Square square) {
        int x = 55;
        int y = 55;
        int xRadius = 6;
        int yRadius = (int) (6 * 1920 * 1.0f/ 1080);
        Color4F from = new Color4F(1.0f, 0.0f, 0.0f, 1.0f);
        Color4F to = new Color4F(1.0f, 1.0f, 0.3f, 1.0f);

        for (int r = 0; r <= xRadius; r++) {
            for (int angel = 0; angel <= 360; angel += 2) {
                int xIndex = (int) Math.ceil(r * Math.sin(Math.toRadians(angel)));
                int yIndex = (int) Math.ceil(r * Math.cos(Math.toRadians(angel)));
                Color4F color = ColorExt.linearGradient(from, to, xRadius, r);
                for (Vertex v : square.vertices) {
                    if (v.xMeshIndex == x + xIndex && v.yMeshIndex == y + yIndex) {
                        v.color.x = color.r;
                        v.color.y = color.g;
                        v.color.z = color.b;
                    }
                }
            }
        }

        xRadius = 15;
        Color4F outerColor = new Color4F(0.4f, 0.9f, 0.0f, 1.0f);//new Color4F(0.2f, 0.8f, 0.1f, 1.0f);
        for (int r = 6; r <= xRadius; r++) {
            for (int angel = 0; angel <= 360; angel += 2) {
                int xIndex = (int) Math.floor(r * Math.sin(Math.toRadians(angel)));
                int yIndex = (int) Math.ceil(r * Math.cos(Math.toRadians(angel)));
                Color4F color = ColorExt.linearGradient(to, outerColor, xRadius - 6, r - 6);
                for (Vertex v : square.vertices) {
                    if (v.xMeshIndex == x + xIndex && v.yMeshIndex == y + yIndex) {
                        v.color.x = color.r;
                        v.color.y = color.g;
                        v.color.z = color.b;
                        for (Vertex sameTriangleVert : square.triangleMapping.get(v.belongToTriangleIndex)) {
//                            sameTriangleVert.color.x = color.r;
//                            sameTriangleVert.color.y = color.g;
//                            sameTriangleVert.color.z = color.b;
                        }
                    }
                }
            }
        }

        return square;
    }

    public static Square modifyVertexColor1(Square square) {
        int x = 62;
        int y = 80;
        int xRadius = 4;
        int yRadius = (int) (6 * 1920 * 1.0f/ 1080);
        Color4F from = new Color4F(1.0f, 0.0f, 0.0f, 1.0f);
        Color4F to = new Color4F(1.0f, 1.0f, 0.3f, 1.0f);

        for (int r = 0; r <= xRadius; r++) {
            for (int angel = 0; angel <= 360; angel += 2) {
                int xIndex = (int) Math.ceil(r * Math.sin(Math.toRadians(angel)));
                int yIndex = (int) Math.ceil(r * Math.cos(Math.toRadians(angel)));
                Color4F color = ColorExt.linearGradient(from, to, xRadius, r);
                for (Vertex v : square.vertices) {
                    if (v.xMeshIndex == x + xIndex && v.yMeshIndex == y + yIndex) {
                        v.color.x = color.r;
                        v.color.y = color.g;
                        v.color.z = color.b;
                    }
                }
            }
        }

        xRadius = 10;
        Color4F outerColor = new Color4F(0.4f, 0.9f, 0.0f, 1.0f);//new Color4F(0.2f, 0.8f, 0.1f, 1.0f);
        for (int r = 4; r <= xRadius; r++) {
            for (int angel = 0; angel <= 360; angel += 2) {
                int xIndex = (int) Math.floor(r * Math.sin(Math.toRadians(angel)));
                int yIndex = (int) Math.ceil(r * Math.cos(Math.toRadians(angel)));
                Color4F color = ColorExt.linearGradient(to, outerColor, xRadius - 4, r - 4);
                for (Vertex v : square.vertices) {
                    if (v.xMeshIndex == x + xIndex && v.yMeshIndex == y + yIndex) {
                        v.color.x = color.r;
                        v.color.y = color.g;
                        v.color.z = color.b;
                        for (Vertex sameTriangleVert : square.triangleMapping.get(v.belongToTriangleIndex)) {
//                            sameTriangleVert.color.x = color.r;
//                            sameTriangleVert.color.y = color.g;
//                            sameTriangleVert.color.z = color.b;
                        }
                    }
                }
            }
        }

        return square;
    }



    public static float[] transferToBuffer(List<Vertex> vertices) {
        int strip = 8;
        float[] triangels = new float[vertices.size() * strip];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            triangels[i * strip + 0] = vertex.position.x;
            triangels[i * strip + 1] = vertex.position.y;
            triangels[i * strip + 2] = vertex.position.z;

            triangels[i * strip + 3] = vertex.color.x;
            triangels[i * strip + 4] = vertex.color.y;
            triangels[i * strip + 5] = vertex.color.z;

            triangels[i * strip + 6] = vertex.texCoords.x;
            triangels[i * strip + 7] = vertex.texCoords.y;
        }
        return triangels;
    }

}
