package guyuegushu.myownapp.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by guyuegushu on 2016/10/11.
 * 创建数据库和表
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "BookLibrary.db";
    private final static String TABLE_NAME_SHELF = "BookInfo";
    private final static String TABLE_NAME_BOOK = "BookPage";
    private final static int VERSION = 1;//如何更改后续的版本号？
    private Context context;

    public DatabaseHelper(Context context) {
        this(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    public DatabaseHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        this.context = context;
    }

    public static String getTableNameShelf() {
        return TABLE_NAME_SHELF;
    }

    public static String getTableNameBook() {
        return TABLE_NAME_BOOK;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            upgradeDb(db, version);
        }

    }

    private void createShelfTable(SQLiteDatabase db) {
        String CREATE_TABLE_SHELF = "CREATE TABLE" + " " + TABLE_NAME_SHELF +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT NOT NULL, " +
                "ADDRESS TEXT UNIQUE," +
                "IS_FIRST_PAGE INTEGER DEFAULT 1," +
                "BOOK_SIZE INTEGER);";
        db.execSQL(CREATE_TABLE_SHELF);
    }

    private void createBookTable(SQLiteDatabase db) {
        String CREATE_TABLE_BOOK = "CREATE TABLE " + TABLE_NAME_BOOK +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ADDRESS TEXT, " +
                "PAGE INTEGER, " +
                "CURRENT INTEGER," +
                "FORWARD INTEGER," +
                "BACK INTEGER," +
                "CHAPTER TEXT);";
        db.execSQL(CREATE_TABLE_BOOK);
    }

    private void upgradeDb(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                createShelfTable(db);
                createBookTable(db);
                break;
            default:
                break;
        }
    }
}
