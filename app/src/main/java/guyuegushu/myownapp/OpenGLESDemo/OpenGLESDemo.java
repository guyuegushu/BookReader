package guyuegushu.myownapp.OpenGLESDemo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;



/**
 * Created by Administrator on 2017/3/21.
 */

public class OpenGLESDemo extends Activity {

    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLView(this);
        setContentView(mGLView);
    }


}
