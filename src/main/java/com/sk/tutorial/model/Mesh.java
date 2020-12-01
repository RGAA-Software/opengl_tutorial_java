package com.sk.tutorial.model;

import com.sk.tutorial.renderer.IRenderer;
import com.sk.tutorial.shader.ShaderProgram;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Mesh extends IRenderer {

    public List<Vertex> vertices;
    public List<Integer> indices;
    public List<Texture> textures;

    private int VAO;
    private int VBO;
    private int EBO;

    public Mesh(List<Vertex> vs, List<Integer> is, List<Texture> ts, ShaderProgram program) {
        super(program);
        vertices = vs;
        indices = is;
        textures = ts;
    }

    @Override
    public void init() {
        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        float[] verticles = new float[vertices.size() * (3 + 3 + 2)];
//        float[] normals = new float[vertices.size() * 3];
//        float[] texCoords = new float[vertices.size() * 2];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            verticles[i * 8 + 0] = vertex.position.x;
            verticles[i * 8 + 1] = vertex.position.y;
            verticles[i * 8 + 2] = vertex.position.z;

            verticles[i * 8 + 3] = vertex.normal.x;
            verticles[i * 8 + 4] = vertex.normal.y;
            verticles[i * 8 + 5] = vertex.normal.z;

            verticles[i * 8 + 6] = vertex.texCoords.x;
            verticles[i * 8 + 7] = vertex.texCoords.y;
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

        // 顶点位置
        glEnableVertexAttribArray(posLoc);
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 8 * 4, 0);
        // 顶点法线
        glEnableVertexAttribArray(normalLoc);
        glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 8*4, 3*4);
        // 顶点纹理坐标
        glEnableVertexAttribArray(texLoc);
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 8*4, 6*4);

        glBindVertexArray(0);
    }

    @Override
    public void render(double deltaTime) {
         int diffuseNr = 1;
         int specularNr = 1;
        for( int i = 0; i < textures.size(); i++)
        {
            glActiveTexture(GL_TEXTURE0 + i); // 在绑定之前激活相应的纹理单元
            // 获取纹理序号（diffuse_textureN 中的 N）
            String number = "";
            String name = textures.get(i).type;
            if(name.equals("texture_diffuse"))
                number = String.valueOf(diffuseNr++);
        else if(name.equals("texture_specular"))
            number = String.valueOf(specularNr++);
            mShaderProgram.setUniform1f(("material." + name + number), i);
            glBindTexture(GL_TEXTURE_2D, textures.get(i).id);
        }
        glActiveTexture(GL_TEXTURE0);

        // 绘制网格
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }


}
