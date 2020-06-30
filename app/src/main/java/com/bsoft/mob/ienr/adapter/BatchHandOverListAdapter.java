package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.handover.BatchHandOverRecord;
import com.bsoft.mob.ienr.model.handover.HandOverRecord;

import java.util.List;

/**
 * Description: 交接单
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-15
 * Time: 14:19
 * Version:
 */
public class BatchHandOverListAdapter extends BaseExpandableListAdapter {
    Context mContext;
    List<BatchHandOverRecord> list;

    public BatchHandOverListAdapter(Context mContext, List<BatchHandOverRecord> list) {
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
                    R.layout.item_list_text_one_secondary_icon,  parent,false);
            vHolder = new ChildHolder();
            vHolder.tv_itemname = (TextView) convertView
                    .findViewById(R.id.id_tv);
            vHolder.tv_itemtime = (TextView) convertView
                    .findViewById(R.id.id_tv_more);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ChildHolder) convertView.getTag();
        }
        String yslx = list.get(groupPosition).HandOverRecordList.get(childPosition).YSLX;
        if (yslx.equals("1")) {
            yslx = "转科交接";
        } else if (yslx.equals("2")) {
            yslx = "手术交接";
        } else if (yslx.equals("3")) {
            yslx = "外出检查";
        } else {
            yslx = "未知类型";
        }
        vHolder.tv_itemname.setText(yslx);
        vHolder.tv_itemtime.setText(list.get(groupPosition).HandOverRecordList
                .get(childPosition).TXSJ);
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).HandOverRecordList.size();
    }


    @Override
    public BatchHandOverRecord getGroup(int groupPosition) {
        return list.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return list.size();
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
        vHolder.tv_handovername.setText(list.get(groupPosition).BRXM);
        vHolder.tv_handovernum.setText("已核对" + list.get(groupPosition).CheckCount + "份  未核对" + list.get(groupPosition).NotCheckCount + "份");
        vHolder.tv_handovernum.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
    }
}
