package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.HealthGuidActivity;
import com.bsoft.mob.ienr.model.healthguid.HealthGuid;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidItem;

import java.util.List;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidListAdapter extends BaseExpandableListAdapter {
    Context mContext;
    List<HealthGuid> list;

    /**
     * <p>
     * Title: PlanListAdapter
     * </p>
     * <p>
     * Description: 构造函数，初始化上下文和计划列表参数
     * </p>
     *
     * @param mContext
     * @param list
     */
    public HealthGuidListAdapter(Context mContext, List<HealthGuid> list) {
        super();
        this.mContext = mContext;
        this.list = list;
    }


    /*
     * (非 Javadoc) <p>Title: getChild</p> <p>Description: 获取子项 </p>
     *
     * @param groupPosition 父项位置
     *
     * @param childPosition 子项位置
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getChild(int, int)
     */
    @Override
    public HealthGuidItem getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).HealthGuidItems.get(childPosition);
    }

    /*
     * (非 Javadoc) <p>Title: getChildId</p> <p>Description: 获取子项id </p>
     *
     * @param groupPosition
     *
     * @param childPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getChildId(int, int)
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return Long.parseLong(list.get(groupPosition).HealthGuidItems
                .get(childPosition).XH);
    }

    /*
     * (非 Javadoc) <p>Title: getChildView</p> <p>Description: 获取子项视图</p>
     *
     * @param groupPosition
     *
     * @param childPosition
     *
     * @param isLastChild
     *
     * @param convertView
     *
     * @param parent
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getChildView(int, int,
     * boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_two_secondary, parent,false);
            vHolder = new ChildHolder();
            vHolder.tv_itemname = (TextView) convertView
                    .findViewById(R.id.detail_name);
            vHolder.tv_itemtime = (TextView) convertView
                    .findViewById(R.id.detail_num);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ChildHolder) convertView.getTag();
        }
        vHolder.tv_itemname.setText(list.get(groupPosition).HealthGuidItems
                .get(childPosition).MS);
        vHolder.tv_itemtime.setText(list.get(groupPosition).HealthGuidItems
                .get(childPosition).XJSJ);
        /* vHolder.tv_itemnum.setVisibility(View.GONE);
        vHolder.iv_itemadd.setVisibility(View.GONE);
       vHolder.tv_itemnum.setText(list.get(groupPosition).HealthGuidItems
                .get(childPosition).SL + "份");*/
 /*       vHolder.iv_itemadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(mContext,
                        HealthGuidActivity.class));
                intent.putExtra("operType", "1");
                intent.putExtra("type", list.get(groupPosition).GLLX);
                intent.putExtra("xh", "");
                intent.putExtra("lxbh", list.get(groupPosition).HealthGuidItems.get(childPosition).LXBH);
                mContext.startActivity(intent);

            }
        });*/
        return convertView;
    }

    /*
     * (非 Javadoc) <p>Title: getChildrenCount</p> <p>Description:
     * 获取某个父项下子项数量</p>
     *
     * @param groupPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).HealthGuidItems.size();
    }

    /*
     * (非 Javadoc) <p>Title: getGroup</p> <p>Description: 获取父项</p>
     *
     * @param groupPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public HealthGuid getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    /*
     * (非 Javadoc) <p>Title: getGroupCount</p> <p>Description:获取父项数量 </p>
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {
        return list.size();
    }

    /*
     * (非 Javadoc) <p>Title: getGroupId</p> <p>Description: 获取父项的id</p>
     *
     * @param groupPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getGroupId(int groupPosition) {
        return Long.parseLong(list.get(groupPosition).XH);
    }

    /*
     * (非 Javadoc) <p>Title: getGroupView</p> <p>Description: </p>
     *
     * @param groupPosition
     *
     * @param isExpanded
     *
     * @param convertView
     *
     * @param parent
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
     * android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ParentHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_group_primary_icon, parent,false);
            vHolder = new ParentHolder();
            vHolder.tv_healthguidname = (TextView) convertView
                    .findViewById(R.id.id_tv);
            vHolder.tv_healthguidnum = (TextView) convertView
                    .findViewById(R.id.id_tv_more);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ParentHolder) convertView.getTag();
        }
        vHolder.tv_healthguidname.setText(list.get(groupPosition).MS);
        vHolder.tv_healthguidnum.setText(list.get(groupPosition).SL + "份");
        vHolder.tv_healthguidnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        HealthGuidActivity.class);
                intent.putExtra("operType", "1");
                intent.putExtra("type", list.get(groupPosition).GLLX);
                intent.putExtra("xh", "0");
                intent.putExtra("lxbh", list.get(groupPosition).XH);
                mContext.startActivity(intent);

            }
        });
        return convertView;
    }

    /*
     * (非 Javadoc) <p>Title: hasStableIds</p> <p>Description: </p>
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
     * (非 Javadoc) <p>Title: isChildSelectable</p> <p>Description: </p>
     *
     * @param groupPosition
     *
     * @param childPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ParentHolder {
        TextView tv_healthguidname;
        TextView tv_healthguidnum;
    }

    class ChildHolder {
        TextView tv_itemname;
        TextView tv_itemtime;
    }
}
