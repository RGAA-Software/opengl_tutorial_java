package com.sk.tutorial;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {

    public static byte[] readFileAsByteArray(String path) {
        FileInputStream fis = null;
        byte[] data = null;
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
            data = baos.toByteArray();
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
