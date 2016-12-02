package com.example.veb.bookreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VEB on 2016/10/11.
 */
public class BookShelf extends Activity implements ClickListener {

    private DBManager dbManager;
    private ListView shelfListView;
    private List<TxtFile> shelfList = new ArrayList<>();
    private static final String TAG = "BookShelf";
    private TxtAdapter shelfAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        dbManager = new DBManager(this);
        Button btn_local = (Button) findViewById(R.id.local);
        Button btn_exits = (Button) findViewById(R.id.exits);
        btn_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookShelf.this, BrowserActivity.class);
                startActivity(intent);
            }
        });
        btn_exits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setShelfListView();
    }

    private void setShelfListView() {

        shelfListView = (ListView) findViewById(R.id.shelf);
        this.registerForContextMenu(shelfListView);
        shelfList = dbManager.getShelfFromDb();
        shelfAdapter = new TxtAdapter(this, shelfList, this);
        shelfListView.setAdapter(shelfAdapter);
    }

    @Override
    public void onClicks(View item, View widget, int position, int which) {
        Log.d(TAG, "########################################     点击生效   ");
        String bookPath = shelfList.get(position).getTxtFilePath();
        Intent shelfIntent = new Intent(this, ReadBook.class);
        shelfIntent.putExtra("bookPath", bookPath);
        startActivityForResult(shelfIntent, RESULT_OK);
    }

    @Override
    public void onLongClicks(View item, View parents, int position, int ids) {
        Log.d(TAG, "########################################   长按生效   ");

        final String bookPath = shelfList.get(position).getTxtFilePath();

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
