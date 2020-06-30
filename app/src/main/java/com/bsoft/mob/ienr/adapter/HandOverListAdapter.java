package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.HandOverActivity;
import com.bsoft.mob.ienr.model.handover.HandOverForm;
import com.bsoft.mob.ienr.model.handover.HandOverRecord;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

/**
 * Description: 交接单
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-15
 * Time: 14:19
 * Version:
 */
public class HandOverListAdapter extends BaseExpandableListAdapter {
    Context mContext;
    List<HandOverForm> list;

    public HandOverListAdapter(Context mContext, List<HandOverForm> list) {
        super();
        this.mContext = mContext;
        this.list = list;
    }


    @Override
    public HandOverRecord getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).HandOverRecordList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return Long.parseLong(list.get(groupPosition).HandOverRecordList
                .get(childPosition).JLXH);
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_three_secondary,  parent,false);
            vHolder = new ChildHolder();
            vHolder.tv_itemname = (TextView) convertView
                    .findViewById(R.id.id_tv_one);
            vHolder.tv_itemtime = (TextView) convertView
                    .findViewById(R.id.id_tv_two);
            vHolder.tv_itemnum = (TextView) convertView
                    .findViewById(R.id.id_tv_three);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ChildHolder) convertView.getTag();
        }
        String jjnr = list.get(groupPosition).HandOverRecordList.get(childPosition).JJNR;
       if (!EmptyTool.isBlank(jjnr)){
           jjnr = jjnr.replace(" 00:00:00", "");
       }
        if (!EmptyTool.isBlank(jjnr) && jjnr.length() > 50) {
            jjnr = jjnr.substring(0, 50) + "...";
        }
        vHolder.tv_itemname.setText(jjnr);
        String jjzt = list.get(groupPosition).HandOverRecordList.get(childPosition).ZTBZ;
        if (jjzt.equals("0")) {
            jjzt = "保存";
        } else if (jjzt.equals("1")) {
            jjzt = "发送";
        } else if (jjzt.equals("2")) {
            jjzt = "核对";
        } else {
            jjzt = "未知";
        }
        vHolder.tv_itemnum.setText(jjzt);
        vHolder.tv_itemtime.setText(list.get(groupPosition).HandOverRecordList
                .get(childPosition).TXSJ);
        vHolder.tv_itemnum.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).HandOverRecordList.size();
    }


    @Override
    public HandOverForm getGroup(int groupPosition) {
        return list.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return list.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return Long.parseLong(list.get(groupPosition).YSXH);
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ParentHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_group_primary_icon,  parent,false);
            vHolder = new ParentHolder();
            vHolder.tv_handovername = (TextView) convertView
                    .findViewById(R.id.id_tv);
            vHolder.tv_handovernum = (TextView) convertView
                    .findViewById(R.id.id_tv_more);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ParentHolder) convertView.getTag();
        }
        vHolder.tv_handovername.setText(list.get(groupPosition).YSMC);
        vHolder.tv_handovernum.setVisibility(View.VISIBLE);
        vHolder.tv_handovernum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        HandOverActivity.class);
                intent.putExtra("jlxh", "0");
                intent.putExtra("ysxh", list.get(groupPosition).YSXH);
                intent.putExtra("yslx", list.get(groupPosition).YSLX);
                mContext.startActivity(intent);

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

    class ParentHolder {
        TextView tv_handovername;
        TextView tv_handovernum;
    }

    class ChildHolder {
        TextView tv_itemname;
        TextView tv_itemtime;
        TextView tv_itemnum;
    }
}
