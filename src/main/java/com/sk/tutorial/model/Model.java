package com.sk.tutorial.model;

import com.sk.tutorial.light.Light;
import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;

import com.sk.tutorial.world.Director;
import org.joml.Math;
import org.joml.Matrix4f;

import java.util.List;

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
    public void setLight(Light light) {
        super.setLight(light);
        for (Mesh mesh : meshes) {
            mesh.setLight(light);
        }
    }

    @Override
    public void startRenderDirectLightShadowMap() {
        super.startRenderDirectLightShadowMap();
        for (Mesh mesh : meshes) {
            mesh.startRenderDirectLightShadowMap();
        }
    }

    @Override
    public void stopRenderDirectLightShadowMap() {
        super.stopRenderDirectLightShadowMap();
        for (Mesh mesh : meshes) {
            mesh.stopRenderDirectLightShadowMap();
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

    @Override
    public void startRenderPointLightShadow() {
        super.startRenderPointLightShadow();
        for (Mesh mesh : meshes) {
            mesh.startRenderPointLightShadow();
        }
    }

    @Override
    public void stopRenderPointLightShadow() {
        super.stopRenderPointLightShadow();
        for (Mesh mesh : meshes) {
            mesh.stopRenderPointLightShadow();
        }
    }

    @Override
    public void setCubeViews(Matrix4f[] views) {
        super.setCubeViews(views);
        for (Mesh mesh : meshes) {
            mesh.setCubeViews(views);
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
