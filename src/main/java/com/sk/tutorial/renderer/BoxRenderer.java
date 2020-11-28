package com.sk.tutorial.renderer;

import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class BoxRenderer extends IRenderer {

    private int mTexture1;
    private int mTexture2;

    public static BoxRenderer createBoxRenderer() {
        return new BoxRenderer("shader/base/vs.glsl", "shader/base/fs.glsl");
    }

    public BoxRenderer(String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
    }

    @Override
    public void init() {
        float[] vertex = {
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
        };

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);

        int posLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aPos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 5*4, 0);
        glEnableVertexAttribArray(posLoc);

        int texLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aTex");
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 5*4, 3*4);
        glEnableVertexAttribArray(texLoc);

        mTexture1 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, mTexture1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int[] x = new int[1];
        int[] y = new int[1];
        int[] c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer imageData = STBImage.stbi_load("resources/images/image1.jpg", x, y, c, 3);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }

        mTexture2= glGenTextures();
        glBindTexture(GL_TEXTURE_2D, mTexture2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        x = new int[1];
        y = new int[1];
        c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        imageData = STBImage.stbi_load("resources/images/image2.jpg", x, y, c, 3);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }
    }

    @Override
    public void render() {
        super.render();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, mTexture2);

        mShaderProgram.setUniform1i("image1", 0);
        mShaderProgram.setUniform1i("image2", 1);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
