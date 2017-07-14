package net.bat.store.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by bingbing.li on 2017/1/17.
 */

public class FileManagerUtils {

    private static final String TAG = "FileManagerUtils";
    private FileManagerUtils() {
        throw new AssertionError();
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public static void writeFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return ;
        }
        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            if (append && new File(filePath).length() > 0) {
                fileWriter.append("\r\n");
            }
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileWriter);
        }
    }

    public static void writeLog(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Writer writer = null;
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            makeDirs(filePath);
            fileOutputStream = new FileOutputStream(filePath, append);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            writer = new BufferedWriter(outputStreamWriter);
            if (append && new File(filePath).length() > 0) {
                writer.append("\r\n");
            }
            writer.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
            IOUtils.close(outputStreamWriter);
            IOUtils.close(fileOutputStream);
        }
    }



    public static String getConfigure(String fileName) {
        Log.i(TAG, "getConfigure: fileName:"+fileName);
        if(!(new File(fileName).exists())){
            return null;
        }
        FileInputStream inStream=null;
        ByteArrayOutputStream outStream=null;
        try {
            inStream = new FileInputStream(fileName);
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            return outStream.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(outStream!=null){
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(inStream!=null){
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void makeDirs(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        File parentFile=file.getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
