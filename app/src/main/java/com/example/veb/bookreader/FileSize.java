package com.example.veb.bookreader;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by VEB on 2016/11/9.
 */

public class FileSize {

    private static final String TAG_FileSize = "FileSize";

    private long FileSize(File file) {
        long fileSize = 0;
        if (file.exists()) {
            fileSize = file.length();
        } else {
            Log.d(TAG_FileSize, "文件不存在");
        }
        return fileSize;
    }

    public long DirSize(File dir) {
        long dirSize = 0;
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File files[] = dir.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        dirSize += DirSize(file);
                    } else {
                        dirSize += FileSize(file);
                    }
                }
            }
        }
        return dirSize;
    }

    private String FormatSize(long size) {
        String formats = "";
        DecimalFormat df = new DecimalFormat("#.0");
        if (size == 0) {
            return "0B";
        } else if (size < 1024) {
            formats = df.format( size )+ "B";
        } else if (size < 1024 * 1024) {
            formats = df.format( size/1024 ) +"KB";
        }else if (size < 1024 * 1024 * 1024){
            formats = df.format( size/(1024 * 1024) )+ "MB";
        }else {
            formats = df.format( size/(1027 * 1024 *1024) )+ "GB";
        }
        return formats;
    }

    public String getFileOrDirSize(String filePath){
        String return_size = "";
        File file = new File(filePath);
        return_size = FormatSize(DirSize(file));
        return return_size;
    }
}
