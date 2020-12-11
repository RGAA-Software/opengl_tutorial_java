package com.sk.tutorial;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.input.InputProcessor;
import com.sk.tutorial.layer.MultiBoxLayer;
import com.sk.tutorial.layer.SingleLightCubeLayer;
import com.sk.tutorial.model.Model;
import com.sk.tutorial.model.ModelLoader;
import com.sk.tutorial.renderer.Skybox;
import com.sk.tutorial.renderer.Sprite;
import com.sk.tutorial.shader.ShaderProgram;
import com.sk.tutorial.world.Director;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // The window handle
    private long window;

    private float width = 800;
    private float height = 600;
//    private int vao;

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {

        mCamera = new Camera(new Vector3f(0, 0, 5),
                new Vector3f(0, 0, -1),
                new Vector3f(0, 1, 0),
                0, 270, 0);

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_SAMPLES, 4);
        //glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, 1);

        // Create the window
        window = glfwCreateWindow((int)width, (int)height, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        InputProcessor.getInstance().processCursorCallback(window, width, height, mCamera);

    }

    private Camera mCamera;

    private double mLastTime = 0;
    private double mDeltaTime = 0;

    private MultiBoxLayer mBoxLayer;
    private SingleLightCubeLayer mSingleLightLayer;
    private Model mModel;
    private Model mRefractModel;

    private Sprite mFloor;
    private Sprite mGrass;
    private List<Vector3f> grassPos;
    private Skybox mSkybox;

    private void prepare() {
        Matrix4f mProjMat = new Matrix4f()
                .perspective((float) Math.toRadians(45),
                        width / height,
                        0.1f, 100.0f);

        Director.getInstance()
                .setProjection(mProjMat)
                .setCamera(mCamera);

//        vao = glGenVertexArrays();
//        glBindVertexArray(vao);

//        mBoxLayer = new MultiBoxLayer(mCamera, mProjMat, "shader/base/vs.glsl", "shader/base/fs.glsl");
        mSingleLightLayer = new SingleLightCubeLayer(mCamera, mProjMat, "shader/light_cube/vs.glsl", "shader/light_cube/fs.glsl");

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ShaderProgram modelShader = new ShaderProgram();
        modelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs.glsl");

//        mModel = ModelLoader.loadModel("resources/model/nanosuit/nanosuit.obj", modelShader);
//        mModel = ModelLoader.loadModel("resources/model/satellite/10477_Satellite_v1_L3.obj", modelShader);
        mModel = ModelLoader.loadModel("resources/model/deer/deer.obj", modelShader);
        mModel.setScale(0.001f);
        mModel.setPosition(new Vector3f(0, 0, -3));
        mModel.setCamera(mCamera);
        mModel.setProjection(mProjMat);

        ShaderProgram outlineModelShader = new ShaderProgram();
//        outlineModelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_refract.glsl");
//        outlineModelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs.glsl");
        outlineModelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_normal.glsl");

//        mRefractModel = ModelLoader.loadModel("resources/model/nanosuit/nanosuit.obj", outlineModelShader);
        mRefractModel = ModelLoader.loadModel("resources/model/statue/12328_Statue_v1_L2.obj", outlineModelShader);
        mRefractModel.setRotateDegree(-90);
        mRefractModel.setRotateAxis(new Vector3f(1, 0, 0));
//        mRefractModel = ModelLoader.loadModel("resources/model/satellite/10477_Satellite_v1_L3.obj", outlineModelShader);
//        mRefractModel = ModelLoader.loadModel("resources/model/deer/deer.obj", outlineModelShader);
        mRefractModel.setScale(0.01f);
        mRefractModel.setPosition(new Vector3f(5, 0, -3));
        mRefractModel.setCamera(mCamera);
        mRefractModel.setProjection(mProjMat);


        mFloor = new Sprite("resources/images/floor.jpg");
        mFloor.setCamera(mCamera);
        mFloor.setProjection(mProjMat);
        mFloor.setPosition(new Vector3f(0, -1, 0));
        float floorVertices[] = {
                // positions          // texture Coords
                8f, -0.5f,  8f,
                -8f, -0.5f,  8f,
                -8f, -0.5f, -8f,

                8f, -0.5f,  8f,
                -8f, -0.5f, -8f,
                8f, -0.5f, -8f,
        };
        float floorTexcoord[] = {
                8.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 8.0f,

                8.0f, 0.0f,
                0.0f, 8.0f,
                8.0f, 8.0f
        };
        mFloor.setVertices(floorVertices, null,  floorTexcoord);


        float[] grassVertices = {
                // positions         // texture Coords (swapped y coordinates because texture is flipped upside down)
                0.0f,  0.5f,  0.0f,
                0.0f, -0.5f,  0.0f,
                1.0f, -0.5f,  0.0f,

                0.0f,  0.5f,  0.0f,
                1.0f, -0.5f,  0.0f,
                1.0f,  0.5f,  0.0f,
        };

        float[] grassTexCoord = {
                0.0f,  0.0f,
                0.0f,  1.0f,
                1.0f,  1.0f,
                0.0f,  0.0f,
                1.0f,  1.0f,
                1.0f,  0.0f
        };

        mGrass = new Sprite("resources/images/window.png", false);
        mGrass.setVertices(grassVertices, null, grassTexCoord);
        //mGrass.setPosition(new Vector3f());

         Vector3f[] posArray = new Vector3f[]{
            new Vector3f(-1.5f, -1.0f, -0.48f),
            new Vector3f(1.5f, -1.0f, 0.51f),
            new Vector3f(0.0f, -1.0f, 0.7f),
            new Vector3f(-0.3f, -1.0f, -2.3f),
            new Vector3f(0.5f, -1.0f, -0.6f),
        };
        grassPos = Arrays.asList(posArray);
        grassPos.sort(new Comparator<Vector3f>() {
            @Override
            public int compare(Vector3f left, Vector3f right) {
                return Float.compare(left.z, right.z);
            }
        });
        for (Vector3f p : grassPos) {
            System.out.println("p : " + p.z);
        }

        String[] cubemapImages = new String[] {
            "resources/skybox/skybox_1/right.jpg",
            "resources/skybox/skybox_1/left.jpg",
            "resources/skybox/skybox_1/top.jpg",
            "resources/skybox/skybox_1/bottom.jpg",
            "resources/skybox/skybox_1/front.jpg",
            "resources/skybox/skybox_1/back.jpg",
        };
        mSkybox = new Skybox("shader/skybox/vs.glsl", "shader/skybox/fs.glsl", cubemapImages);
    }

    private void render(double deltaTime) {
        if (mLastTime <= 0) {
            mLastTime = glfwGetTime();
        }
        mDeltaTime = glfwGetTime() - mLastTime;

        mFloor.render(deltaTime);

        for (Vector3f pos : grassPos) {
            mGrass.setPosition(pos);
            mGrass.render(deltaTime);
        }

        mModel.render(deltaTime);
        mRefractModel.render(deltaTime);

        mSkybox.render(deltaTime);

        mLastTime = glfwGetTime();
    }

    private void loop() {
        GL.createCapabilities();
        glClearColor(.2f, 0.2f, 0.2f, 1.0f);

        prepare();

        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            render(mDeltaTime);

            InputProcessor.getInstance().processKey(window, mCamera, (float) mDeltaTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}