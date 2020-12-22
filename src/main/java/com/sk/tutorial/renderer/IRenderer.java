package com.sk.tutorial.renderer;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.light.Light;
import com.sk.tutorial.shader.ShaderProgram;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

public abstract class IRenderer {

    protected ShaderProgram mShaderProgram;

    protected Matrix4f mProjection;
    protected Camera mCamera;
    protected Vector3f mPosition;
    protected float mRotateDegree;
    protected Vector3f mRotateAxis;
    protected float mScale;
    protected Light mLight;

    protected int mRenderVAO = -1;

    public IRenderer(ShaderProgram program) {
        mShaderProgram = program;
        init();
    }

    public IRenderer(String vertexShaderPath, String fragmentShaderPath) {
        mShaderProgram = new ShaderProgram();
        mShaderProgram.initWithShaderPath(vertexShaderPath, fragmentShaderPath);
        init();
    }

    public IRenderer(String vertexShaderPath, String fragmentShaderPath, String geometryShaderPath) {
        mShaderProgram = new ShaderProgram();
        mShaderProgram.initWithShaderPath(vertexShaderPath, fragmentShaderPath, geometryShaderPath);
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

    public void setScale(float scale) {
        mScale = scale;
    }

    public void prepare() {
        mShaderProgram.use();
    }

    public void bindVAO() {
        glBindVertexArray(mRenderVAO);
    }

    public void unbindVAO() {
        glBindVertexArray(0);
    }

    public void setLight(Light light) {
        mLight = light;
    }

    public void render(double deltaTime) {
        if (mShaderProgram != null) {
            mShaderProgram.use();
        }
        if (mRenderVAO != -1) {
            bindVAO();
        }
        if (mLight != null && mShaderProgram != null) {
            mShaderProgram.setUniform3fv("light.position", mLight.position);
            mShaderProgram.setUniform3fv("light.ambient", mLight.ambient);
            mShaderProgram.setUniform3fv("light.diffuse", mLight.diffuse);
            mShaderProgram.setUniform3fv("light.specular", mLight.specular);
            mShaderProgram.setUniform3fv("light.direction", mLight.direction);
        }
    }

    public ShaderProgram getShaderProgram() {
        return mShaderProgram;
    }
}
