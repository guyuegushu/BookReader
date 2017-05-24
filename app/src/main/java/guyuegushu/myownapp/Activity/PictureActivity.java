package guyuegushu.myownapp.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import guyuegushu.myownapp.Adapter.PictureViewAdapter;
import guyuegushu.myownapp.R;

/**
 * Created by Administrator on 2017/3/16.
 */

public class PictureActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_view);
    }



}
