package com.sk.tutorial.model;

import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;
import com.sk.tutorial.util.TextUtils;
import org.lwjgl.opengl.GL33;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh extends IRenderer {

    public List<Vertex> vertices;
    public List<Integer> indices;
    public Material material;

    private boolean instance;

    private int VBO;
    private int EBO;

    public Mesh(List<Vertex> vs, List<Integer> is, Material ts, ShaderProgram program) {
        super(program);
        vertices = vs;
        indices = is;
        material = ts;

        setupData();
    }

    private void setupData() {
        mRenderVAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(mRenderVAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        int verticleSize = 3 + 3 + 2 + 3 + 3;
        float[] verticles = new float[vertices.size() * verticleSize];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            verticles[i * verticleSize + 0] = vertex.position.x;
            verticles[i * verticleSize + 1] = vertex.position.y;
            verticles[i * verticleSize + 2] = vertex.position.z;

            verticles[i * verticleSize + 3] = vertex.normal.x;
            verticles[i * verticleSize + 4] = vertex.normal.y;
            verticles[i * verticleSize + 5] = vertex.normal.z;

            verticles[i * verticleSize + 6] = vertex.texCoords.x;
            verticles[i * verticleSize + 7] = vertex.texCoords.y;

            verticles[i * verticleSize + 8] = vertex.tangent.x;
            verticles[i * verticleSize + 9] = vertex.tangent.y;
            verticles[i * verticleSize + 10] = vertex.tangent.z;

            verticles[i * verticleSize + 11] = vertex.bitangent.x;
            verticles[i * verticleSize + 12] = vertex.bitangent.y;
            verticles[i * verticleSize + 13] = vertex.bitangent.z;
        }


        glBufferData(GL_ARRAY_BUFFER, verticles, GL_STATIC_DRAW);

        int[] idxArray = new int[indices.size()];
        int index = 0;
        for (Integer idx : indices) {
            idxArray[index++] = idx;
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxArray, GL_STATIC_DRAW);

        int posLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aPos");
        int normalLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aNormal");
        int texLoc = glGetAttribLocation(mShaderProgram.getProgram(), "aTex");
        int tangentLoc = mShaderProgram.getAttribLocation("aTangent");
        int bitangentLoc = mShaderProgram.getAttribLocation("aBiTangent");

        // 顶点位置
        glEnableVertexAttribArray(posLoc);
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, verticleSize * 4, 0);
        // 顶点法线
        glEnableVertexAttribArray(normalLoc);
        glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, verticleSize*4, 3*4);
        // 顶点纹理坐标
        glEnableVertexAttribArray(texLoc);
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, verticleSize*4, 6*4);

        glEnableVertexAttribArray(tangentLoc);
        glVertexAttribPointer(tangentLoc, 3, GL_FLOAT, false, verticleSize * 4, 8 * 4);

        glEnableVertexAttribArray(bitangentLoc);
        glVertexAttribPointer(bitangentLoc, 3, GL_FLOAT, false, verticleSize * 4, 11 * 4);

        glBindVertexArray(0);
    }

    public void enableInstanceRender() {
        instance = true;
    }

    @Override
    public void render(double deltaTime) {
        super.render(deltaTime);
        List<Texture> textures = material.textures;
        if (textures != null) {
            for (int i = 0; i < textures.size(); i++) {
                int textureId = textures.get(i).id;
                glActiveTexture(GL_TEXTURE0 + i);
                glBindTexture(GL_TEXTURE_2D, textureId);
                String type = textures.get(i).type;
                if ( TextUtils.equals(type, Texture.TYPE_DIFFUSE)) {
                    //mShaderProgram.setUniform1i("material[" + i + "].type", 1);
                    mShaderProgram.setUniform1i("diffuseImage", i);
                } else if (TextUtils.equals(type, Texture.TYPE_SPECULAR)) {
                    //mShaderProgram.setUniform1i("material[" + i + "].type", 2);
                    mShaderProgram.setUniform1i("specularImage", i);
                } else if (TextUtils.equals(type, Texture.TYPE_NORMAL)) {
                    //mShaderProgram.setUniform1i("material[" + i + "].type", 3);
                    mShaderProgram.setUniform1i("normalImage", i);
                }
                //mShaderProgram.setUniform1i(("material[" + i + "].image"), i);

            }
            mShaderProgram.setUniform1i("materialSize", textures.size());
        }

        if (mShadowMap != -1) {
            glActiveTexture(GL_TEXTURE0 + textures.size());
            glBindTexture(GL_TEXTURE_2D, mShadowMap);
            mShaderProgram.setUniform1i("shadowMap", textures.size());
        }

        if (instance) {
            glBindVertexArray(mRenderVAO);
            GL33.glDrawElementsInstanced(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0, 1000);
            glBindVertexArray(0);
        } else {
            // 绘制网格
            glBindVertexArray(mRenderVAO);
            glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        }

    }


}
