package com.sk.tutorial.world;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;

import org.joml.Matrix4f;

public class Director {

    private static Director sInstance = new Director();


    private Matrix4f mProjection;
    private Camera mCamera;

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

    public Matrix4f getProjection() {
        return mProjection;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void updateProjectionCamera(IRenderer renderer) {
        renderer.getShaderProgram().setUniformMatrix4fv("view", mCamera.lookAt());
        renderer.getShaderProgram().setUniformMatrix4fv("proj", mProjection);
    }
}
