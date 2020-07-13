package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.visit.VisitPerson;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class VisitAdapter extends BaseExpandableListAdapter {

    String[] groups;

    VisitPerson[][] childs;

    Context mContext;

    /**
     * key 为group position , value 为child
     */
    // SparseArrayCompat<ArrayList<InspectResult>> childs;
    public VisitAdapter(String[] groups, VisitPerson[][] childs,
                        Context mContext) {

        this.childs = childs;
        this.groups = groups;
        this.mContext = mContext;
    }

    private boolean removeItems(int groupPosition, int childPosition) {

        if (groupPosition < 0 || childPosition < 0
                || groupPosition >= getGroupCount()
                || childPosition >= getChildrenCount(groupPosition)) {
            return false;
        }
        VisitPerson[] visitedItems = childs[groupPosition];
        visitedItems = ArrayUtils.remove(visitedItems, childPosition);
        childs[groupPosition] = visitedItems;
        return true;
    }

    public void changeItem(int groupPosition, String zyh,
                           int targetGroupPosition, ArrayList<VisitPerson> list) {

        if (groupPosition < 0 || groupPosition >= getGroupCount()
                || targetGroupPosition < 0
                || targetGroupPosition >= getGroupCount()) {
            return;
        }
        VisitPerson[] visitedItems = childs[groupPosition];

        if (visitedItems != null) {
            for (int i = 0; i < visitedItems.length; i++) {
                VisitPerson person = visitedItems[i];
                if (person.ZYH.equals(zyh)) {
                    removeItems(groupPosition, i);
                    break;
                }
            }
        }

        addList(targetGroupPosition, list);
        notifyDataSetChanged();
    }

    public int findPosition(int groupPosition, String zyh) {

        if (groupPosition < 0 || EmptyTool.isBlank(zyh)
                || groupPosition >= getGroupCount()) {
            return -1;
        }

        VisitPerson[] visitedItems = childs[groupPosition];
        if (visitedItems == null) {
            return -1;
        }

        int position = 0;
        for (VisitPerson person : visitedItems) {

            if (zyh.equals(person.ZYH)) {
                return position;
            }
            position++;
        }

        return -1;
    }

    private boolean addArray(int groupPosition, VisitPerson[] items) {

        if (groupPosition < 0 || items == null
                || groupPosition >= getGroupCount()) {
            return false;
        }
        VisitPerson[] visitedItems = childs[groupPosition];
        if (visitedItems == null) {
            childs[groupPosition] = items;
        } else {
            for (int i = 0; i < visitedItems.length; i++) {
                VisitPerson person = visitedItems[i];
                if (person.ZYH.equals(items[0].ZYH)) {
                    removeItems(groupPosition, i);
                    break;
                }
            }
            VisitPerson[] visitedItems2 = childs[groupPosition];
            childs[groupPosition] = ArrayUtils.addAll(visitedItems2, items);
        }
        return true;
    }

    private boolean addList(int groupPosition, ArrayList<VisitPerson> list) {

        if (list == null) {
            return false;
        }
        VisitPerson[] items = list.toArray(new VisitPerson[list.size()]);
        return addArray(groupPosition, items);
    }

    @Override
    public int getGroupCount() {
        return groups != null ? groups.length : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs[groupPosition] != null ? childs[groupPosition].length : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return childs[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String groupText = getGroup(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_list_group_daily_tour, parent,false);
        }

        TextView groupTextView = (TextView) convertView
                .findViewById(R.id.id_tv);
        groupTextView.setText(groupText);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_two_vert_secondary_selected, parent,false);
            vHolder = new ViewHolder();
            vHolder.tv_brxm = (TextView) convertView
                    .findViewById(R.id.id_tv_one);
            vHolder.tv_brch = (TextView) convertView
                    .findViewById(R.id.id_tv_two);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        VisitPerson result = childs[groupPosition][childPosition];
        vHolder.tv_brxm.setText(result.BRXM);
        StringBuilder sb = new StringBuilder("暂无床号");
        if (result.BRCH != null) {
            sb = new StringBuilder(result.XSCH).append("床");
        }
//		String time = getFormateTime(result.XSSJ);
//		if (!EmptyTool.isBlank(time)) {
//			sb.append("-").append(time).append("巡");
//		}
        vHolder.tv_brch.setText(sb.toString());
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        public TextView tv_brxm;
        public TextView tv_brch;
    }

}
