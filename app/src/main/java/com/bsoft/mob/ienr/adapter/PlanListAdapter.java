package com.bsoft.mob.ienr.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NurseFocusActivity;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.model.nurseplan.Plan;
import com.bsoft.mob.ienr.model.nurseplan.SimpleRecord;

import java.util.List;

public class PlanListAdapter extends BaseExpandableListAdapter {
    Activity activity;
    List<Plan> list;

    public PlanListAdapter(Activity activity, List<Plan> list) {
        this.activity = activity;
        this.list = list;
    }


    @Override
    public SimpleRecord getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).SimpleRecord.get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return Long.parseLong(list.get(groupPosition).SimpleRecord
                .get(childPosition).XH);
    }

    @Override
    public View getChildView(final int groupPosition,
                             final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ChildHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.item_list_text_one_secondary_icon, parent,false);
            vHolder = new  ChildHolder();
            vHolder.tv_itemname = (TextView) convertView
                    .findViewById(R.id.id_tv);
            vHolder.id_tv_more = (TextView) convertView
                    .findViewById(R.id.id_tv_more);
            Drawable drawable = ContextCompatHelper.getDrawable(activity,R.drawable.ic_add_black_24dp);
            vHolder.id_tv_more.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ChildHolder) convertView.getTag();
        }
        vHolder.tv_itemname.setText(list.get(groupPosition).SimpleRecord
                .get(childPosition).MS);
        vHolder.id_tv_more.setText(list.get(groupPosition).SimpleRecord
                .get(childPosition).UMBER);
        vHolder.id_tv_more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,
                        NurseFocusActivity.class);
                intent.putExtra("WTXH",
                        list.get(groupPosition).SimpleRecord
                                .get(childPosition).XH);
                intent.putExtra("GLLX", list.get(groupPosition).GLLX);
                intent.putExtra("GLXH", list.get(groupPosition).XH);
                intent.putExtra("ISADD", true);
                activity.startActivityForResult(intent,
                        NurseFocusActivity.REQUEST_CODE);

            }
        });
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).SimpleRecord == null ? 0 : list
                .get(groupPosition).SimpleRecord.size();
    }


    @Override
    public Plan getGroup(int groupPosition) {
        return list.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return list.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return Long.parseLong(list.get(groupPosition).XH);
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
         ParentHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.item_list_group_primary, parent,false);
            vHolder = new ParentHolder();
            vHolder.tv_planname = (TextView) convertView
                    .findViewById(R.id.id_tv);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ParentHolder) convertView.getTag();
        }
        vHolder.tv_planname.setText(list.get(groupPosition).MS);
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
        TextView tv_planname;
    }

    class ChildHolder {
        TextView tv_itemname;
        TextView id_tv_more;
    }
}
