package com.sk.tutorial.geometry;

import com.sk.tutorial.model.Vertex;

import java.util.List;

public class TempSquareGen {

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
                    System.out.println("left center color : " + centerColor);
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
