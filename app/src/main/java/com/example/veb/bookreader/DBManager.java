package com.example.veb.bookreader;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VEB on 2016/10/11.
 * 遍历以找到.txt文件，并存入list中
 */
public class DBManager {

    final static String TAG = "DBManager";
    private Context context;
    private SQLiteDatabase db;
    private FileSize size;

    public DBManager(Context context) {
        this.context = context;
        //创建数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    protected List<TxtFile> getNameList(File bookFiles) {
        if (!bookFiles.exists()) {
            boolean isMkdirs = bookFiles.mkdirs();
            if (isMkdirs) {
                ToastUtil.showToast(context, R.string.mkDir, 0);
            } else
                ToastUtil.showToast(context, R.string.mkDirEr, 0);
        } else
            Log.d(TAG, "=================文件已存在");

        List<TxtFile> browserList = new ArrayList<>();
        TxtFile txtFile = getDirInfo(bookFiles);
        for (File f : files) {


        }
        if (txtFile != null) {
            Log.d(TAG, "================添加到表中");
            browserList.add(txtFile);
        } else {
            Log.d(TAG, "=================文件不是文本文件");
        }
        return browserList;
    }

    private TxtFile getDirInfo(File file) {
        File[] files = file.listFiles();
        TxtFile txtFile = null;
        if (files != null) {
            Log.d(TAG, "=================Dir循环ing");
            if (file.isDirectory()) {
                Log.d(TAG, "=================目录");
                txtFile = getDirInfo(file);
            } else {
                Log.d(TAG, "=================文件fffffff");
                txtFile = getFileInfo(file);
            }

        } else {
            ToastUtil.showToast(context, R.string.isEmpty, 0);
        }
        return txtFile;
    }

    private TxtFile getFileInfo(File file) {
        TxtFile txtFile = null;
        String fileName = file.getName();
        size = new FileSize();
        if (fileName.endsWith(".txt")) {
            Log.d(TAG, "=================获取txt结尾的文件");
            long fileSize = size.DirSize(file);
            txtFile = new TxtFile(file.getAbsolutePath(), fileName, fileSize);
        }
        return txtFile;
    }

    protected void saveShelfToDb(TxtFile txtFile) {

        String SQL_EXIST = "SELECT NAME, ADDRESS FROM BookInfo WHERE ADDRESS = ?";
        Cursor cursor = db.rawQuery(SQL_EXIST, new String[]{txtFile.getTxtFilePath()});
        if (cursor == null || !cursor.moveToFirst()) {//判断是否存在该书籍
            String SQL_INSERT_SHELF = "INSERT INTO BookInfo(" +
                    "NAME, ADDRESS, BOOK_SIZE) VALUES(?,?,?)";
            Object[] args = new Object[]{
                    txtFile.getTxtFileName(), txtFile.getTxtFilePath(), txtFile.getTxtFileSize()
            };
            db.execSQL(SQL_INSERT_SHELF, args);
        } else {
            cursor.close();
        }

    }

    private void saveCacheToDb(String bookPath, String cache, int flags) {

        switch (flags) {
            case 1: {//向前阅读
                ContentValues values = new ContentValues();
                values.put("CACHE_HEAD", cache);
                db.update("BookInfo", values, "ADDRESS = ?", new String[]{bookPath});
                break;
            }

            case 2: {//向后阅读
                String SQL_UPDATE_CACHE_BODY = "UPDATE BookInfo " +
                        "SET CACHE_BODY = ?"
//                            + DatabaseUtils.sqlEscapeString(cache)
                        + " WHERE ADDRESS = ?";
                Object args[] = new Object[]{cache, bookPath};
                db.execSQL(SQL_UPDATE_CACHE_BODY, args);
                break;
            }
        }
    }

    protected List<TxtFile> getShelfFromDb() {
        List<TxtFile> shelfList = new ArrayList<>();
        TxtFile txtFile;

        String SQL_QUERY = "SELECT NAME,ADDRESS,BOOK_SIZE FROM BookInfo";
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("NAME"));
                String filePath = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                int fileSize = cursor.getInt(cursor.getColumnIndex("BOOK_SIZE"));
                txtFile = new TxtFile(filePath, fileName, fileSize);
                shelfList.add(txtFile);
            } while (cursor.moveToNext());
        } else {
            ToastUtil.showToast(GlobalApplication.getContext(), "书架是空的，请选择书籍", 0);
        }
        try {
            if (cursor != null)
                cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return shelfList;
    }

    private String getCacheHead(String bookPath) {
        String cache = "";
        String SQL_HEAD_CACHE = "SELECT CACHE_HEAD FROM BookInfo WHERE ADDRESS = ?";
        String[] args = new String[]{bookPath};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SQL_HEAD_CACHE, args);
            if (null != cursor && cursor.moveToFirst()) {
                cache = cursor.getString(cursor.getColumnIndex("CACHE_HEAD"));
            } else {
                Log.d(TAG, "####################### HEAD_CACHE不存在");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cache;
    }

    private String getCacheBody(String bookPath) {
        String cache = "";
        String SQL_BODY_CACHE = "SELECT CACHE_BODY FROM BookInfo WHERE ADDRESS = ?";
        String[] args = new String[]{bookPath};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SQL_BODY_CACHE, args);
            if (null != cursor && cursor.moveToFirst()) {
                cache = cursor.getString(cursor.getColumnIndex("CACHE_BODY"));
            } else {
                Log.d(TAG, "####################### BODY_CACHE不存在");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cache;
    }

    private boolean isNeedTocache(String bookPath) {
        boolean check = true;
        FileSize fileSize = new FileSize();
        File file = new File(bookPath);
        try {
            long size = fileSize.DirSize(file);
            if (8192 >= size) {
                check = false;
            } else {
                check = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    private void cacheBook(String bookPath, int skipNum, int flags) {

        File bookFile = new File(bookPath);
        int tmp = skipNum * (8 * 1024) - 64;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(bookFile));
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(bis, "GBK"));
            char[] args = new char[8 * 1024];
            if (isFirstOpen(bookPath)) { //第一次打开
                bufReader.read(args);
                saveCacheToDb(bookPath, new String(args), 1);
                bufReader.skip(8 * 1024 - 64);
                bufReader.read(args);
                saveCacheToDb(bookPath, new String(args), 2);
            } else {//不是第一次打开,cache到第skipNum次
                bufReader.skip(tmp);
                bufReader.read(args);
                saveCacheToDb(bookPath, new String(args), flags);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getBookFromDb(String bookPath, int skipNum, int flags) {
        String cache = "";
        if (isNeedTocache(bookPath)) {
//        if (isFirstOpen(bookPath)) {
            switch (flags) {
                case 1: {//向前阅读
                    cacheBook(bookPath, skipNum, flags);
                    cache = getCacheHead(bookPath);
                    break;
                }
                case 2: {//向后阅读
                    cacheBook(bookPath, skipNum, flags);
                    cache = getCacheBody(bookPath);
                    break;
                }
                default:
                    break;
            }
        } else {

            try {
                File bookFile = new File(bookPath);
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(bookFile));
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(bis, "GBK"));
                String len = "";
                StringBuilder builder = new StringBuilder();
                while ((len = bufReader.readLine()) != null) {
                    builder.append(len);
                }
                cache = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cache;
    }

    private boolean isFirstOpen(String bookPath) {

        String SQL_ISFIRST = "SELECT CACHE_BODY,CACHE_HEAD FROM BookInfo WHERE ADDRESS = ?";
        String args[] = new String[]{bookPath};
        Cursor cursor = db.rawQuery(SQL_ISFIRST, args);

        boolean check = null == cursor || !cursor.moveToFirst();

        if (cursor != null)
            cursor.close();

        return check;
    }

    protected int getSkipNum(String bookPath) {
        int skipNum = 1;

        String SQL_SKIP_NUM = "SELECT SKIP_NUM FROM BookInfo WHERE ADDRESS = ?";
        String args[] = new String[]{bookPath};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SQL_SKIP_NUM, args);
            if (cursor != null && cursor.moveToFirst()) {
                skipNum = cursor.getInt(cursor.getColumnIndex("SKIP_NUM"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return skipNum;
    }

    protected void setSkipNum(String bookPath, int skipNum) {
        String SQL_UPDATE_SKIP_NUM = "UPDATE BookInfo SET SKIP_NUM = ? WHERE ADDRESS = ?";
        Object objects[] = new Object[]{skipNum, bookPath};
        db.execSQL(SQL_UPDATE_SKIP_NUM, objects);
    }

    protected void delBookFromDb(String bookPath) {
        String SQL_DELETE_BOOK = "DELETE FROM BookInfo WHERE ADDRESS = ?";
        Object args[] = new Object[]{bookPath};
        db.execSQL(SQL_DELETE_BOOK, args);
    }


}


