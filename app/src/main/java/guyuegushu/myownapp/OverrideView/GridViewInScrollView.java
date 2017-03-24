package guyuegushu.myownapp.OverrideView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2017/3/23.
 */

public class GridViewInScrollView extends GridView {

    public GridViewInScrollView(Context context){
        this(context, null);
    }

    public GridViewInScrollView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public GridViewInScrollView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
