package com.sk.tutorial.renderer;

import com.sk.tutorial.model.Texture;
import com.sk.tutorial.model.Vertex;
import com.sk.tutorial.world.Director;

import org.joml.Matrix4f;

import java.nio.IntBuffer;
import java.util.List;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class MixColorSprite extends Sprite {

    private Texture mMixTexture;
    private Texture mSamplerTexture;

    public MixColorSprite(int textureId) {
        super(textureId);
    }

    public MixColorSprite(String imagePath, boolean flip) {
        super(imagePath, flip);
    }

    public MixColorSprite(String imagePath) {
        super(imagePath);
    }

    public MixColorSprite(String imagePath, boolean flip, String vsPath, String fsPath) {
        super(imagePath, flip, vsPath, fsPath);
    }

    public MixColorSprite(String imagePath, String mixTexPath, boolean flip, String vsPath, String fsPath, int bufferType) {
        super(imagePath, flip, vsPath, fsPath, bufferType, false);
        mMixTexture = new Texture(mixTexPath, Texture.TYPE_SINGLE_CHANNEL, flip, GL_RGB, false);
        mSamplerTexture = new Texture("resources/images/sampler.png", Texture.TYPE_DIFFUSE, flip, GL_RGB, false);
    }

    public MixColorSprite(String imagePath, String normalMapPath, boolean flip, String vsPath, String fsPath, int bufferType, boolean gammaCorrection) {
        super(imagePath, normalMapPath, flip, vsPath, fsPath, bufferType, gammaCorrection);
    }

    public void setPosColorAttribs(float[] attribs) {
        mRenderVAO = glGenVertexArrays();

        int strip = 8;

        glBindVertexArray(mRenderVAO);
        mVerticleSize =  attribs.length/strip;

        int vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, attribs, GL_STATIC_DRAW);
        int vertexLoc = mShaderProgram.getAttribLocation("aPos");
        glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, strip * 4, 0);
        glEnableVertexAttribArray(vertexLoc);

        int mixColorLoc = mShaderProgram.getAttribLocation("aMixColor");
        glVertexAttribPointer(mixColorLoc, 3, GL_FLOAT, false, strip * 4, 4 * 3);
        glEnableVertexAttribArray(mixColorLoc);

        int texLoc = mShaderProgram.getAttribLocation("aTex");
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, strip * 4, 4 * 6);
        glEnableVertexAttribArray(texLoc);

    }

    public void setVertices(float[] vertices, float[] normals, float[] texCoords, float[] colors) {
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

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        int mixColorLoc = mShaderProgram.getAttribLocation("aMixColor");
        glVertexAttribPointer(mixColorLoc, 3, GL_FLOAT, false, 0,0);
        glEnableVertexAttribArray(mixColorLoc);

        glBindVertexArray(0);
    }


    private Matrix4f model = new Matrix4f();

    @Override
    public void render(double deltaTime) {
        mShaderProgram.use();
        glBindVertexArray(mRenderVAO);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture.id);
        mShaderProgram.setUniform1i("image", 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, mMixTexture.id);
        mShaderProgram.setUniform1i("imageMix", 1);


        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, mSamplerTexture.id);
        mShaderProgram.setUniform1i("imageSampler", 2);

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


        mShaderProgram.setUniform3fv("cameraPos", Director.getInstance().getCamera().getCameraPos());

        getShaderProgram().setUniformMatrix4fv("model", model);
        Director.getInstance().updateProjectionCamera(this);

        glDrawArrays(GL_TRIANGLES, 0, mVerticleSize);
        glBindVertexArray(0);
    }
}
