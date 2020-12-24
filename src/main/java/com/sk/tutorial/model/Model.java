package com.sk.tutorial.model;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;

import com.sk.tutorial.world.Director;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Model extends IRenderer {

    public List<Mesh> meshes;

    private Matrix4f model = new Matrix4f();

    private float scale;
    private boolean mDebugRotate;

    public Model(ShaderProgram program) {
        super(program);
    }


    public void setScale(float scale) {
        this.scale = scale;
    }

    public void enableDebugRotate() {
        mDebugRotate = true;
    }

    @Override
    public void startRenderShadowMap() {
        super.startRenderShadowMap();
        for (Mesh mesh : meshes) {
            mesh.startRenderShadowMap();
        }
    }

    @Override
    public void stopRenderShadowMap() {
        super.stopRenderShadowMap();
        for (Mesh mesh : meshes) {
            mesh.stopRenderShadowMap();
        }
    }

    @Override
    public void bindShadowMap(int id) {
        super.bindShadowMap(id);
        for(Mesh mesh : meshes) {
            mesh.bindShadowMap(id);
        }
    }

    @Override
    public void setShaderProgram(ShaderProgram program) {
        super.setShaderProgram(program);
        for (Mesh mesh : meshes) {
            mesh.setShaderProgram(program);
        }
    }

    private float mRotate = 0;
    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);
        model = model.identity();

        mRotate += (float)deltaTime /2;

        if (mPosition != null) {
            model = model.translate(mPosition);
        }

        if (mRotateDegree != 0) {
            model = model.rotate(Math.toRadians(mRotateDegree), mRotateAxis);
        }

        if (mDebugRotate) {
            model = model.rotate(mRotate, 0, 1, 0);
        }

        if (scale != 0) {
            model = model.scale(scale);
        }

        getShaderProgram().setUniformMatrix4fv("model", model);
        getShaderProgram().setUniformMatrix4fv("view", Director.getInstance().getCamera().lookAt());
        getShaderProgram().setUniformMatrix4fv("proj", Director.getInstance().getProjection());


        for (Mesh mesh : meshes) {
            mesh.render(deltaTime);
        }
    }
}
