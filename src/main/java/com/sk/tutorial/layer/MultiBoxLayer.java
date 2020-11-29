package com.sk.tutorial.layer;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.renderer.BoxRenderer;
import com.sk.tutorial.renderer.IRenderer;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class MultiBoxLayer extends IRenderer {

    private Vector3f[] boxPositions = new Vector3f[] {
            new Vector3f( 0.0f,  0.0f,  0.0f),
            new Vector3f( 2.0f,  5.0f, -15.0f),
            new Vector3f(-1.5f, -2.2f, -2.5f),
            new Vector3f(-3.8f, -2.0f, -12.3f),
            new Vector3f( 2.4f, -0.4f, -3.5f),
            new Vector3f(-1.7f,  3.0f, -7.5f),
            new Vector3f( 1.3f, -2.0f, -2.5f),
            new Vector3f( 1.5f,  2.0f, -2.5f),
            new Vector3f( 1.5f,  0.2f, -1.5f),
            new Vector3f(-1.3f,  1.0f, -1.5f)
    };

    private int[] rotateFactor = new int[10];

    private BoxRenderer[] mBoxRenderer = new BoxRenderer[10];
    private Camera mCamera;
    private Random mRandom = new Random();
    private Matrix4f model = new Matrix4f();
    private Matrix4f mProjection;

    public MultiBoxLayer(Camera camera, Matrix4f proj, String vertexPath, String fragPath) {
        super(vertexPath, fragPath);
        mCamera = camera;
        mProjection = proj;
        for (int i = 0; i < mBoxRenderer.length; i++) {
            mBoxRenderer[i] = new BoxRenderer(mCamera, vertexPath, fragPath);
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void render(double deltaTime) {
        for (int i = 0; i < boxPositions.length; i++) {

            mBoxRenderer[i].prepare();
            Vector3f point = boxPositions[i];
            if (rotateFactor[i] == 0) {
                rotateFactor[i] = mRandom.nextInt(28) + 1;
            }

            model = model.identity();
            model = model.translate(point);
            model = model.rotate((float)Math.toRadians(glfwGetTime() * rotateFactor[i]), 0, 0, 1);
            model = model.rotate((float)Math.toRadians(glfwGetTime() * rotateFactor[i]), 0, 1, 0);

            mBoxRenderer[i].getShaderProgram().setUniformMatrix4fv("model", model);
            mBoxRenderer[i].getShaderProgram().setUniformMatrix4fv("view", mCamera.lookAt());
            mBoxRenderer[i].getShaderProgram().setUniformMatrix4fv("proj", mProjection);

            mBoxRenderer[i].render(deltaTime);
        }
    }


}
