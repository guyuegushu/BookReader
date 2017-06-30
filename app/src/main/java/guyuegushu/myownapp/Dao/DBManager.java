package guyuegushu.myownapp.Dao;


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

import guyuegushu.myownapp.Adapter.ComparatorAdapter;
import guyuegushu.myownapp.Model.BookInfoToRead;
import guyuegushu.myownapp.Model.BookInfoToShelf;
import guyuegushu.myownapp.StaticGlobal.GlobalApplication;
import guyuegushu.myownapp.StaticGlobal.PinyinComparator;
import guyuegushu.myownapp.Util.FileSizeUtil;
import guyuegushu.myownapp.Util.LogUtil;
import guyuegushu.myownapp.Util.PinyinUtil;
import guyuegushu.myownapp.Util.ToastUtil;

/**
 * Created by guyuegushu on 2016/10/11.
 * 遍历以找到.txt文件，并存入list中
 */

public class DBManager {

    private SQLiteDatabase db;
    private BookInfoToShelf.Builder builder;

    public DBManager(Context context) {
        //创建数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        builder = new BookInfoToShelf.Builder();
    }

    private boolean isCursorExist(Cursor cursor) {
        return cursor != null && cursor.moveToFirst();
    }

    public int getCurrentPage(String bookPath) {
        String SQL_CURRENT_PAGE = "SELECT CURRENT FROM " + DatabaseHelper.getTableNameBook() + " WHERE ADDRESS=? ";
        Cursor cursor = db.rawQuery(SQL_CURRENT_PAGE, new String[]{bookPath});
        if (isCursorExist(cursor)) {
            return cursor.getInt(cursor.getColumnIndex("CURRENT"));
        }
        return 0;
    }

    public int getForwardPage(String bookPath) {
        String SQL_FORWARD_PAGE = "SELECT FORWARD FROM " + DatabaseHelper.getTableNameBook() + " WHERE ADDRESS=? ";
        Cursor cursor = db.rawQuery(SQL_FORWARD_PAGE, new String[]{bookPath});
        if (isCursorExist(cursor)) {
            return cursor.getInt(cursor.getColumnIndex("FORWARD"));
        }
        return 0;
    }

    public int getBackPage(String bookPath) {
        String SQL_BACK_PAGE = "SELECT BACK FROM " + DatabaseHelper.getTableNameBook() + " WHERE ADDRESS=? ";
        Cursor cursor = db.rawQuery(SQL_BACK_PAGE, new String[]{bookPath});
        if (isCursorExist(cursor)) {
            return cursor.getInt(cursor.getColumnIndex("BACK"));
        }
        return 0;
    }

    public void setBookInfoToShelf(BookInfoToShelf bookInfoToShelf) {

        String SQL_EXIST = "SELECT NAME, ADDRESS FROM BookInfo WHERE ADDRESS = ?";
        Cursor cursor = db.rawQuery(SQL_EXIST, new String[]{bookInfoToShelf.getPath()});
        if (!isCursorExist(cursor)) {//判断是否存在该书籍
            String SQL_INSERT_SHELF = "INSERT INTO " + DatabaseHelper.getTableNameShelf() +
                    "(NAME, ADDRESS, BOOK_SIZE) VALUES(?,?,?)";
            Object[] args = new Object[]{
                    bookInfoToShelf.getName(), bookInfoToShelf.getPath(), bookInfoToShelf.getSize()
            };
            db.execSQL(SQL_INSERT_SHELF, args);
        } else {
            cursor.close();
        }
    }

    public List<BookInfoToShelf> getBookInfoToShelf() {
        List<BookInfoToShelf> shelfList = new ArrayList<>();
        BookInfoToShelf bookInfoToShelf;

        String SQL_QUERY = "SELECT NAME,ADDRESS,BOOK_SIZE FROM " + DatabaseHelper.getTableNameShelf();
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        if (isCursorExist(cursor)) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("NAME"));
                String filePath = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                String fileSize = String.valueOf(cursor.getInt(cursor.getColumnIndex("BOOK_SIZE")));
                bookInfoToShelf = builder.path(filePath)
                        .name(fileName)
                        .size(fileSize)
                        .letterHead(PinyinUtil.converterToFirstSpell(fileName))
                        .build();
                shelfList.add(bookInfoToShelf);
            } while (cursor.moveToNext());
        } else {
            ToastUtil.showToast("书架是空的，请选择书籍", 0);
        }

        try {
            if (cursor != null)
                cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return shelfList;
    }

    public boolean isFirstPage(String bookPath) {
        String SQL_IS_FIRST_PAGE = "SELECT IS_FIRST_PAGE FROM " + DatabaseHelper.getTableNameShelf() + " WHERE ADDRESS=? ";
        Cursor cursor = db.rawQuery(SQL_IS_FIRST_PAGE, new String[]{bookPath});
        if (isCursorExist(cursor)) {
            return cursor.getInt(cursor.getColumnIndex("IS_FIRST_PAGE")) == 1;
        }
        return false;
    }

    public void setIsFirstPage(String bookPath, int isFirstPage) {
        String SQL_INSERT_IS_FIRST_PAGE = "UPDATE " + DatabaseHelper.getTableNameShelf() +
                " SET IS_FIRST_PAGE = ? WHERE ADDRESS = ?";
        db.execSQL(SQL_INSERT_IS_FIRST_PAGE, new Object[]{bookPath, isFirstPage});
    }

    public void setBookInfoToRead(BookInfoToRead bookInfoToRead) {

        String SQL_INSERT_BOOK = "INSERT INTO " + DatabaseHelper.getTableNameBook() +
                " (ADDRESS, PAGE, CURRENT, FORWARD, BACK, CHAPTER) VALUES (?,?,?,?,?,?)";
        Object[] objects = new Object[]{bookInfoToRead.getAddress(), bookInfoToRead.getPage(),
                bookInfoToRead.getCurrent(), bookInfoToRead.getForward(), bookInfoToRead.getBack(), bookInfoToRead.getChapter()};
        db.execSQL(SQL_INSERT_BOOK, objects);

    }

    public void delBookFromDb(String bookPath) {
        String SQL_DELETE_FROM_SHELF = "DELETE FROM" + DatabaseHelper.getTableNameShelf() + " WHERE ADDRESS = ?";
        String SQL_DELETE_FROM_BOOK = "DELETE FROM" + DatabaseHelper.getTableNameBook() + " WHERE ADDRESS = ?";
        Object args[] = new Object[]{bookPath};
        db.execSQL(SQL_DELETE_FROM_SHELF, args);
        db.execSQL(SQL_DELETE_FROM_BOOK, args);


    }

}


