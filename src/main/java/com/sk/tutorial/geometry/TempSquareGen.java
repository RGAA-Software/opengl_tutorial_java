package com.sk.tutorial.geometry;

import com.sk.tutorial.model.Vertex;

import java.util.List;

public class TempSquareGen {


    public static IndexTempSquare generateIndexTempSquare(int width, int height) {
        IndexTempSquare indexTempSquare = new IndexTempSquare(width, height);
        float pieceSize = 2.0f / width;
        float startX = -1.0f;
        float startY = -1.0f;
        float uvPieceSize = 1.0f / width;

        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                Vertex vertex = new Vertex();
                vertex.position.x = startX + x * pieceSize;
                vertex.position.y = startY + y * pieceSize;
                vertex.texCoords.x = x * uvPieceSize;
                vertex.texCoords.y = y * uvPieceSize;
                vertex.xMeshIndex = x;
                vertex.yMeshIndex = y;
                indexTempSquare.vertices[x][y] = vertex;

                if (x < width && y < height) {
                    Vertex center = new Vertex();
                    center.position.x = startX + (x + 0.5f) * pieceSize;
                    center.position.y = startY + (y + 0.5f) * pieceSize;
                    center.texCoords.x = (x + 0.5f) * uvPieceSize;
                    center.texCoords.y = (y + 0.5f) * uvPieceSize;
                    center.xMeshIndex = x;
                    center.yMeshIndex = y;
                    indexTempSquare.centerVertices[x][y] = center;
                }
            }
        }

        fillIndexColor(indexTempSquare, width, height);

        return indexTempSquare;
    }


    public static void fillIndexColor(IndexTempSquare indexTempSquare, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float centerColor = (float)Temperature.tempMatrix[height - 1 - y][x] / 55.0f;
                indexTempSquare.centerVertices[x][y].color.x = centerColor;
            }
        }
    }

    public static float[] transferToColorBuffer(IndexTempSquare indexTempSquare) {
        int width = indexTempSquare.width;
        int height = indexTempSquare.height;
        float[] buffer = new float[width * height * 3 * 5];
        Vertex[][] vertices = indexTempSquare.vertices;
        Vertex[][] centerVertices = indexTempSquare.centerVertices;

        int index = 0;
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                Vertex vertex = vertices[x][y];
                buffer[index++] = vertex.color.x;
                buffer[index++] = vertex.color.x;
                buffer[index++] = vertex.color.x;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vertex center = centerVertices[x][y];
                buffer[index++] = center.color.x;
                buffer[index++] = center.color.x;
                buffer[index++] = center.color.x;
            }
        }
        return buffer;
    }

    public static float[] transferToUVBuffer(IndexTempSquare indexTempSquare) {
        int width = indexTempSquare.width;
        int height = indexTempSquare.height;
        float[] buffer = new float[width * height * 3 * 5];
        Vertex[][] vertices = indexTempSquare.vertices;
        Vertex[][] centerVertices = indexTempSquare.centerVertices;

        int index = 0;
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                Vertex vertex = vertices[x][y];
                buffer[index++] = vertex.texCoords.x;
                buffer[index++] = vertex.texCoords.y;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vertex center = centerVertices[x][y];
                buffer[index++] = center.texCoords.x;
                buffer[index++] = center.texCoords.y;
            }
        }
        return buffer;
    }

    static float[] vBuffer ;

    public static float[] transferToVerticesBuffer(IndexTempSquare indexTempSquare) {
        int width = indexTempSquare.width;
        int height = indexTempSquare.height;
        float[] buffer = new float[width * height * 3 * 5];
        Vertex[][] vertices = indexTempSquare.vertices;
        Vertex[][] centerVertices = indexTempSquare.centerVertices;

        int index = 0;
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                Vertex vertex = vertices[x][y];
                buffer[index++] = vertex.position.x;
                buffer[index++] = vertex.position.y;
                buffer[index++] = vertex.position.z;
            }
        }

        System.out.println("verticle size : " + index);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vertex center = centerVertices[x][y];
                buffer[index++] = center.position.x;
                buffer[index++] = center.position.y;
                buffer[index++] = center.position.z;
            }
        }
        System.out.println("total size : " + index);

        vBuffer = buffer;
        return buffer;
    }

    public static int[] transferToIndexBuffer(IndexTempSquare indexTempSquare) {
        int triangleCount = 4;
        int[] buffer = new int[indexTempSquare.width * indexTempSquare.height * triangleCount * 3];
        int width = indexTempSquare.width;
        int height = indexTempSquare.height;
        int centerOffset = (width + 1) * (height + 1);
        System.out.println("center offset : " + centerOffset);
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // left-top         (x, y)
                // left-bottom      (x, y+1)
                // right-top        (x+1, y)
                // right-bottom     (x+1, y+1)
                // center           (x, y) + OFFSET
                int leftTopIndex = (y * (width + 1) + x);
                int leftBottomIndex = ((y+1) * (width + 1) + x);
                int rightTopIndex = (y * (width + 1) + x + 1);
                int rightBottomIndex = ((y + 1) * (width + 1) + x + 1);
                int centerIndex = (y * (width) + x) + centerOffset;

                buffer[index++] = leftTopIndex;
                buffer[index++] = rightTopIndex;
                buffer[index++] = centerIndex;

//                System.out.println("left top : " + leftTopIndex + " right top : " + rightTopIndex + " center : " + centerIndex);
//                System.out.println("(" + vBuffer[leftTopIndex] + ", " + vBuffer[leftTopIndex + 1] + "," + vBuffer[leftTopIndex+2]+ ")");
//                System.out.println("(" + vBuffer[rightTopIndex] + ", " + vBuffer[rightTopIndex + 1] + "," + vBuffer[rightTopIndex+2]+ ")");
//                System.out.println("(" + vBuffer[centerIndex] + ", " + vBuffer[centerIndex + 1] + "," + vBuffer[centerIndex+2]+ ")");


                buffer[index++] = leftTopIndex;
                buffer[index++] = centerIndex;
                buffer[index++] = leftBottomIndex;

                buffer[index++] = leftBottomIndex;
                buffer[index++] = rightBottomIndex;
                buffer[index++] = centerIndex;

                buffer[index++] = rightBottomIndex;
                buffer[index++] = rightTopIndex;
                buffer[index++] = centerIndex;
            }
        }
        return buffer;
    }

    public static void calculateColor(IndexTempSquare indexTempSquare) {
        int index = 0;
        int width = indexTempSquare.width;
        int height = indexTempSquare.height;
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {

                int left = Math.min(Math.max(x - 1, 0), width - 1);
                int top = Math.min(Math.max(y - 1, 0), height - 1);
                int right = Math.min(x, width - 1);
                int bottom = Math.min(y , height - 1);

                float leftTopColor = indexTempSquare.centerVertices[left][top].color.x;
                float leftBottomColor = indexTempSquare.centerVertices[left][bottom].color.x;
                float rightTopColor = indexTempSquare.centerVertices[right][top].color.x;
                float rightBottomColor = indexTempSquare.centerVertices[right][bottom].color.x;

                float color = (leftBottomColor + leftTopColor + rightBottomColor + rightTopColor) /4;
                indexTempSquare.vertices[x][y].color.x = color;
                indexTempSquare.vertices[x][y].color.y = color;
                indexTempSquare.vertices[x][y].color.z = color;

//                System.out.println("left : " + left + " right : " + right + " top : " + top + " bottom : " + bottom);
//                System.out.println("left top : " + leftTopColor + " left bottom : " + leftBottomColor + " right top : " + rightTopColor + " right bottom : " + rightBottomColor + " center : " + color);
            }
        }
    }


    ///////// TEMP SQUARE

    public static TempSquare generate(int width, int height) {
        TempSquare square = new TempSquare(width, height);

        float pieceSize = 2.0f / width;
        float startX = -1.0f;
        float startY = -1.0f;
        float uvPieceSize = 1.0f / width;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TempSquare.OneTempSquare ts = new TempSquare.OneTempSquare();
                ts.x = x;
                ts.y = y;

                // left
                ts.left = new TempSquare.Triangle();
                ts.left.v1 = new Vertex(startX + pieceSize * x, startY + pieceSize * y, 0);
                ts.left.v1.texCoords.x = x * uvPieceSize;
                ts.left.v1.texCoords.y = y * uvPieceSize;

                ts.left.v2 = new Vertex(startX + pieceSize * (x + 0.5f), startY + pieceSize * (y + 0.5f), 0);
                ts.left.v2.texCoords.x = (x + 0.5f) * uvPieceSize;
                ts.left.v2.texCoords.y = (y + 0.5f) * uvPieceSize;

                ts.left.v3 = new Vertex(startX + pieceSize * x, startY + pieceSize * (y + 1), 0);
                ts.left.v3.texCoords.x = x * uvPieceSize;
                ts.left.v3.texCoords.y = (y + 1) * uvPieceSize;

                // bottom
                ts.bottom = new TempSquare.Triangle();

                ts.bottom.v1 = new Vertex(startX + pieceSize * x, startY + pieceSize * y, 0);
                ts.bottom.v1.texCoords.x = x * uvPieceSize;
                ts.bottom.v1.texCoords.y = y * uvPieceSize;

                ts.bottom.v2 = new Vertex(startX + pieceSize * (x + 1), startY + pieceSize * y, 0);
                ts.bottom.v2.texCoords.x = (x + 1) * uvPieceSize;
                ts.bottom.v2.texCoords.y = y * uvPieceSize;

                ts.bottom.v3 = new Vertex(startX + pieceSize * (x + 0.5f), startY + pieceSize * (y + 0.5f), 0);
                ts.bottom.v3.texCoords.x = (x + 0.5f) * uvPieceSize;
                ts.bottom.v3.texCoords.y = (y + 0.5f) * uvPieceSize;


                // right
                ts.right = new TempSquare.Triangle();

                ts.right.v1 = new Vertex(startX + pieceSize * (x + 1), startY + pieceSize * y, 0);
                ts.right.v1.texCoords.x = (x + 1) * uvPieceSize;
                ts.right.v1.texCoords.y = y * uvPieceSize;

                ts.right.v2 = new Vertex(startX + pieceSize * (x + 1), startY + pieceSize * (y + 1), 0);
                ts.right.v2.texCoords.x = (x + 1) * uvPieceSize;
                ts.right.v2.texCoords.y = (y + 1) * uvPieceSize;

                ts.right.v3 = new Vertex(startX + pieceSize * (x + 0.5f), startY + pieceSize * (y + 0.5f), 0);
                ts.right.v3.texCoords.x = (x + 0.5f) * uvPieceSize;
                ts.right.v3.texCoords.y = (y + 0.5f) * uvPieceSize;

                // top
                ts.top = new TempSquare.Triangle();

                ts.top.v1 = new Vertex(startX + pieceSize * (x + 0.5f), startY + pieceSize * (y + 0.5f), 0);
                ts.top.v1.texCoords.x = (x + 0.5f) * uvPieceSize;
                ts.top.v1.texCoords.y = (y + 0.5f) * uvPieceSize;

                ts.top.v2 = new Vertex(startX + pieceSize * (x + 1), startY + pieceSize * (y + 1), 0);
                ts.top.v2.texCoords.x = (x + 1) * uvPieceSize;
                ts.top.v2.texCoords.y = (y + 1) * uvPieceSize;

                ts.top.v3 = new Vertex(startX + pieceSize * x, startY + pieceSize * (y + 1), 0);
                ts.top.v3.texCoords.x = x * uvPieceSize;
                ts.top.v3.texCoords.y = (y + 1) * uvPieceSize;

                square.addTempSquare(x, y, ts);
            }
        }

        fillCenterColor(square);

        return square;
    }

    public static TempSquare fillCenterColor(TempSquare tempSquare) {
        int width = tempSquare.squares[0].length;
        int height = tempSquare.squares.length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float centerColor = (float)Temperature.tempMatrix[x][height - 1 - y] / 37.0f;
                //System.out.println("center color : " + centerColor);

                TempSquare.OneTempSquare currentSquare = tempSquare.squares[x][y];

                currentSquare.centerColor = centerColor;
                currentSquare.left.v2.color.x = centerColor;
                currentSquare.bottom.v3.color.x = centerColor;
                currentSquare.right.v3.color.x = centerColor;
                currentSquare.top.v1.color.x = centerColor;

            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TempSquare.OneTempSquare currentSquare = tempSquare.squares[x][y];

                float centerColor = currentSquare.centerColor;

                TempSquare.OneTempSquare leftTopSquare = null;
                TempSquare.OneTempSquare leftBottomSquare = null;

                TempSquare.OneTempSquare rightTopSquare = null;
                TempSquare.OneTempSquare rightBottomSquare = null;

//                TempSquare.OneTempSquare leftSquare = null;
//                TempSquare.OneTempSquare rightSquare = null;
//                TempSquare.OneTempSquare topSquare = null;
//                TempSquare.OneTempSquare bottomSquare = null;

//                if (x - 1 >= 0) {
//                    leftSquare = tempSquare.squares[x-1][y];
//                }
//                if (x + 1 < width) {
//                    rightSquare = tempSquare.squares[x+1][y];
//                }
//                if (y - 1 >= 0) {
//                    topSquare = tempSquare.squares[x][y-1];
//                }
//                if (y + 1 < height) {
//                    bottomSquare = tempSquare.squares[x][y+1];
//                }
                if (x - 1 >= 0 && y - 1 >= 0) {
                    leftTopSquare = tempSquare.squares[x-1][y-1];
                }
                if (x - 1 >= 0 && y + 1 < height) {
                    leftBottomSquare = tempSquare.squares[x-1][y+1];
                }
                if (x + 1 < width && y - 1 >= 0) {
                    rightTopSquare = tempSquare.squares[x+1][y-1];
                }
                if (x + 1 < width && y + 1 < height) {
                    rightBottomSquare = tempSquare.squares[x+1][y+1];
                }

                //left   left_bottom, center, left_top
                //bottom left_bottom, right_bottom, center
                //right  right_bottom, right_top, center
                //top    center, right_top, left_top

                if (leftTopSquare != null) {
                    currentSquare.left.v3.color.x = getColor(centerColor, leftTopSquare.centerColor);
//                    System.out.println("left center color : " + centerColor);
                } else {
                    currentSquare.left.v3.color.x = getColor(centerColor, centerColor);
                }
                currentSquare.top.v3.color.x = currentSquare.left.v3.color.x;

                if (leftBottomSquare != null) {
                    currentSquare.left.v1.color.x = getColor(centerColor, leftBottomSquare.centerColor);
                } else {
                    currentSquare.left.v1.color.x = getColor(centerColor, centerColor);
                }
                currentSquare.bottom.v1.color.x = currentSquare.left.v1.color.x;

                if (rightTopSquare != null) {
                    currentSquare.right.v2.color.x = getColor(centerColor, rightTopSquare.centerColor);
                } else {
                    currentSquare.right.v2.color.x = getColor(centerColor, centerColor);
                }
                currentSquare.top.v2.color.x = currentSquare.right.v2.color.x;

                if (rightBottomSquare != null) {
                    currentSquare.bottom.v2.color.x = getColor(centerColor, rightBottomSquare.centerColor);
                } else {
                    currentSquare.bottom.v2.color.x = getColor(centerColor, centerColor);
                }
                currentSquare.right.v1.color.x = currentSquare.bottom.v2.color.x;
            }
        }
        return tempSquare;
    }

    private static float getColor(float c1, float c2) {
        return (c1 * 0.7f + c2 * 0.3f);
    }


    public static float[] transferToBuffer(TempSquare tempSquare) {
        int strip = 8 * 3 * 4;
        TempSquare.OneTempSquare[][] squares = tempSquare.squares;
        float[] triangels = new float[squares.length * squares[0].length * strip];

        for (int y = 0; y < squares.length; y++) {
            for (int x = 0; x < squares[0].length; x++) {
                TempSquare.OneTempSquare oneSquare = squares[y][x];
                int index = (y * squares.length + x) * strip;
                triangels[index++] = oneSquare.left.v1.position.x;
                triangels[index++] = oneSquare.left.v1.position.y;
                triangels[index++] = oneSquare.left.v1.position.z;
                triangels[index++] = oneSquare.left.v1.color.x;
                triangels[index++] = oneSquare.left.v1.color.y;
                triangels[index++] = oneSquare.left.v1.color.z;
                triangels[index++] = oneSquare.left.v1.texCoords.x;
                triangels[index++] = oneSquare.left.v1.texCoords.y;

                triangels[index++] = oneSquare.left.v2.position.x;
                triangels[index++] = oneSquare.left.v2.position.y;
                triangels[index++] = oneSquare.left.v2.position.z;
                triangels[index++] = oneSquare.left.v2.color.x;
                triangels[index++] = oneSquare.left.v2.color.y;
                triangels[index++] = oneSquare.left.v2.color.z;
                triangels[index++] = oneSquare.left.v2.texCoords.x;
                triangels[index++] = oneSquare.left.v2.texCoords.y;

                triangels[index++] = oneSquare.left.v3.position.x;
                triangels[index++] = oneSquare.left.v3.position.y;
                triangels[index++] = oneSquare.left.v3.position.z;
                triangels[index++] = oneSquare.left.v3.color.x;
                triangels[index++] = oneSquare.left.v3.color.y;
                triangels[index++] = oneSquare.left.v3.color.z;
                triangels[index++] = oneSquare.left.v3.texCoords.x;
                triangels[index++] = oneSquare.left.v3.texCoords.y;

                triangels[index++] = oneSquare.bottom.v1.position.x;
                triangels[index++] = oneSquare.bottom.v1.position.y;
                triangels[index++] = oneSquare.bottom.v1.position.z;
                triangels[index++] = oneSquare.bottom.v1.color.x;
                triangels[index++] = oneSquare.bottom.v1.color.y;
                triangels[index++] = oneSquare.bottom.v1.color.z;
                triangels[index++] = oneSquare.bottom.v1.texCoords.x;
                triangels[index++] = oneSquare.bottom.v1.texCoords.y;

                triangels[index++] = oneSquare.bottom.v2.position.x;
                triangels[index++] = oneSquare.bottom.v2.position.y;
                triangels[index++] = oneSquare.bottom.v2.position.z;
                triangels[index++] = oneSquare.bottom.v2.color.x;
                triangels[index++] = oneSquare.bottom.v2.color.y;
                triangels[index++] = oneSquare.bottom.v2.color.z;
                triangels[index++] = oneSquare.bottom.v2.texCoords.x;
                triangels[index++] = oneSquare.bottom.v2.texCoords.y;

                triangels[index++] = oneSquare.bottom.v3.position.x;
                triangels[index++] = oneSquare.bottom.v3.position.y;
                triangels[index++] = oneSquare.bottom.v3.position.z;
                triangels[index++] = oneSquare.bottom.v3.color.x;
                triangels[index++] = oneSquare.bottom.v3.color.y;
                triangels[index++] = oneSquare.bottom.v3.color.z;
                triangels[index++] = oneSquare.bottom.v3.texCoords.x;
                triangels[index++] = oneSquare.bottom.v3.texCoords.y;

                triangels[index++] = oneSquare.right.v1.position.x;
                triangels[index++] = oneSquare.right.v1.position.y;
                triangels[index++] = oneSquare.right.v1.position.z;
                triangels[index++] = oneSquare.right.v1.color.x;
                triangels[index++] = oneSquare.right.v1.color.y;
                triangels[index++] = oneSquare.right.v1.color.z;
                triangels[index++] = oneSquare.right.v1.texCoords.x;
                triangels[index++] = oneSquare.right.v1.texCoords.y;

                triangels[index++] = oneSquare.right.v2.position.x;
                triangels[index++] = oneSquare.right.v2.position.y;
                triangels[index++] = oneSquare.right.v2.position.z;
                triangels[index++] = oneSquare.right.v2.color.x;
                triangels[index++] = oneSquare.right.v2.color.y;
                triangels[index++] = oneSquare.right.v2.color.z;
                triangels[index++] = oneSquare.right.v2.texCoords.x;
                triangels[index++] = oneSquare.right.v2.texCoords.y;

                triangels[index++] = oneSquare.right.v3.position.x;
                triangels[index++] = oneSquare.right.v3.position.y;
                triangels[index++] = oneSquare.right.v3.position.z;
                triangels[index++] = oneSquare.right.v3.color.x;
                triangels[index++] = oneSquare.right.v3.color.y;
                triangels[index++] = oneSquare.right.v3.color.z;
                triangels[index++] = oneSquare.right.v3.texCoords.x;
                triangels[index++] = oneSquare.right.v3.texCoords.y;

                triangels[index++] = oneSquare.top.v1.position.x;
                triangels[index++] = oneSquare.top.v1.position.y;
                triangels[index++] = oneSquare.top.v1.position.z;
                triangels[index++] = oneSquare.top.v1.color.x;
                triangels[index++] = oneSquare.top.v1.color.y;
                triangels[index++] = oneSquare.top.v1.color.z;
                triangels[index++] = oneSquare.top.v1.texCoords.x;
                triangels[index++] = oneSquare.top.v1.texCoords.y;

                triangels[index++] = oneSquare.top.v2.position.x;
                triangels[index++] = oneSquare.top.v2.position.y;
                triangels[index++] = oneSquare.top.v2.position.z;
                triangels[index++] = oneSquare.top.v2.color.x;
                triangels[index++] = oneSquare.top.v2.color.y;
                triangels[index++] = oneSquare.top.v2.color.z;
                triangels[index++] = oneSquare.top.v2.texCoords.x;
                triangels[index++] = oneSquare.top.v2.texCoords.y;

                triangels[index++] = oneSquare.top.v3.position.x;
                triangels[index++] = oneSquare.top.v3.position.y;
                triangels[index++] = oneSquare.top.v3.position.z;
                triangels[index++] = oneSquare.top.v3.color.x;
                triangels[index++] = oneSquare.top.v3.color.y;
                triangels[index++] = oneSquare.top.v3.color.z;
                triangels[index++] = oneSquare.top.v3.texCoords.x;
                triangels[index++] = oneSquare.top.v3.texCoords.y;

            }
        }
        return triangels;
    }

}
