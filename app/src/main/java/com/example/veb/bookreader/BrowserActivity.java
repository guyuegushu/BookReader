package com.example.veb.bookreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by VEB on 2016/10/9.
 */
public class BrowserActivity extends AppCompatActivity implements ClickListener {

    private DBManager dbManager;
    private ListView mListView;
    private List<MyTxtInfo> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().addActivity(BrowserActivity.this);
        setContentView(R.layout.activity_browser_list);
        initToolBar();
        initOtherView();
//        registerForContextMenu(mListView);
    }

    private MyComparatorAdapter adapter;
    private EditTextForSearch search;

    private void initOtherView() {
        dbManager = new DBManager(this);
        mListView = (ListView) findViewById(R.id.list);

        String txtPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TxtBook";
        final File file = new File(txtPath);

        if (!file.exists()) {
            boolean isMkdirs = file.mkdirs();
            if (isMkdirs) {
                ToastUtil.showToast(this, R.string.mkDir, 0);
                mList = dbManager.getNameList(file);
            } else
                ToastUtil.showToast(this, R.string.mkDirEr, 0);
        } else {
            mList = dbManager.getNameList(file);
        }

        adapter = new MyComparatorAdapter(BrowserActivity.this, mList, BrowserActivity.this);

        PinyinComparator comparator = new PinyinComparator();
        final SideBar sideBar = (SideBar) findViewById(R.id.sidebar);
        final TextView dialog = (TextView) findViewById(R.id.letter_dialog);
        sideBar.setTextView(dialog);

        Collections.sort(mList, comparator);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            list.add(mList.get(i).getLetterHead());
        }

        sideBar.setExistLetter(list);//输入已经存在的字母列表

        mListView.setAdapter(adapter);

        search = (EditTextForSearch) findViewById(R.id.search_bar);

        if (getLastCustomNonConfigurationInstance() != null) {
            String lastWord = (String) getLastCustomNonConfigurationInstance();
            search.setText(lastWord);
            dbManager.updateListView(lastWord, mList, adapter);
        }
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dbManager.updateListView(s.toString(), mList, adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String str) {
                int position = adapter.getPositionForSection(str.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
    }

    public void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_browser_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(R.string.browser_small_title);
        setTitle(R.string.browser_title);
        toolbar.setLogo(R.drawable.browser_icon);


        //在设置toolbar之后，否则没有回退效果
        toolbar.setNavigationIcon(R.drawable.return_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle(R.string.shelf_menu_title);
//        menu.add(0, 0, 0, R.string.shelf_menu_item_1);
//        menu.add(0, 1, 0, R.string.shelf_menu_item_1);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a1:
                ToastUtil.showToast(this, R.string.waiting_add, 0);
                return true;
            case R.id.a2:
                ToastUtil.showToast(this, R.string.waiting_add, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClicks(View item, View widget, int position, int which) {
        MyTxtInfo myTxtInfo = mList.get(position);
        String bookPath = myTxtInfo.getTxtPath();
        dbManager.saveShelfToDb(myTxtInfo);
        Intent display = new Intent(this, ReadBook.class);
        display.putExtra("bookPath", bookPath);
        startActivity(display);
    }

    @Override
    public void onLongClicks(View item, View parents, int position, int ids) {

        final String bookPath = mList.get(position).getTxtPath();
        final File file = new File(bookPath);
        AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
        builder.setMessage(R.string.shelf_dialog_title);
        builder.setTitle(R.string.shelf_dialog_content);
        builder.setPositiveButton(R.string.shelf_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (file.exists() && file != null) {
                    file.delete();
                }
                initOtherView();
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

    /**
     * 保存意外发生前的情况
     *
     * @return
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return search.getText().toString();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                if (view != null && view instanceof EditTextForSearch) {
                    Rect r = new Rect();
                    view.getGlobalVisibleRect(r);
                    int rawX = (int) ev.getRawX();
                    int rawY = (int) ev.getRawY();

                    if (!r.contains(rawX, rawY)) {
                        view.clearFocus();
                        hideKeyboard();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
