package com.sk.tutorial;

import com.sk.tutorial.camera.Camera;
import com.sk.tutorial.framebuffer.FrameBuffer;
import com.sk.tutorial.geometry.*;
import com.sk.tutorial.input.InputProcessor;
import com.sk.tutorial.layer.SingleLightCubeLayer;
import com.sk.tutorial.light.Light;
import com.sk.tutorial.model.Model;
import com.sk.tutorial.model.ModelLoader;
import com.sk.tutorial.model.Vertex;
import com.sk.tutorial.renderer.MixColorSprite;
import com.sk.tutorial.renderer.Sprite;
import com.sk.tutorial.shader.ShaderProgram;
import com.sk.tutorial.ui.FrameBufferPreview;
import com.sk.tutorial.world.Director;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
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

    private int mShadowMapSize = 2048;

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

    private SingleLightCubeLayer mSingleLightLayer;
    private SingleLightCubeLayer mSingleCube;
    private Model mModel;
    private Model mNanoSuit;
    private Model mWolf;

    private Sprite mFloor;
    private Sprite mWall;
    private Sprite mFirstNormalRect;
    private Sprite mSecondNormalRect;
    private Sprite mTestImage;
    private MixColorSprite mTempImage;
//    private FrameBufferPreview mFrameBufferPreview;
//    private FrameBufferPreview mMainScene;
    private FrameBufferPreview mBlurPreview;
    private FrameBufferPreview mOriginPreview;

    private Matrix4f mShadowView;

    private ShaderProgram mPointDepthShaderProgram;
    private ShaderProgram modelShaderProgram;

    private Matrix4f mCubeProj = new Matrix4f();
    private Matrix4f[] mCubeViews = new Matrix4f[6];

    private float mSunY = 1.5f;
    private float MAX_SUN_Y = 8;
    private float MIN_SUN_Y = 0.9f;
    private boolean isUping = true;

    private Light light;
    private List<Light> mLights;

    private List<Light> generateLights() {
        List<Light> lights = new ArrayList<>();

        Light lt = new Light();
        lt.direction = new Vector3f(0.8f, -1.0f, 1.5f);
        lt.position = new Vector3f(0.3f, 2.1f, -1.0f);
        lt.ambient = new Vector3f(0.1f, 0.1f, 0.1f);
        lt.diffuse = new Vector3f(0.6f, 0.6f, 0.6f);
        lt.specular = new Vector3f(0.3f, 0.3f, 0.3f);
        lt.constant = 1.0f;
        lt.linear = 0.09f;
        lt.quadratic = 0.032f;
        lights.add(lt);

        lt = lt.copy();
        lt.position = new Vector3f(-8.1f, MIN_SUN_Y, -8.0f);
        lt.diffuse = new Vector3f(1.0f, 0, 0);
        lights.add(lt);

        lt = lt.copy();
        lt.position = new Vector3f(-4.3f, MIN_SUN_Y, -8.0f);
        lt.diffuse = new Vector3f(0, 1.0f, 0);
        lights.add(lt);

//        lt = lt.copy();
//        lt.position = new Vector3f(-1.4f, MIN_SUN_Y, -1.0f);
//        lt.diffuse = new Vector3f(.5f, .5f, .5f);
//        lights.add(lt);

        light = lights.get(0);
        return lights;
    }

    private void prepare() {
        Matrix4f mProjMat = new Matrix4f()
                .perspective((float) Math.toRadians(45),
                        width / height,
                        0.1f, 1000.0f);

        Matrix4f mOrthoProjMat = new Matrix4f()
                .ortho(-10, 10, -10, 10, -10, 10);

        float near = 0.1f;
        float far = 25f;
        mCubeProj = mCubeProj.perspective((float)Math.toRadians(90.0f), mShadowMapSize/mShadowMapSize, near, far);

        mLights = generateLights();

        mCubeViews[0] = (new Matrix4f().lookAt(light.position, new Vector3f(light.position).add(new Vector3f(1.0f,0.0f,0.0f)), new Vector3f(0.0f,-1.0f,0.0f)));
        mCubeViews[1] = (new Matrix4f().lookAt(light.position, new Vector3f(light.position).add(new Vector3f(-1.0f,0.0f,0.0f)), new Vector3f(0.0f,-1.0f,0.0f)));
        mCubeViews[2] = (new Matrix4f().lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f,1.0f,0.0f)), new Vector3f(0.0f,0.0f,1.0f)));
        mCubeViews[3] = (new Matrix4f().lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f,-1.0f,0.0f)), new Vector3f(0.0f,0.0f,-1.0f)));
        mCubeViews[4] = (new Matrix4f().lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f,0.0f,1.0f)), new Vector3f(0.0f,-1.0f,0.0f)));
        mCubeViews[5] = (new Matrix4f().lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f,0.0f,-1.0f)), new Vector3f(0.0f,-1.0f,0.0f)));

        Director.getInstance()
                .setProjection(mProjMat)
                .setOrthoProjection(mOrthoProjMat)
                .setCubeProjection(mCubeProj)
                .setCamera(mCamera);

        mFrameBuffer = new FrameBuffer();
        mFrameBuffer.init((int)width, (int)height);

//        mCubeFrameBuffer = new CubeDepthFrameBuffer();
//        mCubeFrameBuffer.init(mShadowMapSize, mShadowMapSize);

        mPointDepthShaderProgram = new ShaderProgram();
        mPointDepthShaderProgram.initWithShaderPath("shader/cube_map/vs.glsl", "shader/cube_map/fs.glsl", "shader/cube_map/gs.glsl");

        modelShaderProgram = new ShaderProgram();
        modelShaderProgram.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_normal_point.glsl");


        mShadowView = new Matrix4f()
                .identity()
                .lookAt(light.position, light.direction, new Vector3f(0, 1, 0));


        mModel = ModelLoader.loadModel("resources/model/cyborg/cyborg.obj", modelShaderProgram);
        mModel.setCamera(mCamera);
        mModel.setProjection(mProjMat);
        mModel.setScale(0.4f);
        mModel.setPosition(new Vector3f(0, 1, 0));
        mModel.addLight(light);
        mModel.enableDebugRotate();

//        ShaderProgram wolfShaderProgram = new ShaderProgram();
//        wolfShaderProgram.initWithShaderPath("shader/model/vs.glsl", "shader/model/fs_normal.glsl");
//        mWolf = ModelLoader.loadModel("resources/model/hehua/hehua.obj", wolfShaderProgram);
//        mWolf.setCamera(mCamera);
//        mWolf.setProjection(mProjMat);
//        mWolf.setScale(0.0023f);
//        mWolf.setPosition(new Vector3f(-1, -0.25f, 0));
//        mWolf.setLight(sun);


//        mNanoSuit = ModelLoader.loadModel("resources/model/nanosuit/nanosuit.obj", modelShaderProgram);
//        mNanoSuit.setCamera(mCamera);
//        mNanoSuit.setProjection(mProjMat);
//        mNanoSuit.setScale(0.1f);
//        mNanoSuit.setPosition(new Vector3f(1, 1, -1));
//        mNanoSuit.addLight(light);
//        mNanoSuit.enableDebugRotate();

//        mBoxLayer = new MultiBoxLayer(mCamera, mProjMat, "shader/base/vs.glsl", "shader/base/fs.glsl");
        mSingleLightLayer = new SingleLightCubeLayer(mCamera, mProjMat, "shader/light_cube/vs.glsl", "shader/light_cube/fs.glsl");
        //Vector3f lightDirection = new Vector3f(sun.direction);
        //lightDirection = lightDirection.sub(-1, 0, 0);
        mSingleLightLayer.setPosition(light.position);
        mSingleLightLayer.setScale(0.12f);
        mSingleLightLayer.addLight(light);
        mSingleLightLayer.enableRotate();

        mSingleCube = new SingleLightCubeLayer(mCamera, mProjMat, "shader/light_cube/vs.glsl", "shader/light_cube/fs.glsl");
//        mSingleCube.setCubeMap(mCubeFrameBuffer.getFrameBufferTexId());
        mSingleCube.setScale(0.5f);
        mSingleCube.setPosition(new Vector3f(-3, 1f, 0));
        //mSingleCube.setColor(new Vector3f(0.5f, 0.5f, 0.5f));
        mSingleCube.addLight(light);

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE);
        glPointSize(8);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_FRONT);

        boolean hdr = false;

        mFloor = new Sprite("resources/images/wood.png", false, "shader/sprite/vs.glsl", "shader/sprite/fs_blinn_point.glsl", GL_RGBA, hdr);
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
        mFloor.addBatchLights(mLights);

        mWall = new Sprite("resources/images/wood.png", false, "shader/sprite/vs.glsl", "shader/sprite/fs_blinn_point.glsl", GL_RGBA, false);
        mWall.setRotateDegree(90);
        mWall.setRotateAxis(new Vector3f(0, 0, 1));
        mWall.setVertices(grassVertices, grassNormals, grassTexCoord);
        mWall.setPosition(new Vector3f(3, 0, 0));
        mWall.addBatchLights(mLights);

        mFirstNormalRect = new Sprite("resources/images/brickwall.jpg", "resources/images/brickwall_normal.jpg",false, "shader/sprite/vs.glsl", "shader/sprite/fs_blinn_point.glsl", GL_RGB, false);
        float[] rectVertices = {

                1f, 1f, 0.0f,
                1f, -1f, 0.0f,
                -1f, 1f, 0.0f,

                1f, -1f, 0.0f,
                -1f, -1f, 0.0f,
                -1f, 1f, 0.0f
        };

        List<Vertex> mMixAttribs = new ArrayList<>();
        mMixAttribs.add(new Vertex(new Vector3f(1f, 1f, 0.0f), new Vector3f(1, 0, 0)));
        mMixAttribs.add(new Vertex(new Vector3f(1f, -1f, 0.0f), new Vector3f(0, 1, 0)));
        mMixAttribs.add(new Vertex(new Vector3f( -1f, 1f, 0.0f), new Vector3f(0, 0, 1)));

        mMixAttribs.add(new Vertex(new Vector3f(1f, -1f, 0.0f), new Vector3f(0, 1, 0)));
        mMixAttribs.add(new Vertex(new Vector3f(-1f, -1f, 0.0f), new Vector3f(1, 1, 0)));
        mMixAttribs.add(new Vertex(new Vector3f( -1f, 1f, 0.0f), new Vector3f(0, 0, 1)));

        float[] rectNormals = {
                0, 0, 1.0f,
                0, 0, 1.0f,
                0, 0, 1.0f,
                0, 0, 1.0f,
                0, 0, 1.0f,
                0, 0, 1.0f,
        };

        float[] rectTexCoords = {
                1, 1,
                1, 0,
                0, 1,
                1, 0,
                0, 0,
                0, 1
        };
        mFirstNormalRect.setVertices(rectVertices, rectNormals, rectTexCoords);
        mFirstNormalRect.setPosition(new Vector3f(-3, 1, -3));
        mFirstNormalRect.addBatchLights(mLights);

        mSecondNormalRect = new Sprite("resources/images/brickwall.jpg", "resources/images/brickwall_normal.jpg",false, "shader/sprite/vs.glsl", "shader/sprite/fs_blinn_point.glsl", GL_RGB, false);
        mSecondNormalRect.setRotateDegree(-90);
        mSecondNormalRect.setRotateAxis(new Vector3f(1, 0, 0));
        mSecondNormalRect.setVertices(rectVertices, rectNormals, rectTexCoords);
        mSecondNormalRect.setPosition(new Vector3f(-2, 0.02f, -2));
        mSecondNormalRect.addBatchLights(mLights);

        mTempImage = new MixColorSprite("resources/images/test.png", "resources/images/test_split.png",true, "shader/sprite/vs_mix_color.glsl", "shader/sprite/fs_blinn_point_mix_color.glsl", GL_RGB);

        Square square = TriangleGen.genTrianglesForImage(20, false);
        square = TriangleGen.modifyVertexColor(square);
        square = TriangleGen.modifyVertexColor1(square);
        float[] genTriangleBuffer = TriangleGen.transferToBuffer(square.vertices);

        TempSquare tempSquare = TempSquareGen.generate(32, 32);
        float[] triangleBuffer = TempSquareGen.transferToBuffer(tempSquare);

        IndexTempSquare indexTempSquare = TempSquareGen.generateIndexTempSquare(32, 32);
        float[] verticleBuffer = TempSquareGen.transferToVerticesBuffer(indexTempSquare);
        int[] indexBuffer = TempSquareGen.transferToIndexBuffer(indexTempSquare);
        TempSquareGen.calculateColor(indexTempSquare);
        float[] indexColorBuffer = TempSquareGen.transferToColorBuffer(indexTempSquare);
        float[] indexUVBuffer = TempSquareGen.transferToUVBuffer(indexTempSquare);


        //mTempImage.setPosColorAttribs(triangleBuffer);
        mTempImage.setVerticesIndexBuffer(verticleBuffer, indexUVBuffer, indexColorBuffer, indexBuffer);
        mTempImage.setScaleAxis(new Vector3f(1920f/1080, 1.0f, 1));
        mTempImage.setPosition(new Vector3f(0, 0, -1));

        mTempImage.addBatchLights(mLights);

        mTestImage = new Sprite("resources/images/pic1.jpg",true, "shader/sprite/vs.glsl", "shader/sprite/fs_blinn_point.glsl", GL_RGB, false);
        mTestImage.setVertices(rectVertices, rectNormals, rectTexCoords);
        mTestImage.setScaleAxis(new Vector3f(1f, 1801.0f/1024, 1));
        mTestImage.setPosition(new Vector3f(-3, 4, -5));
        mTestImage.addBatchLights(mLights);
//
//        mFrameBufferPreview = new FrameBufferPreview(mFrameBuffer.getFrameBufferTexId(), "shader/2d_base/fs_luma.glsl");
//        mFrameBufferPreview.setTranslate(new Vector3f(0.25f, 0.75f, 0));
//        mFrameBufferPreview.setScale(0.25f);

        mBlurPreview = new FrameBufferPreview(mFrameBuffer.getFrameBufferId(), "shader/2d_base/fs_blur.glsl");
        mBlurPreview.setTranslate(new Vector3f(0, 0, 0));

        mOriginPreview = new FrameBufferPreview(mFrameBuffer.getFrameBufferId(), "shader/2d_base/fs_image.glsl");
        mOriginPreview.setTranslate(new Vector3f(0.75f, 0.75f, 0));
        mOriginPreview.setScale(0.25f);

//        mMainScene = new FrameBufferPreview(mFrameBuffer.getFrameBufferTexId(), mFrameBuffer.getFrameBufferTexId2(), "shader/2d_base/fs_compose_two_pass.glsl");
//        mMainScene.setTranslate(new Vector3f(0, 0, 0));
//        mMainScene.setScale(1.0f);

        mModel.setCubeViews(mCubeViews);
//        mNanoSuit.setCubeViews(mCubeViews);
        mWall.setCubeViews(mCubeViews);

//        glPolygonMode(GL_FRONT_AND_BACK ,GL_LINE );

    }

    private FrameBuffer mFrameBuffer;
//    private CubeDepthFrameBuffer mCubeFrameBuffer;

    private void render(double deltaTime) {
        if (mLastTime <= 0) {
            mLastTime = glfwGetTime();
        }
        mDeltaTime = glfwGetTime() - mLastTime;

//        if (isUping) {
//            if (light.position.y <= MAX_SUN_Y) {
//                light.position.y += deltaTime/2;
//            } else {
//                isUping = false;
//            }
//        }else {
//            if (light.position.y >= MIN_SUN_Y) {
//                light.position.y -= deltaTime/2;
//            } else {
//                isUping = true;
//            }
//        }

//        mCubeViews[0].identity();
//        mCubeViews[1].identity();
//        mCubeViews[2].identity();
//        mCubeViews[3].identity();
//        mCubeViews[4].identity();
//        mCubeViews[5].identity();
//
//        mCubeViews[0].lookAt(light.position, new Vector3f(light.position).add(new Vector3f(1.0f, 0.0f, 0.0f)), new Vector3f(0.0f, -1.0f, 0.0f));
//        mCubeViews[1].lookAt(light.position, new Vector3f(light.position).add(new Vector3f(-1.0f, 0.0f, 0.0f)), new Vector3f(0.0f, -1.0f, 0.0f));
//        mCubeViews[2].lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f, 1.0f, 0.0f)), new Vector3f(0.0f, 0.0f, 1.0f));
//        mCubeViews[3].lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f, -1.0f, 0.0f)), new Vector3f(0.0f, 0.0f, -1.0f));
//        mCubeViews[4].lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f, 0.0f, 1.0f)), new Vector3f(0.0f, -1.0f, 0.0f));
//        mCubeViews[5].lookAt(light.position, new Vector3f(light.position).add(new Vector3f(0.0f, 0.0f, -1.0f)), new Vector3f(0.0f, -1.0f, 0.0f));


//        glViewport(0, 0, mShadowMapSize, mShadowMapSize);
//        mCubeFrameBuffer.begin();
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glClearColor(.2f, 0.2f, 0.2f, 1.0f);
//
//        mModel.startRenderPointLightShadow();
//        mModel.setShaderProgram(mPointDepthShaderProgram);
//        mNanoSuit.startRenderPointLightShadow();
//        mNanoSuit.setShaderProgram(mPointDepthShaderProgram);
//
//        mModel.render(deltaTime);
//        mNanoSuit.render(deltaTime);
//        mWall.render(deltaTime);
//
//        mCubeFrameBuffer.end();

//        glViewport(0, 0, (int)width, (int)height);
        mFrameBuffer.begin();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(.2f, 0.2f, 0.2f, 1.0f);

//        mModel.stopRenderPointLightShadow();
//        mModel.setShaderProgram(modelShaderProgram);
//        mNanoSuit.stopRenderPointLightShadow();
//        mNanoSuit.setShaderProgram(modelShaderProgram);
//        mWall.stopRenderPointLightShadow();
        //mWall.setShaderProgram();

//        mModel.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());
//        mWolf.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());
//        mNanoSuit.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());
//        mFloor.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());
//        mWall.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());
//        mSingleLightLayer.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());
//        mSingleCube.bindShadowMap(mCubeFrameBuffer.getFrameBufferTexId());

        //mSingleCube.render(deltaTime);
        //mSingleLightLayer.render(deltaTime);
        //mFloor.render(deltaTime);
        //mWall.render(deltaTime);
        //mModel.render(deltaTime);
        //mWolf.render(deltaTime);
//        mNanoSuit.render(deltaTime);
        //mFirstNormalRect.render(deltaTime);
        //mSecondNormalRect.render(deltaTime);
        //mTestImage.render(deltaTime);
        mTempImage.render(deltaTime);
        mFrameBuffer.end();
//
//
//        //glClearColor(.2f, 0.2f, 0.2f, 1.0f);
//        mFrameBufferPreview.render(deltaTime);
        mOriginPreview.render(deltaTime);
        mBlurPreview.render(deltaTime);
//        mMainScene.render(deltaTime);

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