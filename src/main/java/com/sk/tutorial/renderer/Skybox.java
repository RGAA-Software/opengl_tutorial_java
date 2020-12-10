package com.sk.tutorial.renderer;

import com.sk.tutorial.world.Director;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;

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
import static org.lwjgl.opengl.GL30.*;

public class Skybox extends IRenderer {

    private String[] mSkyboxImages;
    private int mCubeMapTexId = -1;
    private Matrix4f model = new Matrix4f();

    public Skybox(String vertexShaderPath, String fragmentShaderPath, String[] images) {
        super(vertexShaderPath, fragmentShaderPath);
        mSkyboxImages = images;
        initSkybox();
    }

    private void initSkybox() {
        float[] skyboxVertices = {
            // positions
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f
        };

        mRenderVAO = glGenVertexArrays();
        glBindVertexArray(mRenderVAO);

        int skyboxVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO);
        glBufferData(GL_ARRAY_BUFFER, skyboxVertices, GL_STATIC_DRAW);
        int posLoc = mShaderProgram.getAttribLocation("aPos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(posLoc);

        loadCubeMap();

        glBindVertexArray(0);
    }

    private void loadCubeMap() {
        mCubeMapTexId = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, mCubeMapTexId);

        int[] x = new int[1];
        int[] y = new int[1];
        int[] c = new int[1];
        for (int imgIdx = 0; imgIdx < mSkyboxImages.length; imgIdx++) {
            String path = mSkyboxImages[imgIdx];
            STBImage.stbi_set_flip_vertically_on_load(false);
            ByteBuffer imageData = STBImage.stbi_load(path, x, y, c, 3);
            if (imageData != null) {
                System.out.println("x : " + x[0] + " c : " + c[0]);
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + imgIdx, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
                STBImage.stbi_image_free(imageData);
            }
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }


    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);
        glBindVertexArray(mRenderVAO);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, mCubeMapTexId);

        model = model.identity();
        if (mPosition != null) {
            model = model.translate(mPosition);
        }
        getShaderProgram().setUniformMatrix4fv("model", model);
        Director.getInstance().updateProjectionCamera(this);
        mShaderProgram.setUniform1i("skybox", 0);
        glDrawArrays(GL_TRIANGLES, 0, 36);

        glBindVertexArray(0);
    }
}
