package com.sk.tutorial.renderer;

import com.sk.tutorial.model.Texture;
import com.sk.tutorial.world.Director;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Sprite extends IRenderer {

    private Texture mTexture;
    private Texture mNormalTexture;
    private int mVerticleSize;
    private Vector3f mPosition;

    public Sprite(int textureId) {
        super("shader/sprite/vs.glsl", "shader/sprite/fs.glsl");
        mTexture = new Texture(textureId);
    }

    public Sprite(String imagePath, boolean flip) {
        this(imagePath, flip, "shader/sprite/vs.glsl", "shader/sprite/fs.glsl");
    }

    public Sprite(String imagePath) {
        this(imagePath, true);
    }

    public Sprite(String imagePath, boolean flip, String vsPath, String fsPath) {
        this(imagePath, flip, vsPath, fsPath, GL_RGB, false);
    }

    public Sprite(String imagePath, boolean flip, String vsPath, String fsPath, int bufferType, boolean gammaCorrection) {
        this(imagePath, null,  flip, vsPath, fsPath, bufferType, gammaCorrection);
    }

    public Sprite(String imagePath, String normalMapPath, boolean flip, String vsPath, String fsPath, int bufferType, boolean gammaCorrection) {
        super(vsPath, fsPath);
        mTexture = new Texture(imagePath, Texture.TYPE_DIFFUSE, flip, bufferType, gammaCorrection);
        if (normalMapPath != null) {
            mNormalTexture = new Texture(normalMapPath, Texture.TYPE_NORMAL, flip, bufferType, gammaCorrection);
        }
    }

    public void setVertices(float[] vertices, float[] normals, float[] texCoords) {
        mRenderVAO = glGenVertexArrays();
        glBindVertexArray(mRenderVAO);

        assert (vertices != null);

        mVerticleSize = vertices.length / 3;

        int vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int vertexLoc = mShaderProgram.getAttribLocation("aPos");
        glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(vertexLoc);

        if (normals != null) {
            int normalBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalBuffer);
            glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
            int normalLoc = mShaderProgram.getAttribLocation("aNormal");
            glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(normalLoc);
        }

        if (texCoords != null) {
            int texBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texBuffer);
            glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);
            int texLoc = mShaderProgram.getAttribLocation("aTex");
            glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(texLoc);
        }

        glBindVertexArray(0);
    }

    public void setPosition(Vector3f position) {
        mPosition = position;
    }

    private Matrix4f model = new Matrix4f();
    private Matrix4f rotateMatrix = new Matrix4f();

    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture.id);
        mShaderProgram.setUniform1i("image", 0);

        model = model.identity();
        if (mPosition != null) {
            model = model.translate(mPosition);
        }

        if (mRotateAxis != null) {
            model = model.rotate((float)Math.toRadians(mRotateDegree), mRotateAxis);
        }

        if (mScaleAxis != null) {
            model = model.scale(mScaleAxis);
        }

        if (mShadowMap != -1) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, mShadowMap);
            mShaderProgram.setUniform1i("shadowMap", 1);
        }

        if (mCubeShadowMap != -1) {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_CUBE_MAP, mCubeShadowMap);
            mShaderProgram.setUniform1i("shadowMap", 2);
        }

        if (mNormalTexture != null && mNormalTexture.id != -1) {
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, mNormalTexture.id);
            mShaderProgram.setUniform1i("hasNormalMap", 1);
            mShaderProgram.setUniform1i("normalMap", 3);
            if (mRotateAxis != null) {
                rotateMatrix.identity().rotate((float) Math.toRadians(mRotateDegree), mRotateAxis);
                mShaderProgram.setUniformMatrix4fv("rotateMatrix", rotateMatrix);
            } else {
                mShaderProgram.setUniformMatrix4fv("rotateMatrix", rotateMatrix.identity());
            }
        }

        mShaderProgram.setUniform3fv("cameraPos", Director.getInstance().getCamera().getCameraPos());

        getShaderProgram().setUniformMatrix4fv("model", model);
        Director.getInstance().updateProjectionCamera(this);

        glDrawArrays(GL_TRIANGLES, 0, mVerticleSize);
        glBindVertexArray(0);

    }
}
