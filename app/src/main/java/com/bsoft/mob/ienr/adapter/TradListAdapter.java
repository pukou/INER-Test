package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.trad.TradBean;

import java.util.List;

public class TradListAdapter extends BaseExpandableListAdapter {
    Context mContext;
    List<TradBean> mList;


    public TradListAdapter(Context context, List<TradBean> list) {
        this.mContext = context;
        this.mList = list;

    }


    @Override
    public TradBean.TradChild getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).tradChildList.get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(final int groupPosition,
                             final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final ChildHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_group_trad_child, parent,false);
            vHolder = new ChildHolder();

            vHolder.id_tv = (TextView) convertView
                    .findViewById(R.id.id_tv);
            vHolder.id_tv2 = (TextView) convertView
                    .findViewById(R.id.id_tv_2);
            vHolder.id_tv3 = (TextView) convertView
                    .findViewById(R.id.id_tv_3);
            vHolder.id_tv4 = (TextView) convertView
                    .findViewById(R.id.id_tv_4);
            vHolder.id_tv5 = (TextView) convertView
                    .findViewById(R.id.id_tv_5);
            vHolder.id_tv6 = (TextView) convertView
                    .findViewById(R.id.id_tv_6);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ChildHolder) convertView.getTag();
        }

        vHolder.id_tv.setText(mList.get(groupPosition).tradChildList.get(childPosition).name);
        vHolder.id_tv2.setText(mList.get(groupPosition).tradChildList.get(childPosition).name2);
        vHolder.id_tv3.setText(mList.get(groupPosition).tradChildList.get(childPosition).name3);
        vHolder.id_tv4.setText(mList.get(groupPosition).tradChildList.get(childPosition).name4);
        vHolder.id_tv5.setText(mList.get(groupPosition).tradChildList.get(childPosition).name5);
        vHolder.id_tv6.setText(mList.get(groupPosition).tradChildList.get(childPosition).name6);

        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).tradChildList == null ? 0 : mList
                .get(groupPosition).tradChildList.size();
    }


    @Override
    public TradBean getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return mList.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ParentHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_group_primary_icon,parent,false);
            vHolder = new ParentHolder();
            vHolder.id_tv = (TextView) convertView
                    .findViewById(R.id.id_tv);
            vHolder.id_tv_more = (TextView) convertView
                    .findViewById(R.id.id_tv_more);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ParentHolder) convertView.getTag();
        }
        vHolder.id_tv.setText(mList.get(groupPosition).name);
        vHolder.id_tv_more.setTextColor("4".equals(mList.get(groupPosition).name2)?Color.RED: ContextCompat.getColor(mContext,R.color.colorAccent));
        vHolder.id_tv_more.setText(mList.get(groupPosition).name3);

        vHolder.id_tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.edit(view, groupPosition);
                }
            }
        });

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void refreshData(List<TradBean> tradBeanList) {
        mList.clear();
        mList.addAll(tradBeanList);
        notifyDataSetChanged();

    }


    class ParentHolder {
        TextView id_tv;
        TextView id_tv_more;
    }

    class ChildHolder {
        TextView id_tv;
        TextView id_tv2;
        TextView id_tv3;
        TextView id_tv4;
        TextView id_tv5;
        TextView id_tv6;

    }

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static abstract class ClickListener {
        public abstract void edit(View view, int groupPosition);

    }
}
