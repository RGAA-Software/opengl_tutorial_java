package com.sk.tutorial.model;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Model extends IRenderer {

    public List<Mesh> meshes;

    private Camera mCamera;
    private Matrix4f model = new Matrix4f();
    private Matrix4f mProjection;

    public Model(ShaderProgram program) {
        super(program);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void setProjection(Matrix4f proj) {
        mProjection = proj;
    }

    private Vector3f mLightAmbient = new Vector3f(0.1f, 0.1f, 0.1f);
    private Vector3f mLightDiffuse = new Vector3f(0.5f, 0.5f, 0.5f);
    private Vector3f mLightSpecular = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f mLightAmbientEM = new Vector3f(0.9f, 0.9f, 0.9f);
    private Vector3f mLightDiffuseEM = new Vector3f(0.9f, 0.9f, 0.9f);
    private Vector3f mLightSpecularEM = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f mLightDirection = new Vector3f(-0.2f, -1.0f, -0.3f);

    @Override
    public void render(double deltaTime) {
        mShaderProgram.use();
        model = model.identity();
        model = model.scale(0.2f);
        getShaderProgram().setUniformMatrix4fv("model", model);
        getShaderProgram().setUniformMatrix4fv("view", mCamera.lookAt());
        getShaderProgram().setUniformMatrix4fv("proj", mProjection);
        mShaderProgram.setUniform3fv("light.ambient",  mLightAmbientEM);
        mShaderProgram.setUniform3fv("light.diffuse",  mLightDiffuseEM);
        mShaderProgram.setUniform3fv("light.specular", mLightSpecularEM);
        mShaderProgram.setUniform3fv("light.direction", mCamera.getCameraFront());
        mShaderProgram.setUniform1f("light.cosCutoff", (float)Math.cos(Math.toRadians(12.5)));
        mShaderProgram.setUniform1f("light.outerCutoff", (float)Math.cos(Math.toRadians(17.5)));
        mShaderProgram.setUniform3fv("light.position", mCamera.getCameraPos());
        for (Mesh mesh : meshes) {
            mesh.render(deltaTime);
        }
    }
}
