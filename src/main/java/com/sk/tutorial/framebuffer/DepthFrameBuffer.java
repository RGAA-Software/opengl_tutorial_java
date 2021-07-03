package com.sk.tutorial.framebuffer;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengles.GLES20.GL_IMPLEMENTATION_COLOR_READ_FORMAT;
import static org.lwjgl.opengles.GLES20.GL_IMPLEMENTATION_COLOR_READ_TYPE;

public class DepthFrameBuffer {

    private int mFrameBufferId;
    private int mFrameBufferTexId;

    public int getFrameBufferId() {
        return mFrameBufferId;
    }

    public int getFrameBufferTexId() {
        return mFrameBufferTexId;
    }

    public void init(int width, int height) {
        // framebuffer configuration
        // -------------------------
        mFrameBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBufferId);
        // create a color attachment texture
        mFrameBufferTexId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, mFrameBufferTexId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // TODO SHADOW MAPPINT NEED TO CLAMP TO BORDER
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
//        float[] borderColor = new float[]{ 1.0f, 1.0f, 1.0f, 1.0f };
//        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, mFrameBufferTexId, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

//        // create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
//        int rbo = glGenRenderbuffers();
//        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
//        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, (int)width, (int)height); // use a single renderbuffer object for both a depth AND stencil buffer.
//        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo); // now actually attach it
//        // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
//        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
//            System.out.println("ERROR ==== ");
//            return;
//        }
//        int[] readType = new int[1];
//        glGetIntegerv(GL_IMPLEMENTATION_COLOR_READ_TYPE, readType);
//
//        int[] readFormat = new int[1];
//        glGetIntegerv(GL_IMPLEMENTATION_COLOR_READ_FORMAT, readFormat);
//        System.out.println("type : " + readType[0] + " format : " + readFormat[0]);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void begin() {
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBufferId);
    }

    public void end() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

}
