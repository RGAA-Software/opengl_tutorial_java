package com.sk.tutorial.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;


public class InstanceRect extends IRenderer {

    public InstanceRect(String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
    }

    @Override
    public void init() {
        super.init();
        float[] quadVertices = {
            // 位置          // 颜色
            -0.05f,  0.05f, 0,  1.0f, 0.0f, 0.0f,
            0.05f, -0.05f,  0, 0.0f, 1.0f, 0.0f,
            -0.05f, -0.05f, 0,  0.0f, 0.0f, 1.0f,

            -0.05f,  0.05f, 0,  1.0f, 0.0f, 0.0f,
            0.05f, -0.05f,  0, 0.0f, 1.0f, 0.0f,
            0.05f,  0.05f,  0, 0.0f, 1.0f, 1.0f
        };

        mRenderVAO = glGenVertexArrays();
        glBindVertexArray(mRenderVAO);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
        int posLoc = mShaderProgram.getAttribLocation("aPos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 4 * 6, 0);
        glEnableVertexAttribArray(posLoc);

        int colorLoc = mShaderProgram.getAttribLocation("aColor");
        glVertexAttribPointer(colorLoc, 3, GL_FLOAT, false, 4 * 6, 4 * 3);
        glEnableVertexAttribArray(colorLoc);

        Vector2f[] translations = new Vector2f[100];
        int index = 0;
        float offset = 0.1f;
        for(int y = -10; y < 10; y += 2)  {
            for(int x = -10; x < 10; x += 2) {
                Vector2f translation = new Vector2f();
                translation.x = (float)x / 10.0f + offset;
                translation.y = (float)y / 10.0f + offset;
                translations[index++] = translation;
            }
        }

        float[] transBuffer = new float[translations.length * 2];
        for (int i = 0; i < translations.length; i++) {
            transBuffer[i * 2] = translations[i].x;
            transBuffer[i * 2 + 1] = translations[i].y;
        }

        int instanceVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, instanceVBO);
        glBufferData(GL_ARRAY_BUFFER, transBuffer, GL_STATIC_DRAW);
        int offsetLoc = mShaderProgram.getAttribLocation("aOffset");
        glVertexAttribPointer(offsetLoc, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(offsetLoc);
        GL33.glVertexAttribDivisor(offsetLoc, 1);
    }

    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);

        GL33.glDrawArraysInstanced(GL_TRIANGLES, 0, 6, 100);
    }
}
