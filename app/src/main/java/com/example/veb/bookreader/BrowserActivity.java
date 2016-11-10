package com.example.veb.bookreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VEB on 2016/10/9.
 */
public class BrowserActivity extends Activity implements ClickListener {

    private final static String TAG = "BrowserActivity";
    private DBManager dbManager;
    private ListView mListView;
    private List<TxtFile> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_list);
        dbManager = new DBManager(this);
        mListView = (ListView) findViewById(R.id.list);
        String txtPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TxtBook";
        File file = new File(txtPath);
        mList = dbManager.getNameList(file);
        TxtAdapter adapter = new TxtAdapter(BrowserActivity.this, mList, BrowserActivity.this);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClicks(View item, View widget, int position, int which) {
        TxtFile txtFile = mList.get(position);
        String bookPath = txtFile.getTxtFilePath();
        Log.d(TAG, "####################### " + bookPath);
        dbManager.saveShelfToDb(txtFile);
        Intent display = new Intent(this, ReadBook.class);
        display.putExtra("bookPath", bookPath);
        startActivity(display);
    }

    @Override
    public void onLongClicks(View item, View parents, int position, int ids) {
        Log.e(TAG, "########################################     长按生效   ");
        parents.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                Log.e(TAG, "########################################   菜单生效   ");
                menu.setHeaderTitle(R.string.shelf_menu_title);
                menu.add(0, 0, 0, R.string.shelf_menu_item_1);
                menu.add(0, 1, 0, R.string.shelf_menu_item_1);
            }
        });

    }
}
