package com.sk.tutorial.layer;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.renderer.IRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class SingleLightCubeLayer extends IRenderer {

    private Vector3f mColor = new Vector3f(1.0f, 1.0f, 1.0f);

    private Camera mCamera;
    private Matrix4f mProjMat;
    private Matrix4f mModel = new Matrix4f();
    private Vector3f mLightPos = new Vector3f(1.2f, 1.0f, 2.0f);

    public SingleLightCubeLayer(Camera camera, Matrix4f proj, String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
        mCamera = camera;
        mProjMat = proj;
    }

    public void setColor(Vector3f color) {
        mColor = color;
    }

    @Override
    public void init() {
        super.init();
        float[] vertices = {
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,

            -0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,

            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,

            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,
        };

        mRenderVAO = glGenVertexArrays();
        glBindVertexArray(mRenderVAO);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int posLoc = mShaderProgram.getAttribLocation("cubePos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(posLoc);

        glBindVertexArray(0);
    }

    private float mRotate = 0;
    private boolean mEnableRotate;
    public void enableRotate() {
        mEnableRotate = true;
    }

    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);

        mModel.identity();
        if (mPosition != null) {
            mModel = mModel.translate(mPosition);
        }
        if (mEnableRotate) {
            mRotate += (float)deltaTime /2;
            mModel = mModel.rotate(mRotate, 0, 1, 0);
        }
        if (mScale != 0) {
            mModel = mModel.scale(mScale);
        }

        if (mShadowMap != -1) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, mShadowMap);
            mShaderProgram.setUniform1i("shadowMap", 0);
        }

        mShaderProgram.setUniform3fv("lightColor", mColor);
        mShaderProgram.setUniformMatrix4fv("model", mModel);
        mShaderProgram.setUniformMatrix4fv("view", mCamera.lookAt());
        mShaderProgram.setUniformMatrix4fv("proj", mProjMat);
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
