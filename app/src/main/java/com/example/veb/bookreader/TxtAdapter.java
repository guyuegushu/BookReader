package com.example.veb.bookreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by VEB on 2016/10/11.
 */
public class TxtAdapter extends BaseAdapter {

    private List<TxtFile> mList;
    private LayoutInflater mInflater;
    private ClickListener mListener;

    public TxtAdapter(Context context, List<TxtFile> data, ClickListener clickListener) {

        mListener = clickListener;
        mInflater = LayoutInflater.from(context);
        mList = data;
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
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.broswer_list_item, null);
            holder = new Holder();
            holder.textName = (TextView) convertView.findViewById(R.id.list_item_name);
            holder.textSize = (TextView) convertView.findViewById(R.id.list_item_size);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.textName.setText(mList.get(position).getTxtFileName());
        holder.textSize.setText(String.valueOf(mList.get(position).getTxtFileSize()));

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

    class Holder {
        private TextView textName;
        private TextView textSize;
    }
}
