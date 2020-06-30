package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.outcontrol.OutControl;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.Date;

public class OutAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<OutControl> list;

    String[] arrays = null;

    public OutAdapter(Context mContext, ArrayList<OutControl> list) {

        this.mContext = mContext;
        this.list = list;
        arrays = mContext.getResources().getStringArray(R.array.out_with_array);

    }

    @Override
    public int getCount() {

        return list != null ? list.size() : 0;
    }

    @Override
    public OutControl getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHoler holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.out_history_list_item, parent, false);
            holder = new ViewHoler();
            holder.mOutTxt = (TextView) convertView
                    .findViewById(R.id.out_history_outtime_txt);
            holder.mBackTxt = (TextView) convertView
                    .findViewById(R.id.out_history_backtime_txt);
            holder.mWCYYTxt = (TextView) convertView
                    .findViewById(R.id.out_history_wcyy_txt);
            holder.mWithTxt = (TextView) convertView
                    .findViewById(R.id.out_history_with_txt);
            holder.mPlanBackTxt = (TextView) convertView
                    .findViewById(R.id.out_history_plan_backtime_txt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHoler) convertView.getTag();
        }

        OutControl item = list.get(position);

        String title = mContext.getResources().getString(
                R.string.out_control_out_time);
        Date date = DateUtil.getDateCompat(item.WCDJSJ);
        String time = DateUtil.format_yyyyMMdd_HHmm.format(date);
        holder.mOutTxt.setText(title + time);

        title = mContext.getResources().getString(
                R.string.out_control_plan_back_time);
        date = DateUtil.getDateCompat(item.YJHCSJ);
        time = DateUtil.format_yyyyMMdd_HHmm.format(date);
        holder.mPlanBackTxt.setText(title + time);

        // 回床登记项
        if (!EmptyTool.isBlank(item.HCDJSJ)) {
            title = mContext.getResources().getString(
                    R.string.out_control_back_time);
            date = DateUtil.getDateCompat(item.HCDJSJ);
            time = DateUtil.format_yyyyMMdd_HHmm.format(date);
            holder.mBackTxt.setText(title + time);
            holder.mBackTxt.setVisibility(View.VISIBLE);
        } else {
            holder.mBackTxt.setVisibility(View.GONE);
        }

        if (!EmptyTool.isBlank(item.WCYY)) {
            holder.mWCYYTxt.setVisibility(View.VISIBLE);
            holder.mWCYYTxt.setText(mContext.getString(R.string.out_control_wcyy) + item.WCYY);
        } else {
            holder.mWCYYTxt.setVisibility(View.GONE);
        }

        try {
            holder.mWithTxt.setText(arrays[Integer.valueOf(item.PTRY)]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertView;
    }

    class ViewHoler {

        TextView mOutTxt;
        TextView mBackTxt;
        TextView mWithTxt;
        TextView mWCYYTxt;
        TextView mPlanBackTxt;

    }

}
