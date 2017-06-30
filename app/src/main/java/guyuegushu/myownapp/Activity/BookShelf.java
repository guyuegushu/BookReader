package guyuegushu.myownapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import guyuegushu.myownapp.Dao.DBManager;
import guyuegushu.myownapp.Interface.ClickListener;
import guyuegushu.myownapp.Model.BookInfoToShelf;
import guyuegushu.myownapp.OpenGLESDemo.OpenGLESDemo;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.StaticGlobal.MyActivityManager;
import guyuegushu.myownapp.Adapter.ComparatorAdapter;
import guyuegushu.myownapp.Util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guyuegushu on 2016/10/11.
 * 书架功能，显示曾经看过的书籍，可以长按删除，但是暂时不支持直接删除源文件
 */
public class BookShelf extends AppCompatActivity implements ClickListener {

    private DBManager dbManager;
    private List<BookInfoToShelf> shelfList;

    /*
        char是2个字节
        中文是GB2312是2个字节，是utf-8是3个字节
        中文符号是2个字节
        英文符号是1个字节
        全角符号是2个字节
        全角英文是2个字节

        经过简陋的实验，文本文件大小并不能够影响他的读取，一个185M的文件，从sd卡读到内存并转换成string只要1000ms
     */

    private void test(String path) {
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[4000];
            long length = 0;
            StringBuilder stringBuilder = new StringBuilder();
            long sT = System.currentTimeMillis();
            while (fis.read(bytes) != -1) {
                stringBuilder.append(String.valueOf(bytes));
            }
            long eT = System.currentTimeMillis();
            LogUtil.d("共用时：" + (eT - sT) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*              会读到空行
                一次读到回车为止
* */
    private void test2(String path){
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            long sT = System.currentTimeMillis();
            String tmp = reader.readLine();
            String tmp3 = reader.readLine();
            String tmp2 = reader.readLine();

            long eT = System.currentTimeMillis();
            LogUtil.d("共用时：" + (eT - sT) );
            LogUtil.d("output：" +  tmp2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将当前活动添加至管理器，以便管理
        MyActivityManager.getInstance().addActivity(BookShelf.this);
        setContentView(R.layout.activity_shelf);
        initToolbar();
        setShelfListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shelf, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_to_browser:
                Intent intent = new Intent(BookShelf.this, BrowserActivity.class);
                startActivity(intent);
                return true;
            case R.id.btn_to_openGLES:
                Intent intent2 = new Intent(BookShelf.this, OpenGLESDemo.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 初始化toolbar(副标题是会被bar中的控件遮挡)
     * 初始化部分全局变量
     */
    private void initToolbar() {
        dbManager = new DBManager(BookShelf.this);
        shelfList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_shelf_toolbar);
//        toolbar.setSubtitle(R.string.shelf_small_title);//不起作用，估计是被textview覆盖了
//        toolbar.setSubtitleTextColor(Color.GREEN);
        setSupportActionBar(toolbar);
        setTitle("");
        toolbar.setLogo(R.drawable.shelf_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyActivityManager.getInstance().exit(BookShelf.this);
            }
        });
    }

    /**
     * 从数据库中提取数据进行书架的初始化
     */
    private void setShelfListView() {

        ListView shelfListView = (ListView) findViewById(R.id.shelf);
        shelfList = dbManager.getBookInfoToShelf();
        ComparatorAdapter shelfAdapter = new ComparatorAdapter(this, shelfList, this);
        shelfListView.setAdapter(shelfAdapter);
    }

    /**
     * 打开阅读界面
     *
     * @param item
     * @param widget
     * @param position
     * @param which
     */
    @Override
    public void onClicks(View item, View widget, int position, int which) {
        String bookPath = shelfList.get(position).getPath();
        Intent shelfIntent = new Intent(this, ReadBook.class);
        shelfIntent.putExtra("bookPath", bookPath);
        test2(bookPath);
        startActivityForResult(shelfIntent, RESULT_OK);
    }

    /**
     * 长按操作
     *
     * @param item
     * @param parents
     * @param position
     * @param ids
     */
    @Override
    public void onLongClicks(View item, View parents, int position, int ids) {
        final String bookPath = shelfList.get(position).getPath();
        AlertDialog.Builder builder = new AlertDialog.Builder(BookShelf.this);
        builder.setMessage(R.string.shelf_dialog_title);
        builder.setTitle(R.string.shelf_dialog_content);
        builder.setPositiveButton(R.string.shelf_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dbManager.delBookFromDb(bookPath);
                setShelfListView();
            }
        });
        builder.setNegativeButton(R.string.shelf_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setShelfListView();
    }


}
