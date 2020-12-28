package com.sk.tutorial.ui;

import com.sk.tutorial.framebuffer.FrameBuffer;
import com.sk.tutorial.model.Texture;
import com.sk.tutorial.renderer.IRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public class FrameBufferPreview extends IRenderer {

    private Texture mTexture;
    private Vector3f mTranslate;
    private float z;

    private Matrix4f mModel = new Matrix4f();

    public FrameBufferPreview(int texId, boolean hdr) {
        this(texId, -1, hdr ? "shader/2d_base/fs_image_hdr.glsl" : "shader/2d_base/fs_image.glsl");
    }

    public FrameBufferPreview(int texId, String fsPath) {
        this(texId, -1, fsPath);
    }

    public FrameBufferPreview(int texId, int texId2, String fsPath) {
        super("shader/2d_base/vs.glsl", fsPath);
        if (texId2 != -1) {
            mTexture = new Texture(texId, texId2);
        } else {
            mTexture = new Texture(texId);
        }
        initVertices();
    }

    public FrameBufferPreview(int width, int height, int channel) {
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

        if (mTexture.id2 != -1) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, mTexture.id2);
        }

        mModel = mModel.identity();
        if (mTranslate != null) {
            mModel = mModel.translate(mTranslate);
        }
        if (mScale != 0) {
            mModel = mModel.scale(mScale);
        }
        mShaderProgram.setUniformMatrix4fv("model", mModel);
        mShaderProgram.setUniform1i("image", 0);
        mShaderProgram.setUniform1i("image2", 1);

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
