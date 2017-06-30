package guyuegushu.myownapp.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */

public class ReadViewPagerAdapter extends PagerAdapter {

    private List<View> mPageList;

    public ReadViewPagerAdapter(List<View> mPageList) {
        this.mPageList = mPageList;
    }

    /**
     * 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来(就是position)
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mPageList.get(position));
    }

    //实例化页卡
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mPageList.get(position));
        return mPageList.get(position);
    }

    @Override
    public int getCount() {
        return mPageList.size();
    }

    /**
     * 判断出去的view是否等于进来的view 如果为true直接复用
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
