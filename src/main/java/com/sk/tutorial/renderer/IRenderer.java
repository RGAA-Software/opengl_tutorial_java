package com.sk.tutorial.renderer;

import com.sk.tutorial.shader.ShaderProgram;

public abstract class IRenderer {

    protected ShaderProgram mShaderProgram;

    public IRenderer(ShaderProgram program) {
        mShaderProgram = program;
    }

    public IRenderer(String vertexShaderPath, String fragmentShaderPath) {
        mShaderProgram = new ShaderProgram();
        mShaderProgram.initWithShaderPath(vertexShaderPath, fragmentShaderPath);
        init();
    }

    public abstract void init();

    public void prepare() {
        mShaderProgram.use();
    }

    public void render() {

    }

    public ShaderProgram getShaderProgram() {
        return mShaderProgram;
    }
}
