package com.sk.tutorial;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.input.InputProcessor;
import com.sk.tutorial.layer.MultiBoxLayer;
import com.sk.tutorial.layer.SingleLightCubeLayer;
import com.sk.tutorial.light.Sun;
import com.sk.tutorial.model.Model;
import com.sk.tutorial.model.ModelLoader;
import com.sk.tutorial.renderer.GeometryPoint;
import com.sk.tutorial.renderer.InstanceRect;
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
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // The window handle
    private long window;

    private float width = 1920;
    private float height = 1080;
//    private float width = 800;
//    private float height = 600;
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

        mCamera = new Camera(new Vector3f(0, 0.5f, 3.3f),
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
    private SingleLightCubeLayer mSingleCube;
    private Model mModel;
    private Model mRock;
    private Model mRefractModel;
    private Model mWolf;

    private Sprite mFloor;
    private Sprite mGrass;
    private List<Vector3f> grassPos;
    private Skybox mSkybox;

    private GeometryPoint mPoint;
    private InstanceRect mInstanceRect;

    private void prepare() {
        Matrix4f mProjMat = new Matrix4f()
                .perspective((float) Math.toRadians(45),
                        width / height,
                        0.1f, 1000.0f);

        Director.getInstance()
                .setProjection(mProjMat)
                .setCamera(mCamera);

        Sun sun = new Sun();
        sun.direction = new Vector3f(0, -1.5f, 1.5f);
        sun.position = new Vector3f(0.0f, 0.0f, 0.0f);
        sun.ambient = new Vector3f(0.1f, 0.1f, 0.1f);
        sun.diffuse = new Vector3f(0.6f, 0.6f, 0.6f);
        sun.specular = new Vector3f(0.3f, 0.3f, 0.3f);

        ShaderProgram modelShaderProgram = new ShaderProgram();
        modelShaderProgram.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_normal.glsl");
        mModel = ModelLoader.loadModel("resources/model/planet/planet.obj", modelShaderProgram);
        mModel.setCamera(mCamera);
        mModel.setProjection(mProjMat);
        mModel.setScale(0.13f);
        mModel.setPosition(new Vector3f(0, 0, 0));
        mModel.setLight(sun);

        ShaderProgram wolfShaderProgram = new ShaderProgram();
        wolfShaderProgram.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_normal.glsl");
        mWolf = ModelLoader.loadModel("resources/model/hehua/hehua.obj", wolfShaderProgram);
        mWolf.setCamera(mCamera);
        mWolf.setProjection(mProjMat);
        mWolf.setScale(0.0023f);
        mWolf.setPosition(new Vector3f(-1, -0.25f, 0));
        mWolf.setLight(sun);

//        vao = glGenVertexArrays();
//        glBindVertexArray(vao);

//        mBoxLayer = new MultiBoxLayer(mCamera, mProjMat, "shader/base/vs.glsl", "shader/base/fs.glsl");
        mSingleLightLayer = new SingleLightCubeLayer(mCamera, mProjMat, "shader/light_cube/vs.glsl", "shader/light_cube/fs.glsl");
        Vector3f lightDirection = new Vector3f(sun.direction);
        mSingleLightLayer.setPosition(lightDirection.mul(-2));
        mSingleLightLayer.setScale(0.3f);

        mSingleCube = new SingleLightCubeLayer(mCamera, mProjMat, "shader/light_cube/vs.glsl", "shader/light_cube/fs.glsl");
        mSingleCube.setScale(0.5f);
        mSingleCube.setPosition(new Vector3f(1, -0.25f, 0));
        mSingleCube.setColor(new Vector3f(0.5f, 0.5f, 0.5f));

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
//        glEnable(GL_CULL_FACE);
        //glCullFace(GL_FRONT);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE);

        mFloor = new Sprite("resources/images/wood.png", false, "shader/sprite/vs.glsl", "shader/sprite/fs_blinn.glsl");
        float[] grassVertices = {
            10.0f,  0,  10.0f,
            -10.0f, 0,  10.0f,
            -10.0f, 0, -10.0f,
            10.0f,  0,  10.0f,
            -10.0f, 0, -10.0f,
            10.0f,  0, -10.0f,
        };

        float[] grassNormals = {
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
        };

        float[] grassTexCoord = {
                10.0f,  0.0f,
                0.0f,  0.0f,
                0.0f, 10.0f,
                10.0f,  0.0f,
                0.0f, 10.0f,
                10.0f, 10.0f
        };
        mFloor.setVertices(grassVertices, grassNormals, grassTexCoord);
        mFloor.setPosition(new Vector3f(0, -0.5f, 0));
        mFloor.setLight(sun);
//        mFloor.setRotateAxis(new Vector3f(1.0f, 0, 0));
//        mFloor.setRotateDegree(-90);
    }

    private void render(double deltaTime) {
        if (mLastTime <= 0) {
            mLastTime = glfwGetTime();
        }
        mDeltaTime = glfwGetTime() - mLastTime;
        mSingleLightLayer.render(deltaTime);
        mFloor.render(deltaTime);
        mModel.render(deltaTime);
        mWolf.render(deltaTime);
        mSingleCube.render(deltaTime);
        mLastTime = glfwGetTime();
    }

    private void loop() {
        GL.createCapabilities();
        glClearColor(.2f, 0.2f, 0.2f, 1.0f);

        prepare();

        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT |GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

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