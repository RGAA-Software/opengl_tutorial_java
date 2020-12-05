package com.sk.tutorial.renderer;

import com.sk.tutorial.shader.ShaderProgram;

public abstract class IRenderer {

    protected ShaderProgram mShaderProgram;

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

    public void prepare() {
        mShaderProgram.use();
    }

    public abstract void render(double deltaTime);

    public ShaderProgram getShaderProgram() {
        return mShaderProgram;
    }
}
