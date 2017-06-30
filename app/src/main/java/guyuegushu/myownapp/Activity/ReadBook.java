package guyuegushu.myownapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import guyuegushu.myownapp.Adapter.ReadViewPagerAdapter;
import guyuegushu.myownapp.Dao.DBManager;
import guyuegushu.myownapp.Interface.PageOverListener;
import guyuegushu.myownapp.OverrideView.TextViewForRead;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.StaticGlobal.MyActivityManager;

/**
 * Created by guyuegushu on 2016/10/25.
 * 上下滑动翻页
 * 对连续空格进行识别，发现后进行 “\n\n”，自动空两格，从第一个非空格处进行输出
 */
public class ReadBook extends Activity implements ViewPager.OnPageChangeListener, PageOverListener {

    private DBManager dbManager;
    private ViewPager mViewPager;
    private List<View> mPageList;
    private boolean isNeedContinue;

    TextViewForRead textViewForRead;
    TextViewForRead textViewForRead2;

    public void test2(String path) {
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String tmp2 = null;
            StringBuilder tmp = new StringBuilder();
//            while ((tmp2 = reader.readLine()) != null){
//                tmp.append(tmp2);
//                LogUtil.e(tmp2);
//            }
//            textViewForRead.setText(tmp.toString());
            char[] t = new char[1024];
//            tmp2 = reader.readLine();
            reader.read(t);
            textViewForRead.setText(new String(t));
//            LogUtil.e("readLine = " + tmp2.length() + '\n' + "read = " + t.length);
//            textViewForRead.setText(tmp2);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().addActivity(ReadBook.this);
        setContentView(R.layout.activity_display);
        dbManager = new DBManager(this);
        init();
//        test2(getBookPath());

//        openBook(getBookPath(), 0);

    }

    private void init() {
        mPageList = new ArrayList<>();
        mViewPager = (ViewPager) findViewById(R.id.reading_pager);

        View page1 = LayoutInflater.from(this).inflate(R.layout.read_viewpage1, null);
        View page2 = LayoutInflater.from(this).inflate(R.layout.read_viewpage2, null);
        View page3 = LayoutInflater.from(this).inflate(R.layout.read_viewpage3, null);

        textViewForRead = (TextViewForRead) page2.findViewById(R.id.view_page2);
        textViewForRead2 = (TextViewForRead) page1.findViewById(R.id.view_page1);


        mPageList.add(page1);
        mPageList.add(page2);
        mPageList.add(page3);

        PagerAdapter adapter = new ReadViewPagerAdapter(mPageList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(1, false);


    }

    private String getBookPath() {
        String bookPath = null;

        Intent tmp = getIntent();
        bookPath = tmp.getStringExtra("bookPath");

        return bookPath;
    }

    private void openBook(String bookPath, int skipCharNum) {

        isNeedContinue = true;

        File file = new File(bookPath);
        char[] charArrays = null;
        StringBuilder stringBuilder = null;
        try {
            charArrays = new char[1];
            stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (isNeedContinue) {
                for (int i = 1; i > skipCharNum; i++) {
                    int read = reader.read(charArrays);

                    if (read == -1) {

                        textViewForRead.setText(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        break;
                    }

                    stringBuilder.append(new String(charArrays));

                    if (charArrays[0] == '\n') {
                        textViewForRead.setText(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {

//        dbManager.saveSkipNum(getBookPath(), skipNum);
//        LogUtil.d("" + skipNum);
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (mPageList.size() > 1) {
//            if (position < 0) {
//                position = 1;
//                mViewPager.setCurrentItem(position, false);
//            } else if (position > 1) {
//                position = 0;
//                mViewPager.setCurrentItem(position, false);
//            }
            mViewPager.setCurrentItem(1, false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onePageOver(int charCount) {
        isNeedContinue = false;
    }
}
