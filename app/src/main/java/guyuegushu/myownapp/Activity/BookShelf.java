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
import guyuegushu.myownapp.Model.MyTxtInfo;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.StaticGlobal.MyActivityManager;
import guyuegushu.myownapp.StaticGlobal.MyComparatorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guyuegushu on 2016/10/11.
 *
 */
public class BookShelf extends AppCompatActivity implements ClickListener {

    private DBManager dbManager;
    private List<MyTxtInfo> shelfList;

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
        shelfList = dbManager.getShelfFromDb();
        MyComparatorAdapter shelfAdapter = new MyComparatorAdapter(this, shelfList, this);
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
        String bookPath = shelfList.get(position).getTxtPath();
        Intent shelfIntent = new Intent(this, ReadBook.class);
        shelfIntent.putExtra("bookPath", bookPath);
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
        final String bookPath = shelfList.get(position).getTxtPath();
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
