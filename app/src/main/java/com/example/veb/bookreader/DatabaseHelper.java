package com.example.veb.bookreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by VEB on 2016/10/11.
 * 创建数据库和表
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "BookLibrary.db";
    private final static String TABLE_NAME_SHELF = "BookInfo";
    private final static int VERSION = 1;//如何更改后续的版本号？
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    public DatabaseHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ToastUtil.showToast(context, R.string.create_db, 0);
        onUpgrade(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion + 1; version <= newVersion; version++){
            upgradeDb(db,version);
        }

    }

    private void createShelfTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE" + " " + TABLE_NAME_SHELF +
                                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "NAME TEXT NOT NULL, " +
                                "ADDRESS TEXT UNIQUE," +
                                "CACHE_HEAD TEXT, " +
                                "CACHE_BODY TEXT, " +
                                "SKIP_NUM INTEGER DEFAULT 1," +
                                "BOOK_SIZE TEXT DEFAULT 0);";
        db.execSQL(CREATE_TABLE);
    }

    private void upgradeDb(SQLiteDatabase db, int version){
        switch (version){
            case 1 :
                createShelfTable(db);
                break;
            default:
                break;
        }
    }
}
