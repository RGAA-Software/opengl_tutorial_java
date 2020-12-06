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
    private int VAO;
    private int mVerticleSize;
    private Vector3f mPosition;

    public Sprite(String imagePath, boolean flip) {
        super("shader/sprite/vs.glsl", "shader/sprite/fs.glsl");
        mTexture = new Texture(imagePath, Texture.TYPE_DIFFUSE, flip);
    }

    public Sprite(String imagePath) {
        this(imagePath, true);
    }

    public void setVertices(float[] vertices, float[] normals, float[] texCoords) {
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

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

    @Override
    public void render(double deltaTime) {
        mShaderProgram.use();
        glBindVertexArray(VAO);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture.id);
        mShaderProgram.setUniform1i("image", 0);

        model = model.identity();
        if (mPosition != null) {
            model = model.translate(mPosition);
        }
        getShaderProgram().setUniformMatrix4fv("model", model);
        Director.getInstance().updateProjectionCamera(this);

        glDrawArrays(GL_TRIANGLES, 0, mVerticleSize);
        glBindVertexArray(0);

    }
}
