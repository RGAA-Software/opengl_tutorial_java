package com.sk.tutorial.input;

import com.sk.tutorial.camera.Camera;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class InputProcessor {

    private static InputProcessor sInstance = new InputProcessor();

    private boolean firstEnter = true;
    private double lastX, lastY;

    private double cameraSpeed = 3;

    public static InputProcessor getInstance() {
        return sInstance;
    }

    public void processCursorCallback(long window, float width, float height, Camera camera) {
        lastX = width/2;
        lastY = height/2;
        glfwSetCursorPos(window, lastX, lastY);
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallbackI() {
            @Override
            public void invoke(long window, double xpos, double ypos) {

                //System.out.println("x pos : " + xpos + " y pos : " + ypos);

                double pitch = camera.getPitch() , yaw = camera.getYaw();

                if (firstEnter) {
                    lastX =  xpos;
                    lastY =  ypos;
                    firstEnter = false;
                    return;
                }

                double deltaX = xpos - lastX;
                double deltaY = ypos - lastY;

                lastX = xpos;
                lastY = ypos;

                pitch -= deltaY * 0.05f;
                yaw += deltaX * 0.05f;

                if (pitch >= 89.8) {
                    pitch = 89.8;
                }
                if (pitch <= -89.8) {
                    pitch = -89.8;
                }

                camera.updateEulerAngel(pitch, yaw, 0);
            }
        });

    }

    public void processKey(long window, Camera camera, float deltaTime) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);

        float scaledSpeed = (float) (cameraSpeed * deltaTime);
        Vector3f tmpFront = new Vector3f(camera.getCameraFront());
        Vector3f tmpUp = new Vector3f(camera.getCameraUp());

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            //cameraPos = cameraPos.add(tmpFront.mul(scaledSpeed));
            camera.setCameraPos(camera.getCameraPos().add(tmpFront.mul(scaledSpeed)));

        } else if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            //cameraPos = cameraPos.sub(tmpFront.mul(scaledSpeed));
            camera.setCameraPos(camera.getCameraPos().sub(tmpFront.mul(scaledSpeed)));

        } else if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            //cameraPos = cameraPos.sub(tmpFront.cross(cameraUp).normalize().mul(scaledSpeed));
            camera.setCameraPos( camera.getCameraPos().sub( tmpFront.cross(camera.getCameraUp()).normalize().mul(scaledSpeed)) );

        } else if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            //cameraPos = cameraPos.add(tmpFront.cross(cameraUp).normalize().mul(scaledSpeed));
            camera.setCameraPos( camera.getCameraPos().add( tmpFront.cross(camera.getCameraUp()).normalize().mul(scaledSpeed)) );
        } else if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            //cameraPos = cameraPos.add(tmpUp.mul(scaledSpeed));
            camera.setCameraPos( camera.getCameraPos().add(tmpUp.mul(scaledSpeed)) );

        } else if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
            //cameraPos = cameraPos.sub(tmpUp.mul(scaledSpeed));
            camera.setCameraPos( camera.getCameraPos().sub(tmpUp.mul(scaledSpeed)) );
        }
    }

}
