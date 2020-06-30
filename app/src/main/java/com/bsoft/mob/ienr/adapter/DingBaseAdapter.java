package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 单布局适配器的封装
 * Created by Ding.pengqiang
 *
 * on 2016/12/28.
 */
public abstract class DingBaseAdapter<T> extends BaseAdapter{
    public List<T> data;

    private LayoutInflater inflater;

    private int layoutResId;

    public DingBaseAdapter(Context context, List<T> data, int layoutResId){
        this.layoutResId = layoutResId;
        inflater = LayoutInflater.from(context);
        if (data != null) {
            this.data = data;
        }else{
            this.data = new ArrayList<>();
        }
    }

    public void addRes(List<T> data){
        if (data != null) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void updateRes(List<T> data){
        if (data != null) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(layoutResId,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        // 加载数据

        bindData(holder,getItem(position));

        return convertView;
    }

    protected abstract void bindData(ViewHolder holder, T item);

    public static class ViewHolder{

        private View convertView;
        private Map<Integer,View> mCacheViews;

        public ViewHolder(View convertView){
            this.convertView = convertView;
            mCacheViews = new HashMap<>();
        }
        public View getView(int viewId){
            View view = null;

            if (mCacheViews.containsKey(viewId)) {

                view = mCacheViews.get(viewId);
            }else{

                view = convertView.findViewById(viewId);

                mCacheViews.put(viewId,view);
            }
            return view;
        }

    }
}
