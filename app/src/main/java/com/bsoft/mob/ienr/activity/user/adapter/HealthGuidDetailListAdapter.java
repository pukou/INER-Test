package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidDetail;

import java.util.List;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidDetailListAdapter extends BaseAdapter {
    Context mContext;
    List<HealthGuidDetail> list;

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
    public HealthGuidDetailListAdapter(Context mContext, List<HealthGuidDetail> list) {
        super();
        this.mContext = mContext;
        this.list = list;
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
    public HealthGuidDetail getItem(int position) {
        return list.get(position);
    }

    /*
     * (非 Javadoc) <p>Title: getGroupCount</p> <p>Description:获取项目数量 </p>
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
     * (非 Javadoc) <p>Title: getGroupId</p> <p>Description: 获取项目的id</p>
     *
     * @param groupPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_bar_check_text_start_no_clickable,parent,false);

            vHolder = new ViewHolder();

            /*vHolder.tv_healthguidname = (TextView) convertView
                    .findViewById(R.id.id_tv);*/
            vHolder.ch_ischecked = (CheckBox) convertView
                    .findViewById(R.id.id_cb);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        vHolder.ch_ischecked.setText(list.get(position).MS);
        String isCheck = list.get(position).ISCHECK;

        if(isCheck.equals("1"))
        {
            vHolder.ch_ischecked.setChecked(true);
        } else {
            vHolder.ch_ischecked.setChecked(false);
        }

        return convertView;
    }

    class ViewHolder {
       // TextView tv_healthguidname;
        CheckBox ch_ischecked;
    }
}
