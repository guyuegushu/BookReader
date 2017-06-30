package guyuegushu.myownapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import guyuegushu.myownapp.Adapter.ComparatorAdapter;
import guyuegushu.myownapp.Dao.DBManager;
import guyuegushu.myownapp.Interface.ClickListener;
import guyuegushu.myownapp.Model.BookInfoToShelf;
import guyuegushu.myownapp.OverrideView.EditTextForSearch;
import guyuegushu.myownapp.OverrideView.SideBar;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.StaticGlobal.MyActivityManager;
import guyuegushu.myownapp.StaticGlobal.PinyinComparator;
import guyuegushu.myownapp.Util.PinyinUtil;
import guyuegushu.myownapp.Util.ToastUtil;

/**
 * Created by guyuegushu on 2016/10/9.
 * 现在是单独的txt浏览器
 */
public class BrowserActivity extends AppCompatActivity implements ClickListener {

    private DBManager dbManager;
    private List<BookInfoToShelf> mList;
    private ListView mListView;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1001:

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().addActivity(BrowserActivity.this);
        setContentView(R.layout.activity_browser_list);

        dbManager = new DBManager(this);
        mListView = (ListView) findViewById(R.id.list);
        mList = initBrowser();

        initToolBar();
        initAllView();
    }

    private ComparatorAdapter adapter;
    private EditTextForSearch search;

    private List<BookInfoToShelf> initBrowser() {
        String finalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        return childFileFromPath(finalRoot);
    }

    private void initAllView() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            initListView(mList);
            initSideBar(mList);
            initSearchView(mList);
        } else {
            ToastUtil.showToast(R.string.sdcard_not_exist, 0);
        }

    }

    private void initListView(List<BookInfoToShelf> defaultList) {

        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(defaultList, comparator);
        adapter = new ComparatorAdapter(BrowserActivity.this, defaultList, BrowserActivity.this);
        mListView.setAdapter(adapter);
    }

    private void initSideBar(List<BookInfoToShelf> defaultList) {

        adapter = new ComparatorAdapter(BrowserActivity.this, defaultList, BrowserActivity.this);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < defaultList.size(); i++) {
            list.add(defaultList.get(i).getLetterHead());
        }
        final SideBar sideBar = (SideBar) findViewById(R.id.sidebar);
        final TextView dialog = (TextView) findViewById(R.id.letter_dialog);
        sideBar.setTextView(dialog);
        sideBar.setExistLetter(list);//输入已经存在的字母列表
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

    private void initSearchView(final List<BookInfoToShelf> defaultList) {

        adapter = new ComparatorAdapter(BrowserActivity.this, defaultList, BrowserActivity.this);

        search = (EditTextForSearch) findViewById(R.id.search_bar);
        if (getLastCustomNonConfigurationInstance() != null) {
            String lastWord = (String) getLastCustomNonConfigurationInstance();
            search.setText(lastWord);
            updateListView(lastWord, defaultList, adapter);
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateListView(s.toString(), defaultList, adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public void updateListView(String filterStr, List<BookInfoToShelf> mList, ComparatorAdapter adapter) {

        PinyinComparator comparator = new PinyinComparator();
        List<BookInfoToShelf> updateData = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            updateData = mList;
        } else {
            updateData.clear();
            for (BookInfoToShelf info : mList) {
                if (info.getName().contains(filterStr)
                        || PinyinUtil.converterToFirstSpell(info.getName()).startsWith(filterStr)) {
                    updateData.add(info);
                }
            }
        }
        Collections.sort(updateData, comparator);
        adapter.update(updateData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a1:
                ToastUtil.showToast(R.string.waiting_add, 0);
                return true;
            case R.id.a2:
                ToastUtil.showToast(R.string.waiting_add, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClicks(View item, View widget, int position, int which) {
        BookInfoToShelf info = mList.get(position);

        if (info.getName().endsWith(".txt")) {
            String bookPath = info.getPath();
            dbManager.setBookInfoToShelf(info);
            Intent display = new Intent(this, ReadBook.class);
            display.putExtra("bookPath", bookPath);
            startActivity(display);
        } else {
            mList = childFileFromPath(info.getPath());
            initAllView();
        }
    }

    @Override
    public void onLongClicks(View item, View parents, int position, int ids) {

        final String bookPath = mList.get(position).getPath();
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
                initAllView();
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

    public String nameFromPath(String absolutePath) {
        String name = null;
        int desPosition = absolutePath.lastIndexOf('/');
        if (desPosition != -1) {
            name = absolutePath.substring(desPosition + 1, absolutePath.length());
        }
        return name;
    }

    public List<BookInfoToShelf> childFileFromPath(String absolutePath) {
        List<BookInfoToShelf> childList = new ArrayList<>();
        File root = new File(absolutePath);
        if (!root.exists()) {
            childList = null;
        } else {
            File[] childFiles = root.listFiles();
            if (childFiles == null) {
                childList = null;
            } else {
                BookInfoToShelf info = null;
                BookInfoToShelf.Builder builder = null;
                for (File child : childFiles) {
                    String path = child.getAbsolutePath();
                    builder = new BookInfoToShelf.Builder();
                    info = builder
                            .name(nameFromPath(path))
                            .path(path)
                            .letterHead(PinyinUtil.converterToFirstSpell(nameFromPath(path)))
                            .build();
//                    info.setLetterHead(PinyinUtil.converterToFirstSpell(nameFromPath(path)));
                    childList.add(info);
                }
            }
        }
        return childList;
    }

    public List<BookInfoToShelf> parentFileFromPath(String absolutePath) {
        int desPosition = absolutePath.lastIndexOf('/');
        if (desPosition != -1) {
            absolutePath = absolutePath.substring(0, desPosition + 1);
            return childFileFromPath(absolutePath);
        }
        return null;
    }

}
