package com.sk.tutorial.ui;

import com.sk.tutorial.model.Texture;
import com.sk.tutorial.renderer.IRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL30.*;

public class UIImage extends IRenderer {

    private Texture mTexture;
    private Vector3f mTranslate;
    private float z;

    private Matrix4f mModel = new Matrix4f();

    public UIImage(int textureId) {
        this(textureId, "shader/2d_base/fs_image.glsl");
    }

    public UIImage(int textureId, String fsPath) {
        super("shader/2d_base/vs.glsl", fsPath);
        mTexture = new Texture(textureId);
        initVertices();
    }

    public UIImage(int width, int height, int channel) {
        super("shader/2d_base/vs.glsl", "shader/2d_base/fs_image.glsl");
        mTexture = new Texture(width, height, channel);
        initVertices();
    }


    public void initVertices() {
        mRenderVAO = glGenVertexArrays();
        glBindVertexArray(mRenderVAO);

        float[] quadVertices = {
                // positions   // texCoords
                -1.0f,  1.0f, z,
                -1.0f, -1.0f, z,
                1.0f, -1.0f,  z,

                -1.0f,  1.0f, z,
                1.0f, -1.0f,  z,
                1.0f,  1.0f,  z,
        };

        float[] bufferShotTexCoords = new float[] {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
        int posLoc = mShaderProgram.getAttribLocation("aPos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(posLoc);

        int texBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texBuffer);
        glBufferData(GL_ARRAY_BUFFER, bufferShotTexCoords, GL_STATIC_DRAW);
        int texLoc = mShaderProgram.getAttribLocation("aTex");
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(texLoc);
    }

    public void setTranslate(Vector3f vector) {
        mTranslate = vector;
    }

    public void updateBuffer(ByteBuffer data) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture.id);
        ///todo
    }

    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture.id);

        mModel = mModel.identity();
        if (mTranslate != null) {
            mModel = mModel.translate(mTranslate);
        }
        if (mScale != 0) {
            mModel = mModel.scale(mScale);
        }
        mShaderProgram.setUniformMatrix4fv("model", mModel);
        mShaderProgram.setUniform1i("image", 0);

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
