package com.sk.tutorial.renderer;

import com.sk.tutorial.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class GeometryPoint extends IRenderer {

    public GeometryPoint(String vertexShaderPath, String fragmentShaderPath, String geometryShaderPath) {
        super(vertexShaderPath, fragmentShaderPath, geometryShaderPath);
    }

    @Override
    public void init() {
        super.init();
        mRenderVAO = glGenVertexArrays();
        glBindVertexArray(mRenderVAO);

        float[] points = {
                -0.5f,  0.5f,0, // 左上
                0.5f,  0.5f, 0,// 右上
                0.5f, -0.5f, 0,// 右下
                -0.5f, -0.5f, 0, // 左下
        };

        int pointBuf = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, pointBuf);
        glBufferData(GL_ARRAY_BUFFER, points, GL_STATIC_DRAW);
        int posLoc = mShaderProgram.getAttribLocation("aPos");
        System.out.println("posLoc : " + posLoc + " render vao : " + mRenderVAO);
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(posLoc);

        glBindVertexArray(0);
    }

    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);
        glDrawArrays(GL_POINTS, 0, 4);

    }
}
