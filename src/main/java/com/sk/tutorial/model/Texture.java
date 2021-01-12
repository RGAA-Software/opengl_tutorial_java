package com.sk.tutorial.model;

import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
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
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengles.GLES20.GL_LUMINANCE;
import static org.lwjgl.opengles.GLES30.GL_RED;
import static org.lwjgl.opengles.GLES30.GL_SRGB;

public class Texture {

    public static final String TYPE_DIFFUSE = "diffuse";
    public static final String TYPE_SPECULAR = "specular";
    public static final String TYPE_NORMAL = "normal";
    public static final String TYPE_SINGLE_CHANNEL = "single_channel";

    public int id = -1;
    public int id2 = -1;
    public String type;
    private String path;
    private int bufferFormat;

    private int mWidth;
    private int mHeight;
    private int mChannel;

    public Texture(int id) {
        this.id = id;
    }

    public Texture(int id, int id2) {
        this.id = id;
        this.id2 = id2;
    }

    public Texture(String path, String type) {
        this(path, type, true, GL_RGBA, false);
        //System.out.println("Texture path : " + path);
    }

    public Texture(String path, String type, boolean flip, int bufferType, boolean gammaCorrection) {
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
        STBImage.stbi_set_flip_vertically_on_load(flip);
        int channel = 4;
        int bufferFormat = GL_RGB;
        if (bufferType == GL_RGB) {
            bufferFormat = gammaCorrection ? GL_SRGB : GL_RGB;
            channel = 3;
        } else if (bufferType == GL_RGBA) {
            bufferFormat = gammaCorrection ? GL_SRGB_ALPHA : GL_RGBA;
        } else if (bufferType == GL_LUMINANCE) {
            bufferFormat = GL_LUMINANCE;
            channel = 1;
        }

        ByteBuffer imageData = STBImage.stbi_load(path, x, y, c, channel);
        mWidth = x[0];
        mHeight = y[0];
        mChannel = c[0];
        System.out.println("path : " + path + "c : " + c[0]);
        if (imageData != null) {
//            glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB_ALPHA/*GL_RGBA*/, x[0], y[0], 0, GL_RGBA/*GL_RGBA*/, GL_UNSIGNED_BYTE, imageData);
            glTexImage2D(GL_TEXTURE_2D, 0, bufferFormat, x[0], y[0], 0, bufferType, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
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
        glBindTexture(GL_TEXTURE_2D, 0);
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
