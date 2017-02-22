package com.example.veb.bookreader;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by VEB on 2016/10/11.
 * 遍历以找到.txt文件，并存入list中
 */

public class DBManager {

    private Context context;
    private SQLiteDatabase db;
    private FileSizeUtil size;
    private CheckChineseUtil isChinese;

    public DBManager(Context context) {
        this.context = context;
        //创建数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    private boolean isCursorExist(Cursor cursor) {
        return cursor != null && cursor.moveToFirst();
    }

    private boolean isNeedToCache(String bookPath) {
        boolean check = true;
        FileSizeUtil fileSize = new FileSizeUtil();
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

    protected boolean isFirstOpen(String bookPath) {
        String SQL_SKIP_NUM_EXIST = "SELECT SKIP_NUM FROM BookInfo WHERE ADDRESS=?";
        String args[] = new String[]{bookPath};
        Cursor cursor = db.rawQuery(SQL_SKIP_NUM_EXIST, args);
        if (isCursorExist(cursor)) {
            int check = cursor.getInt(cursor.getColumnIndex("SKIP_NUM"));
            if (check == 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected boolean isFirstPage(int skipNum) {
        if (skipNum == 1) {
            return true;
        } else {
            return false;
        }
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

    protected List<MyTxtInfo> getNameList(File bookFiles) {

        List<MyTxtInfo> browserList = new ArrayList<>();

        File[] files = bookFiles.listFiles();

        MyTxtInfo myTxtInfo = null;
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getNameList(f);//只需要txt
                } else {
                    myTxtInfo = getTxtFileName(f);
                    if (myTxtInfo != null) {
                        browserList.add(myTxtInfo);
                    } else {
                        LogUtil.d("文件不是文本文件");
                    }
                }
            }
        } else {
            ToastUtil.showToast(context, R.string.isEmpty, 0);
        }
        return browserList;
    }

    private MyTxtInfo getTxtFileName(File file) {
        MyTxtInfo myTxtInfo = null;
        String fileName = file.getName();
        size = new FileSizeUtil();
        if (fileName.endsWith(".txt")) {
            String fileSize = size.getFileOrDirSize(file);
            myTxtInfo = new MyTxtInfo(file.getAbsolutePath(), fileName, fileSize);
            myTxtInfo.setLetterHead(PinyinUtil.converterToFirstSpell(fileName));
            LogUtil.e(fileName);
        }
        return myTxtInfo;
    }

    protected void saveShelfToDb(MyTxtInfo myTxtInfo) {

        String SQL_EXIST = "SELECT NAME, ADDRESS FROM BookInfo WHERE ADDRESS = ?";
        Cursor cursor = db.rawQuery(SQL_EXIST, new String[]{myTxtInfo.getTxtPath()});
        if (!isCursorExist(cursor)) {//判断是否存在该书籍
            String SQL_INSERT_SHELF = "INSERT INTO BookInfo(" +
                    "NAME, ADDRESS, BOOK_SIZE) VALUES(?,?,?)";
            Object[] args = new Object[]{
                    myTxtInfo.getTxtName(), myTxtInfo.getTxtPath(), myTxtInfo.getTxtSize()
            };
            db.execSQL(SQL_INSERT_SHELF, args);
        } else {
            cursor.close();
        }
    }

    protected List<MyTxtInfo> getShelfFromDb() {
        List<MyTxtInfo> shelfList = new ArrayList<>();
        MyTxtInfo myTxtInfo;

        String SQL_QUERY = "SELECT NAME,ADDRESS,BOOK_SIZE FROM BookInfo";
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        if (isCursorExist(cursor)) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("NAME"));
                String filePath = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                String fileSize = cursor.getString(cursor.getColumnIndex("BOOK_SIZE"));
                myTxtInfo = new MyTxtInfo(filePath, fileName, fileSize);
                myTxtInfo.setLetterHead(PinyinUtil.converterToFirstSpell(fileName));
                shelfList.add(myTxtInfo);
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

    protected void saveSkipNum(String bookPath, int skipNum) {
        String SQL_UPDATE_SKIP_NUM = "UPDATE BookInfo " +
                "SET SKIP_NUM = ?"
                + " WHERE ADDRESS = ?";
        Object args[] = new Object[]{skipNum, bookPath};
        db.execSQL(SQL_UPDATE_SKIP_NUM, args);
    }

    protected byte[] openBook(String bookPath, int skipNum) {

        isChinese = new CheckChineseUtil();
        size = new FileSizeUtil();
        File file = new File(bookPath);

        long max = size.partition(file);

        long skipLength = (skipNum - 1) * (8 * 1024);
        RandomAccessFile bookFile = null;
        byte[] args = new byte[8 * 1024];


        try {

            bookFile = new RandomAccessFile(bookPath, "r");

            LogUtil.d("在第" + skipNum + "部分");
            bookFile.seek(skipLength);
            bookFile.read(args);

            if (isChinese.isChinese(args)) {

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return args;
    }

    protected void delBookFromDb(String bookPath) {
        String SQL_DELETE_BOOK = "DELETE FROM BookInfo WHERE ADDRESS = ?";
        Object args[] = new Object[]{bookPath};
        db.execSQL(SQL_DELETE_BOOK, args);
    }

    public void updateListView(String filterStr, List<MyTxtInfo> mList, MyComparatorAdapter adapter) {

        PinyinComparator comparator = new PinyinComparator();
        List<MyTxtInfo> updateData = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            updateData = mList;
        } else {
            updateData.clear();
            for (MyTxtInfo info : mList) {
                if (info.getTxtName().contains(filterStr)
                        || PinyinUtil.converterToFirstSpell(info.getTxtName()).startsWith(filterStr)) {
                    updateData.add(info);
                }
            }
        }
        Collections.sort(updateData, comparator);
        adapter.update(updateData);
    }

}


