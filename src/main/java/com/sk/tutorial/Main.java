package com.sk.tutorial;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.input.InputProcessor;
import com.sk.tutorial.layer.MultiBoxLayer;
import com.sk.tutorial.layer.SingleLightCubeLayer;
import com.sk.tutorial.model.Mesh;
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
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // The window handle
    private long window;

//    private float width = 1920;
//    private float height = 1080;
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

        mCamera = new Camera(new Vector3f(0, 0, 85),
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

//        vao = glGenVertexArrays();
//        glBindVertexArray(vao);

//        mBoxLayer = new MultiBoxLayer(mCamera, mProjMat, "shader/base/vs.glsl", "shader/base/fs.glsl");
        mSingleLightLayer = new SingleLightCubeLayer(mCamera, mProjMat, "shader/light_cube/vs.glsl", "shader/light_cube/fs.glsl");

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE);

        ShaderProgram modelShader = new ShaderProgram();
//        modelShader.initWithShaderPath("shader/model/vs_explode.glsl",
//                "shader/model/fs_explode.glsl",
//                "shader/model/gs_explode.glsl");
        modelShader.initWithShaderPath("shader/model/vs.glsl",
                "shader/model/fs_explode.glsl");

        mModel = ModelLoader.loadModel("resources/model/planet/planet.obj", modelShader);
        mModel.setScale(2f);
        mModel.setPosition(new Vector3f(0, 0, 0));
        mModel.setCamera(mCamera);
        mModel.setProjection(mProjMat);

        ShaderProgram rockShader = new ShaderProgram();
        rockShader.initWithShaderPath("shader/instance/vs_model.glsl",
                "shader/model/fs_explode.glsl");
        mRock = ModelLoader.loadModel("resources/model/rock/rock.obj", rockShader);
        mRock.setScale(0.1f);
        mRock.setPosition(new Vector3f(0, 0, 1));
        mRock.setCamera(mCamera);
        mRock.setProjection(mProjMat);

        int amount = 1000;
        Matrix4f[] modelMatrices = new Matrix4f[amount];
        Random random = new Random();
        float radius = 50.0f;
        float offset = 2.5f;
        for(int i = 0; i < amount; i++)
        {
            Matrix4f model = new Matrix4f();
            model = model.identity();
            // 1. 位移：分布在半径为 'radius' 的圆形上，偏移的范围是 [-offset, offset]
            float angle = (float)i / (float)amount * 360.0f;
            float displacement = (random.nextInt() % (int)(2 * offset * 100)) / 100.0f - offset;
            float x = (float) (Math.sin(angle) * radius + displacement);
            displacement = (random.nextInt() % (int)(2 * offset * 100)) / 100.0f - offset;
            float y = displacement * 0.4f; // 让行星带的高度比x和z的宽度要小
            displacement = (random.nextInt() % (int)(2 * offset * 100)) / 100.0f - offset;
            float z = (float) (cos(angle) * radius + displacement);
            model = model.translate(x, y, z);

            // 2. 缩放：在 0.05 和 0.25f 之间缩放
            float scale = (float) ((random.nextInt() % 20) / 100.0f + 0.05);
            model = model.scale(scale);

            // 3. 旋转：绕着一个（半）随机选择的旋转轴向量进行随机的旋转
            float rotAngle = (random.nextInt() % 360);
            model = model.rotate(rotAngle, 0.4f, 0.6f, 0.8f);

            // 4. 添加到矩阵的数组中
            modelMatrices[i] = model;
        }

        float[] rockMatrix = new float[modelMatrices.length * 16];
        for (int i = 0; i < modelMatrices.length; i++) {
            Matrix4f matrix = modelMatrices[i];
            Vector4f dest = new Vector4f();
            matrix.getColumn(0, dest);
            rockMatrix[i * 16 + 0] = dest.x;
            rockMatrix[i * 16 + 1] = dest.y;
            rockMatrix[i * 16 + 2] = dest.z;
            rockMatrix[i * 16 + 3] = dest.w;

            matrix.getColumn(1, dest);
            rockMatrix[i * 16 + 4] = dest.x;
            rockMatrix[i * 16 + 5] = dest.y;
            rockMatrix[i * 16 + 6] = dest.z;
            rockMatrix[i * 16 + 7] = dest.w;

            matrix.getColumn(2, dest);
            rockMatrix[i * 16 + 8] = dest.x;
            rockMatrix[i * 16 + 9] = dest.y;
            rockMatrix[i * 16 + 10] = dest.z;
            rockMatrix[i * 16 + 11] = dest.w;

            matrix.getColumn(3, dest);
            rockMatrix[i * 16 + 12] = dest.x;
            rockMatrix[i * 16 + 13] = dest.y;
            rockMatrix[i * 16 + 14] = dest.z;
            rockMatrix[i * 16 + 15] = dest.w;

        }

        int rockVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, rockVBO);
        glBufferData(GL_ARRAY_BUFFER, rockMatrix, GL_STATIC_DRAW);

        for (int i = 0; i < mRock.meshes.size(); i++) {
            Mesh mesh = mRock.meshes.get(i);
            mesh.enableInstanceRender();
            mesh.bindVAO();
            ShaderProgram program = mRock.getShaderProgram();
            int matrixLoc = program.getAttribLocation("aInstanceMatrix");
            glEnableVertexAttribArray(matrixLoc);
            glVertexAttribPointer(matrixLoc, 4, GL_FLOAT, false, 4 * 16, 0);

            glEnableVertexAttribArray(matrixLoc+1);
            glVertexAttribPointer(matrixLoc+1, 4, GL_FLOAT, false, 4 * 16, 4*4);

            glEnableVertexAttribArray(matrixLoc+2);
            glVertexAttribPointer(matrixLoc+2, 4, GL_FLOAT, false, 4 * 16, 4*8);

            glEnableVertexAttribArray(matrixLoc+3);
            glVertexAttribPointer(matrixLoc+3, 4, GL_FLOAT, false, 4 * 16, 4*12);

            GL33.glVertexAttribDivisor(matrixLoc, 1);
            GL33.glVertexAttribDivisor(matrixLoc+1, 1);
            GL33.glVertexAttribDivisor(matrixLoc+2, 1);
            GL33.glVertexAttribDivisor(matrixLoc+3, 1);

            mesh.unbindVAO();
        }

//
//        ShaderProgram refractShader = new ShaderProgram();
//        refractShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_refract.glsl");
//        mWolf = ModelLoader.loadModel("resources/model/wolf/wolf.obj", refractShader);
//        mWolf.setScale(0.0036f);
//        mWolf.setPosition(new Vector3f(2.5f, 0, -1));
//        mWolf.setCamera(mCamera);
//        mWolf.setProjection(mProjMat);
//
//        ShaderProgram outlineModelShader = new ShaderProgram();
////        outlineModelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_refract.glsl");
////        outlineModelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs.glsl");
//        outlineModelShader.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_normal.glsl");
//
////        mRefractModel = ModelLoader.loadModel("resources/model/nanosuit/nanosuit.obj", outlineModelShader);
//        mRefractModel = ModelLoader.loadModel("resources/model/statue/12328_Statue_v1_L2.obj", outlineModelShader);
//        mRefractModel.setRotateDegree(-90);
//        mRefractModel.setRotateAxis(new Vector3f(1, 0, 0));
////        mRefractModel = ModelLoader.loadModel("resources/model/satellite/10477_Satellite_v1_L3.obj", outlineModelShader);
////        mRefractModel = ModelLoader.loadModel("resources/model/deer/deer.obj", outlineModelShader);
//        mRefractModel.setScale(0.01f);
//        mRefractModel.setPosition(new Vector3f(5, 0, -3));
//        mRefractModel.setCamera(mCamera);
//        mRefractModel.setProjection(mProjMat);
//
//
//        mFloor = new Sprite("resources/images/floor.jpg");
//        mFloor.setCamera(mCamera);
//        mFloor.setProjection(mProjMat);
//        mFloor.setPosition(new Vector3f(0, -1, 0));
//        float floorVertices[] = {
//                // positions          // texture Coords
//                8f, -0.5f,  8f,
//                -8f, -0.5f,  8f,
//                -8f, -0.5f, -8f,
//
//                8f, -0.5f,  8f,
//                -8f, -0.5f, -8f,
//                8f, -0.5f, -8f,
//        };
//        float floorTexcoord[] = {
//                8.0f, 0.0f,
//                0.0f, 0.0f,
//                0.0f, 8.0f,
//
//                8.0f, 0.0f,
//                0.0f, 8.0f,
//                8.0f, 8.0f
//        };
//        mFloor.setVertices(floorVertices, null,  floorTexcoord);
//
//
//        float[] grassVertices = {
//                // positions         // texture Coords (swapped y coordinates because texture is flipped upside down)
//                0.0f,  0.5f,  0.0f,
//                0.0f, -0.5f,  0.0f,
//                1.0f, -0.5f,  0.0f,
//
//                0.0f,  0.5f,  0.0f,
//                1.0f, -0.5f,  0.0f,
//                1.0f,  0.5f,  0.0f,
//        };
//
//        float[] grassTexCoord = {
//                0.0f,  0.0f,
//                0.0f,  1.0f,
//                1.0f,  1.0f,
//                0.0f,  0.0f,
//                1.0f,  1.0f,
//                1.0f,  0.0f
//        };
//
//        mGrass = new Sprite("resources/images/window.png", false);
//        mGrass.setVertices(grassVertices, null, grassTexCoord);
//        //mGrass.setPosition(new Vector3f());
//
//         Vector3f[] posArray = new Vector3f[]{
//            new Vector3f(-1.5f, -1.0f, -0.48f),
//            new Vector3f(1.5f, -1.0f, 0.51f),
//            new Vector3f(0.0f, -1.0f, 0.7f),
//            new Vector3f(-0.3f, -1.0f, -2.3f),
//            new Vector3f(0.5f, -1.0f, -0.6f),
//        };
//        grassPos = Arrays.asList(posArray);
//        grassPos.sort(new Comparator<Vector3f>() {
//            @Override
//            public int compare(Vector3f left, Vector3f right) {
//                return Float.compare(left.z, right.z);
//            }
//        });
//        for (Vector3f p : grassPos) {
//            System.out.println("p : " + p.z);
//        }
//
//        String[] cubemapImages = new String[] {
//            "resources/skybox/skybox_1/right.jpg",
//            "resources/skybox/skybox_1/left.jpg",
//            "resources/skybox/skybox_1/top.jpg",
//            "resources/skybox/skybox_1/bottom.jpg",
//            "resources/skybox/skybox_1/front.jpg",
//            "resources/skybox/skybox_1/back.jpg",
//        };
//        mSkybox = new Skybox("shader/skybox/vs.glsl", "shader/skybox/fs.glsl", cubemapImages);
//
//        mPoint = new GeometryPoint("shader/geometry/vs.glsl",
//                "shader/geometry/fs.glsl",
//                "shader/geometry/gs.glsl");
//
//        mInstanceRect = new InstanceRect("shader/instance/vs.glsl", "shader/2d_base/fs.glsl");

    }

    private void render(double deltaTime) {
        if (mLastTime <= 0) {
            mLastTime = glfwGetTime();
        }
        mDeltaTime = glfwGetTime() - mLastTime;
//        mSingleLightLayer.render(deltaTime);
        //mPoint.render(deltaTime);
        mModel.render(deltaTime);
        mRock.render(deltaTime);

//        mInstanceRect.render(deltaTime);

//        mFloor.render(deltaTime);
//
//        for (Vector3f pos : grassPos) {
//            mGrass.setPosition(pos);
//            mGrass.render(deltaTime);
//        }
//
//        mModel.render(deltaTime);
//        mRefractModel.render(deltaTime);
//        mWolf.render(deltaTime);
//
//        mSkybox.render(deltaTime);

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