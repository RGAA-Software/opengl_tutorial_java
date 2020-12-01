package com.sk.tutorial.renderer;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.shader.ShaderProgram;

import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class BoxRenderer extends IRenderer {

    private int mTexture1;
    private int mTexture2;
    private int mTexture3;

    private Vector3f mAmbient;
    private Vector3f mLightPos;
    private Vector3f mLightColor;
    private Camera mCamera;

    private boolean mEmission = false;

    public BoxRenderer(Camera camera, ShaderProgram program) {
        super(program);
        mCamera = camera;
    }

    public BoxRenderer(Camera camera, String vertexShaderPath, String fragmentShaderPath, boolean emission) {
        super(vertexShaderPath, fragmentShaderPath);
        mCamera = camera;
        mEmission = emission;
    }

    @Override
    public void init() {
        mAmbient = new Vector3f(0.12f, 0.12f, 0.12f);
        mLightPos = new Vector3f(1.0f, 1.0f, 1.0f);
        mLightColor = new Vector3f(1.0f, 1.0f, 1.0f);

        float[] vertex = {

            -0.5f, -0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   1.0f, 0.0f,
            0.5f, 0.5f, -0.5f,    0.0f,  0.0f, -1.0f,   1.0f, 1.0f,
            0.5f, 0.5f, -0.5f,    0.0f,  0.0f, -1.0f,   1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f,    0.0f,  0.0f, -1.0f,   0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,   0.0f,  0.0f, -1.0f,   0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f,   0.0f,  0.0f,  1.0f,    0.0f, 0.0f,
            0.5f, -0.5f, 0.5f,   0.0f,  0.0f,  1.0f,    1.0f, 0.0f,
            0.5f, 0.5f, 0.5f,    0.0f,  0.0f,  1.0f,    1.0f, 1.0f,
            0.5f, 0.5f, 0.5f,    0.0f,  0.0f,  1.0f,    1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f,    0.0f,  0.0f,  1.0f,    0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,   0.0f,  0.0f,  1.0f,    0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f,   -1.0f,  0.0f,  0.0f,    1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f,  -1.0f,  0.0f,  0.0f,    1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,    0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,    0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,  -1.0f,  0.0f,  0.0f,    0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f,   -1.0f,  0.0f,  0.0f,    1.0f, 0.0f,

            0.5f, 0.5f, 0.5f,    1.0f,  0.0f,  0.0f,    1.0f, 0.0f,
            0.5f, 0.5f, -0.5f,   1.0f,  0.0f,  0.0f,    1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,    0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,    0.0f, 1.0f,
            0.5f, -0.5f, 0.5f,   1.0f,  0.0f,  0.0f,    0.0f, 0.0f,
            0.5f, 0.5f, 0.5f,    1.0f,  0.0f,  0.0f,    1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,   0.0f, -1.0f,  0.0f,   0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,   0.0f, -1.0f,  0.0f,   1.0f, 1.0f,
            0.5f, -0.5f, 0.5f,    0.0f, -1.0f,  0.0f,   1.0f, 0.0f,
            0.5f, -0.5f, 0.5f,    0.0f, -1.0f,  0.0f,   1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f,    0.0f, -1.0f,  0.0f,   0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,   0.0f, -1.0f,  0.0f,   0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f,   0.0f,  1.0f,  0.0f,   0.0f, 1.0f,
            0.5f, 0.5f, -0.5f,   0.0f,  1.0f,  0.0f,   1.0f, 1.0f,
            0.5f, 0.5f, 0.5f,    0.0f,  1.0f,  0.0f,   1.0f, 0.0f,
            0.5f, 0.5f, 0.5f,    0.0f,  1.0f,  0.0f,   1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f,    0.0f,  1.0f,  0.0f,   0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f,   0.0f,  1.0f,  0.0f,  0.0f, 1.0f
        };

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);

        int posLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aPos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 8*4, 0);
        glEnableVertexAttribArray(posLoc);

        int normalLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aNormal");
        glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 8 * 4, 3*4);
        glEnableVertexAttribArray(normalLoc);

        int texLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aTex");
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 8*4, 6*4);
        glEnableVertexAttribArray(texLoc);

        mTexture1 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, mTexture1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int[] x = new int[1];
        int[] y = new int[1];
        int[] c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer imageData = STBImage.stbi_load("resources/images/container2.png", x, y, c, 4);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, x[0], y[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }

        mTexture2= glGenTextures();
        glBindTexture(GL_TEXTURE_2D, mTexture2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        x = new int[1];
        y = new int[1];
        c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        imageData = STBImage.stbi_load("resources/images/container2_specular.png", x, y, c, 4);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, x[0], y[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }

        mTexture3 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, mTexture3);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        x = new int[1];
        y = new int[1];
        c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        imageData = STBImage.stbi_load("resources/images/matrix.jpg", x, y, c, 3);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }
    }

//    private Vector3f mMaterialAmbient = new Vector3f(1.0f, 0.5f, 0.31f);
//    private Vector3f mMaterialDiffuse = new Vector3f(1.0f, 0.5f, 0.31f);
//    private Vector3f mMaterialSpecular = new Vector3f(0.5f, 0.5f, 0.5f);

    private Vector3f mLightAmbient = new Vector3f(0.1f, 0.1f, 0.1f);
    private Vector3f mLightDiffuse = new Vector3f(0.5f, 0.5f, 0.5f);
    private Vector3f mLightSpecular = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f mLightAmbientEM = new Vector3f(0.9f, 0.9f, 0.9f);
    private Vector3f mLightDiffuseEM = new Vector3f(0.9f, 0.9f, 0.9f);
    private Vector3f mLightSpecularEM = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f mLightDirection = new Vector3f(-0.2f, -1.0f, -0.3f);

    @Override
    public void render(double deltaTime) {
        if (!mEmission) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, mTexture1);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, mTexture2);

            mShaderProgram.setUniform1i("emission", 0);
            mShaderProgram.setUniform1i("image1", 0);
            mShaderProgram.setUniform1i("image2", 1);
            mShaderProgram.setUniform3fv("light.ambient",  mLightAmbient);
            mShaderProgram.setUniform3fv("light.diffuse",  mLightDiffuse);
            mShaderProgram.setUniform3fv("light.specular", mLightSpecular);

            mShaderProgram.setUniform1f("light.constant",  1.0f);
            mShaderProgram.setUniform1f("light.linear",    0.09f);
            mShaderProgram.setUniform1f("light.quadratic", 0.032f);

        } else {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, mTexture3);
            mShaderProgram.setUniform1i("emission", 1);
            mShaderProgram.setUniform1i("image1", 0);
            mShaderProgram.setUniform3fv("light.ambient",  mLightAmbientEM);
            mShaderProgram.setUniform3fv("light.diffuse",  mLightDiffuseEM);
            mShaderProgram.setUniform3fv("light.specular", mLightSpecularEM);
        }

//        mShaderProgram.setUniform3fv("ambient", mAmbient);
//        mShaderProgram.setUniform3fv("lightPos", mLightPos);
//        mShaderProgram.setUniform3fv("lightColor", mLightColor);
//        mShaderProgram.setUniform3fv("cameraPos", mCamera.getCameraPos());

//        mShaderProgram.setUniform3fv("material.ambient",  mMaterialAmbient);
//        mShaderProgram.setUniform3fv("material.diffuse",  mMaterialDiffuse);
//        mShaderProgram.setUniform3fv("material.specular", mMaterialSpecular);
//        mShaderProgram.setUniform1f("material.shininess", 32.0f);


        mShaderProgram.setUniform3fv("light.direction", mCamera.getCameraFront());
        mShaderProgram.setUniform1f("light.cosCutoff", (float)Math.cos(Math.toRadians(12.5)));
        mShaderProgram.setUniform1f("light.outerCutoff", (float)Math.cos(Math.toRadians(17.5)));
        mShaderProgram.setUniform3fv("light.position", mCamera.getCameraPos());


        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
