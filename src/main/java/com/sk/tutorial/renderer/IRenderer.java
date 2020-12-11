package com.sk.tutorial.renderer;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.shader.ShaderProgram;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class IRenderer {

    protected ShaderProgram mShaderProgram;

    protected Matrix4f mProjection;
    protected Camera mCamera;
    protected Vector3f mPosition;
    protected float mRotateDegree;
    protected Vector3f mRotateAxis;

    protected int mRenderVAO;

    public IRenderer(ShaderProgram program) {
        mShaderProgram = program;
        init();
    }

    public IRenderer(String vertexShaderPath, String fragmentShaderPath) {
        mShaderProgram = new ShaderProgram();
        mShaderProgram.initWithShaderPath(vertexShaderPath, fragmentShaderPath);
        init();
    }

    public void init() {

    }

    public void setShaderProgram(ShaderProgram program) {
        mShaderProgram = program;
    }

    public void setProjection(Matrix4f projection) {
        mProjection = projection;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void setPosition(Vector3f position) {
        mPosition = position;
    }

    public void setRotateDegree(float degree) {
        mRotateDegree = degree;
    }

    public void setRotateAxis(Vector3f axis) {
        mRotateAxis = axis;
    }

    public void prepare() {
        mShaderProgram.use();
    }

    public void render(double deltaTime) {
        if (mShaderProgram != null) {
            mShaderProgram.use();
        }
    }

    public ShaderProgram getShaderProgram() {
        return mShaderProgram;
    }
}
