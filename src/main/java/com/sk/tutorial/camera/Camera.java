package com.sk.tutorial.camera;

import org.joml.Vector3f;

public class Camera {

    private Vector3f mCameraPos;
    private Vector3f mCameraFront;
    private Vector3f mCameraUp;

    public Camera(Vector3f pos, Vector3f front, Vector3f up) {
        mCameraPos = pos;
        mCameraFront = front;
        mCameraUp = up;
    }


    public Vector3f getCameraPos() {
        return mCameraPos;
    }

    public Vector3f getCameraFront() {
        return mCameraFront;
    }

    public Vector3f getCameraUp() {
        return mCameraUp;
    }

    public void setCameraPos(Vector3f pos) {
        mCameraPos = pos;
    }

    public void setCameraFront(Vector3f front) {
        mCameraFront = front;
    }

    public void setCameraUp(Vector3f up) {
        mCameraUp = up;
    }

}
