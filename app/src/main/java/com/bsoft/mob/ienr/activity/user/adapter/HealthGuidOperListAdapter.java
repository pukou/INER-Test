package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidOper;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidOperItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidOperListAdapter extends BaseAdapter {
    Context mContext;
    List<HealthGuidOper> list;

    LayoutInflater mInflater;

    /**
     *
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
    public HealthGuidOperListAdapter(Context mContext, List<HealthGuidOper> list) {
        super();
        this.mContext = mContext;
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
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
    public HealthGuidOper getItem(int position) {
        return list.get(position);
    }

    /*
     * (非 Javadoc) <p>Title: getGroupCount</p> <p>Description:获取父项数量 </p>
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getCount() {
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
    public long getItemId(int position) {
        return Long.parseLong(list.get(position).XH);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        ArrayList<HealthGuidOperItem> healthGuidOperItems = list.get(position).HealthGuidOperItems;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_health_guid_oper,  parent,false);

            vHolder = new ViewHolder();

            vHolder.tv_healthguidname = (TextView) convertView
                    .findViewById(R.id.healthguid_scrollist_item_txt);
            vHolder.scrollView_checkbox = (LinearLayout) convertView
                    .findViewById(R.id.id_ll_container);

            convertView.setTag(vHolder);

            scrollViewAddChildView(healthGuidOperItems, vHolder, false);
        } else {
            vHolder = (ViewHolder) convertView.getTag();

            scrollViewAddChildView(healthGuidOperItems, vHolder, true);
        }

        vHolder.tv_healthguidname.setText(list.get(position).MS + "：");

        return convertView;
    }

    private void scrollViewAddChildView(ArrayList<HealthGuidOperItem> healthGuidOperItems, ViewHolder vHolder, boolean isDelViewHolderChildView) {
        if(isDelViewHolderChildView) {
            vHolder.scrollView_checkbox.removeAllViews();
        }
        for(int i = 0; i < healthGuidOperItems.size(); i++) {
            View operView = mInflater.inflate(R.layout.layout_root_check,  null,false);
            final CheckBox checkBox = (CheckBox) operView.findViewById(R.id.id_cb);
            checkBox.setText(healthGuidOperItems.get(i).MS);
            checkBox.setId(Integer.parseInt(healthGuidOperItems.get(i).XH));
            // adapter 复用 view 时，响应OnCheckedChangeListener事件错乱
            checkBox.setOnCheckedChangeListener(null);
            if(healthGuidOperItems.get(i).ISCHECK.equals("1"))
            {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    for (int x = 0; x < list.size(); x++) {
                        for (int y = 0; y < list.get(x).HealthGuidOperItems.size(); y++) {
                            if(list.get(x).HealthGuidOperItems.get(y).XH.equals(Integer.toString(buttonView.getId()))
                                    && list.get(x).HealthGuidOperItems.get(y).MS.equals(buttonView.getText().toString())) {
                                list.get(x).HealthGuidOperItems.get(y).ISCHECK = (isChecked ? "1" : "0");
                                break;
                            }
                        }
                    }
                }
            });
            vHolder.scrollView_checkbox.addView(operView);
        }
    }

    class ViewHolder {
        TextView tv_healthguidname;
        LinearLayout scrollView_checkbox;
    }
}
