package guyuegushu.myownapp.StaticGlobal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import guyuegushu.myownapp.Interface.ClickListener;
import guyuegushu.myownapp.Model.MyTxtInfo;
import guyuegushu.myownapp.R;

import java.util.List;

/**
 * Created by guyuegushu on 2016/10/11.
 *
 */
public class MyComparatorAdapter extends BaseAdapter implements SectionIndexer {

    private List<MyTxtInfo> mList;
    private LayoutInflater mInflater;
    private ClickListener mListener;

    public MyComparatorAdapter(Context context, List<MyTxtInfo> data, ClickListener clickListener) {

        mListener = clickListener;
        mInflater = LayoutInflater.from(context);
        mList = data;
    }

    public void update(List<MyTxtInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        MyTxtInfo tmp = mList.get(position);
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new Holder();
            holder.textName = (TextView) convertView.findViewById(R.id.list_item_name);
            holder.textSize = (TextView) convertView.findViewById(R.id.list_item_size);
            holder.letterHead = (TextView) convertView.findViewById(R.id.letter_head);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        int section = getSectionForPosition(position);

        if (getPositionForSection(section) == position) {
            holder.letterHead.setVisibility(View.VISIBLE);
            holder.letterHead.setText(tmp.getLetterHead());
        } else {
            holder.letterHead.setVisibility(View.GONE);
        }

        holder.textName.setText(tmp.getTxtName());
        holder.textSize.setText(String.valueOf(tmp.getTxtSize()));

        final View view = convertView;
        final int p = position;
        final int ids = holder.textName.getId();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v = view;
                mListener.onClicks(v, parent, p, ids);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v = view;
                mListener.onLongClicks(v, parent, p, ids);
                return true;
            }
        });
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * @param sectionIndex 这个是索引值
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        //通过从0获取每一个的首字母ASCII，只取第一个不同的首字母显示
        for (int i = 0; i < getCount(); i++) {
            String str = mList.get(i).getLetterHead();
            char firstChar = str.toUpperCase().charAt(0);//ascii
            if (sectionIndex == firstChar) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mList.get(position).getLetterHead().charAt(0);
    }

    class Holder {
        private TextView letterHead;
        private TextView textName;
        private TextView textSize;
    }
}
