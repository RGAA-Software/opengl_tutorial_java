package com.sk.tutorial.shader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {

    private int program;

    public ShaderProgram() {
    }

    public ShaderProgram(String vs, String fs) {
        initWithShaderSource(vs, fs);
    }

    public void initWithShaderPath(String vsPath, String fsPath) {
        String vs = readFileAsString(vsPath);
        String fs = readFileAsString(fsPath);
        if (vs == null || fs == null) {
            System.out.println("error : " + vsPath + " " + fsPath);
            return;
        }
        initWithShaderSource(vs, fs);
    }

    private void initWithShaderSource(String vs, String fs) {
        int vtShader = genShader(vs, GL_VERTEX_SHADER);
        int fsShader = genShader(fs, GL_FRAGMENT_SHADER);

        program = glCreateProgram();
        glAttachShader(program, vtShader);
        glAttachShader(program, fsShader);
        glLinkProgram(program);

        glDeleteShader(vtShader);
        glDeleteShader(fsShader);
    }

    private int genShader(String source, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        int[] ret = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, ret);
        if (ret[0] != 1) {
            String shaderLog = glGetShaderInfoLog(shader);
            System.out.println("shader : " + source + " \nErr : " + shaderLog);
        } else {
            System.out.println("compile shader ok .");
        }
        return shader;
    }

    public void use() {
        glUseProgram(program);
    }

    public int getProgram() {
        return program;
    }

    private String readFileAsString(String path) {
        FileInputStream fis = null;
        String data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fis = new FileInputStream(path);
            byte[] buffer = new byte[1024];
            int readBytes = fis.read(buffer);
            while (readBytes != -1) {
                baos.write(buffer, 0, readBytes);
                readBytes = fis.read(buffer);
            }

            baos.flush();
            data = baos.toString();
            baos.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
