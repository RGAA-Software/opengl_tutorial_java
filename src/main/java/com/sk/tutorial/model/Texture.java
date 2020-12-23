package com.sk.tutorial.model;

import com.sk.tutorial.shader.ShaderProgram;

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
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    public static final String TYPE_DIFFUSE = "diffuse";
    public static final String TYPE_SPECULAR = "specular";
    public static final String TYPE_NORMAL = "normal";

    public int id;
    String type;
    String path;

    private int mWidth;
    private int mHeight;
    private int mChannel;

    public Texture(int id) {
        this.id = id;
    }

    public Texture(String path, String type) {
        this(path, type, true);
    }

    public Texture(String path, String type, boolean flip) {
        this.type = type;
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int[] x = new int[1];
        int[] y = new int[1];
        int[] c = new int[1];
        mWidth = x[0];
        mHeight = y[0];
        mChannel = c[0];
        STBImage.stbi_set_flip_vertically_on_load(flip);
        ByteBuffer imageData = STBImage.stbi_load(path, x, y, c, 4);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, x[0], y[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }
        //System.out.println("type : " + type + " x : " + x[0] + " y: " + y[0]);
    }

    public Texture(int width, int height, int channel) {
        mWidth = width;
        mHeight = height;
        mChannel = channel;
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        ByteBuffer buffer = ByteBuffer.allocateDirect(width*height*channel);
        buffer.position(0);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) (i%128));
        }
        buffer.position(0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    @Override
    public String toString() {
        return "Texture{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
