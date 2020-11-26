package com.sk.tutorial;

import com.sk.tutorial.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.*;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Main {

    // The window handle
    private long window;

    private ShaderProgram program;

    private float width = 1920;
    private float height = 1080;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

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

    private void init() {
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

        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, 1);

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

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private int texture;
    private int texture2;

    private Matrix4f model;
    private Matrix4f view;
    private Matrix4f proj;

    private void prepare() {

        model = new Matrix4f();
        view = new Matrix4f();
        //view = view.translate(0, 0, -5);
//        view = view.lookAt(new Vector3f(0, 0, 3),
//                new Vector3f(0, 0, 0),
//                new Vector3f(0, 1, 0));
        proj = new Matrix4f().perspective((float)Math.toRadians(45), width/height, 0.1f, 100.0f);

        program = new ShaderProgram();
        program.initWithShaderPath("shader/base/vs.glsl", "shader/base/fs.glsl");

        float vertices[] = {
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };



        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        int posLoc = glGetAttribLocation(program.getProgram(), "aPos");
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 5*4, 0);
        glEnableVertexAttribArray(posLoc);

        int texLoc = glGetAttribLocation(program.getProgram(), "aTex");
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 5*4, 3*4);
        glEnableVertexAttribArray(texLoc);



        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int[] x = new int[1];
        int[] y = new int[1];
        int[] c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer imageData = STBImage.stbi_load("resources/images/image1.jpg", x, y, c, 3);
        if (imageData != null) {
            System.out.println("x : " + x[0] + " y : " + y[0] + " c : " + c[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }


        texture2 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        x = new int[1];
        y = new int[1];
        c = new int[1];
        STBImage.stbi_set_flip_vertically_on_load(true);
        imageData = STBImage.stbi_load("resources/images/image2.jpg", x, y, c, 3);
        if (imageData != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(imageData);
        }

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
    }

    private Random random = new Random();

    private void render() {
        program.use();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2);

        glUniform1i(glGetUniformLocation(program.getProgram(), "image1"), 0);
        glUniform1i(glGetUniformLocation(program.getProgram(), "image2"), 1);

        int i = 0;
        for (Vector3f point : boxPositions) {

            if (rotateFactor[i] == 0) {
                rotateFactor[i] = random.nextInt(28) + 1;
            }


            try (MemoryStack stack = MemoryStack.stackPush()){
                model = model.identity();
                //model = model.rotate((float)Math.toRadians(-45), 1, 0, 0);
                model = model.translate(point);
                model = model.rotate((float)Math.toRadians(glfwGetTime() * rotateFactor[i]), 0, 0, 1);

                float x = (float) (12 * Math.sin(glfwGetTime()/5));
                float z = (float) (12 * Math.cos(glfwGetTime()/5));
                view = view.identity();
                view = view.lookAt(new Vector3f(x, 0, z),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 1, 0));

                int modelLoc = glGetUniformLocation(program.getProgram(), "model");
                int viewLoc = glGetUniformLocation(program.getProgram(), "view");
                int projLoc = glGetUniformLocation(program.getProgram(), "proj");

                glUniformMatrix4fv(modelLoc, false, model.get(stack.mallocFloat(16)));
                glUniformMatrix4fv(viewLoc, false, view.get(stack.mallocFloat(16)));
                glUniformMatrix4fv(projLoc, false, proj.get(stack.mallocFloat(16)));
            }
            glDrawArrays(GL_TRIANGLES, 0, 36);

            i++;
        }
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(.2f, 0.26f, 0.2f, 1.0f);

        prepare();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            render();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}