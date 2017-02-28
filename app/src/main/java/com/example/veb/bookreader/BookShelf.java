package com.example.veb.bookreader;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VEB on 2016/10/11.
 */
public class BookShelf extends AppCompatActivity implements ClickListener {

    private DBManager dbManager;
    private ListView shelfListView;
    private List<MyTxtInfo> shelfList = new ArrayList<>();
    private MyComparatorAdapter shelfAdapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().addActivity(BookShelf.this);
        setContentView(R.layout.activity_shelf);
        dbManager = new DBManager(this);
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

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.activity_shelf_toolbar);
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

    private void setShelfListView() {

        shelfListView = (ListView) findViewById(R.id.shelf);
        this.registerForContextMenu(shelfListView);
        shelfList = dbManager.getShelfFromDb();
        shelfAdapter = new MyComparatorAdapter(this, shelfList, this);
        shelfListView.setAdapter(shelfAdapter);
    }

    @Override
    public void onClicks(View item, View widget, int position, int which) {
        String bookPath = shelfList.get(position).getTxtPath();
        Intent shelfIntent = new Intent(this, ReadBook.class);
        shelfIntent.putExtra("bookPath", bookPath);
        startActivityForResult(shelfIntent, RESULT_OK);
    }

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
