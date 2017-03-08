package guyuegushu.myownapp.Dao;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import guyuegushu.myownapp.Model.MyItemInfo;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.Adapter.ComparatorAdapter;
import guyuegushu.myownapp.StaticGlobal.PinyinComparator;
import guyuegushu.myownapp.Util.CheckChineseUtil;
import guyuegushu.myownapp.Util.FileSizeUtil;
import guyuegushu.myownapp.Util.LogUtil;
import guyuegushu.myownapp.Util.PinyinUtil;
import guyuegushu.myownapp.Util.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by guyuegushu on 2016/10/11.
 * 遍历以找到.txt文件，并存入list中
 */

public class DBManager {

    private Context context;
    private SQLiteDatabase db;
    private FileSizeUtil size;
    private CheckChineseUtil isChinese;
    private MyItemInfo.Builder builder;

    public DBManager(Context context) {
        this.context = context;
        //创建数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        builder = new MyItemInfo.Builder();
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

    public boolean isFirstOpen(String bookPath) {
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

    public boolean isFirstPage(int skipNum) {
        if (skipNum == 1) {
            return true;
        } else {
            return false;
        }
    }

    public int getSkipNum(String bookPath) {
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

    public List<MyItemInfo> getNameList(File bookFiles) {

        List<MyItemInfo> browserList = new ArrayList<>();

        File[] files = bookFiles.listFiles();

        MyItemInfo myItemInfo = null;
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getNameList(f);//只需要txt
                } else {
                    myItemInfo = getTxtFileName(f);
                    if (myItemInfo != null) {
                        browserList.add(myItemInfo);
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

    private MyItemInfo getTxtFileName(File file) {
        MyItemInfo myItemInfo = null;
        String fileName = file.getName();
        size = new FileSizeUtil();
        if (fileName.endsWith(".txt")) {
            String fileSize = size.getFileOrDirSize(file);
            myItemInfo = builder.path(file.getAbsolutePath())
                    .name(fileName)
                    .size(fileSize)
                    .build();
            myItemInfo.setLetterHead(PinyinUtil.converterToFirstSpell(fileName));
            LogUtil.e(fileName);
        }
        return myItemInfo;
    }

    public void saveShelfToDb(MyItemInfo myItemInfo) {

        String SQL_EXIST = "SELECT NAME, ADDRESS FROM BookInfo WHERE ADDRESS = ?";
        Cursor cursor = db.rawQuery(SQL_EXIST, new String[]{myItemInfo.getPath()});
        if (!isCursorExist(cursor)) {//判断是否存在该书籍
            String SQL_INSERT_SHELF = "INSERT INTO BookInfo(" +
                    "NAME, ADDRESS, BOOK_SIZE) VALUES(?,?,?)";
            Object[] args = new Object[]{
                    myItemInfo.getName(), myItemInfo.getPath(), myItemInfo.getSize()
            };
            db.execSQL(SQL_INSERT_SHELF, args);
        } else {
            cursor.close();
        }
    }

    public List<MyItemInfo> getShelfFromDb() {
        List<MyItemInfo> shelfList = new ArrayList<>();
        MyItemInfo myItemInfo;

        String SQL_QUERY = "SELECT NAME,ADDRESS,BOOK_SIZE FROM BookInfo";
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        if (isCursorExist(cursor)) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("NAME"));
                String filePath = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                String fileSize = cursor.getString(cursor.getColumnIndex("BOOK_SIZE"));
                myItemInfo = builder.path(filePath)
                        .name(fileName)
                        .size(fileSize)
                        .build();
                myItemInfo.setLetterHead(PinyinUtil.converterToFirstSpell(fileName));
                shelfList.add(myItemInfo);
            } while (cursor.moveToNext());
        } else {
//            ToastUtil.showToast(GlobalApplication.getContext(), "书架是空的，请选择书籍", 0);
        }

        try {
            if (cursor != null)
                cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return shelfList;
    }

    public void saveSkipNum(String bookPath, int skipNum) {
        String SQL_UPDATE_SKIP_NUM = "UPDATE BookInfo " +
                "SET SKIP_NUM = ?"
                + " WHERE ADDRESS = ?";
        Object args[] = new Object[]{skipNum, bookPath};
        db.execSQL(SQL_UPDATE_SKIP_NUM, args);
    }

    public byte[] openBook(String bookPath, int skipNum) {

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

    public void delBookFromDb(String bookPath) {
        String SQL_DELETE_BOOK = "DELETE FROM BookInfo WHERE ADDRESS = ?";
        Object args[] = new Object[]{bookPath};
        db.execSQL(SQL_DELETE_BOOK, args);
    }

    public void updateListView(String filterStr, List<MyItemInfo> mList, ComparatorAdapter adapter) {

        PinyinComparator comparator = new PinyinComparator();
        List<MyItemInfo> updateData = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            updateData = mList;
        } else {
            updateData.clear();
            for (MyItemInfo info : mList) {
                if (info.getName().contains(filterStr)
                        || PinyinUtil.converterToFirstSpell(info.getName()).startsWith(filterStr)) {
                    updateData.add(info);
                }
            }
        }
        Collections.sort(updateData, comparator);
        adapter.update(updateData);
    }

}


