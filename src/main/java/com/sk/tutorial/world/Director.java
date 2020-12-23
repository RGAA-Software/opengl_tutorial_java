package com.sk.tutorial.world;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;

import org.joml.Matrix4f;

public class Director {

    private static Director sInstance = new Director();


    private Matrix4f mProjection;
    private Camera mCamera;
    private Matrix4f mOrthoProjection;

    public static Director getInstance() {
        return sInstance;
    }

    public Director setProjection(Matrix4f projection) {
        mProjection = projection;
        return this;
    }

    public Director setCamera(Camera camera) {
        mCamera = camera;
        return this;
    }

    public Director setOrthoProjection(Matrix4f projection) {
        mOrthoProjection = projection;
        return this;
    }

    public Matrix4f getProjection() {
        return mProjection;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void updateProjectionCamera(IRenderer renderer) {
        updateViewMatrix(renderer, mCamera.lookAt());
        updateProjMatrix(renderer, mProjection);
    }

    public void updateViewMatrix(IRenderer renderer, Matrix4f matrix) {
        renderer.getShaderProgram().setUniformMatrix4fv("view", matrix);
    }

    public void updateProjMatrix(IRenderer renderer, Matrix4f matrix) {
        renderer.getShaderProgram().setUniformMatrix4fv("proj", matrix);
    }

    public void updateOrthoProjMatrix(IRenderer renderer) {
        if (mOrthoProjection != null) {
            renderer.getShaderProgram().setUniformMatrix4fv("orthoProj", mOrthoProjection);
        }
    }
}
