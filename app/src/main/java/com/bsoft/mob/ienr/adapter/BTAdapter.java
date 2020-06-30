package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionInfo;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.Date;

public class BTAdapter extends BaseAdapter {

    private ArrayList<BloodTransfusionInfo> list;

    private Context mContext;

    public int checkedPostion = -1;

    public BTAdapter(Context mContext, ArrayList<BloodTransfusionInfo> list) {

        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list != null ? list.size() : 0;
    }

    @Override
    public BloodTransfusionInfo getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_bt, parent, false);

            holder = new ViewHolder();

            holder.MC = (TextView) convertView.findViewById(R.id.MC);
            holder.ABO = (TextView) convertView.findViewById(R.id.ABO);
            holder.stateTxt = (TextView) convertView.findViewById(R.id.ZXZT);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox);
            holder.allNameTxt = (TextView) convertView
                    .findViewById(R.id.allName);
            holder.XL = (TextView) convertView.findViewById(R.id.XL);
            holder.XDH = (TextView) convertView.findViewById(R.id.XDH);
            holder.SXR = (TextView) convertView.findViewById(R.id.SXR);
            holder.JSR = (TextView) convertView.findViewById(R.id.JSR);
            holder.PXFF = (TextView) convertView.findViewById(R.id.PXFF);
            holder.YYRQ = (TextView) convertView.findViewById(R.id.YYRQ);
            holder.DQRQ = (TextView) convertView.findViewById(R.id.DQRQ);
            holder.KSSJ = (TextView) convertView.findViewById(R.id.KSSJ);
            holder.JSSJ = (TextView) convertView.findViewById(R.id.JSSJ);

            holder.view = convertView.findViewById(R.id.view);
            holder.boxView = convertView.findViewById(R.id.boxView);

            convertView.setTag(holder);

            holder.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (holder.boxView.getVisibility() == View.GONE) {
                        holder.boxView.setVisibility(View.VISIBLE);
                    } else {
                        holder.boxView.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BloodTransfusionInfo entity = list.get(position);

        holder.MC.setText(entity.MC);
        holder.allNameTxt.setText(entity.MC);

        holder.ABO.setText("血型：" + entity.ABO + ";");

        holder.XL.setText("血量:" + entity.XL + entity.BAOZHUANG);

        holder.XDH.setText("血袋号：" + entity.XDH);

        if (EmptyTool.isBlank(entity.PXFF)) {
            holder.PXFF.setVisibility(View.GONE);
        } else {
            holder.PXFF.setVisibility(View.VISIBLE);
            holder.PXFF.setText("交叉配血实验结果：" + entity.PXFF);
        }
        Date date = DateUtil.getDateCompat(entity.YYRQ);
        String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
        holder.YYRQ.setText("预约日期：" + dateStr);
        date = DateUtil.getDateCompat(entity.DQRQ);
        dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
        holder.DQRQ.setText("截止日期：" + dateStr);

        if (EmptyTool.isBlank(entity.KSSJ)) {
            holder.KSSJ.setVisibility(View.GONE);
        } else {
            holder.KSSJ.setVisibility(View.VISIBLE);
            date = DateUtil.getDateCompat(entity.KSSJ);
            dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
            holder.KSSJ.setText("开始时间：" + dateStr);
        }

        if (EmptyTool.isBlank(entity.JSSJ)) {
            holder.JSSJ.setVisibility(View.GONE);
        } else {
            holder.JSSJ.setVisibility(View.VISIBLE);
            date = DateUtil.getDateCompat(entity.JSSJ);
            dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
            holder.JSSJ.setText("开始时间：" + dateStr);
        }

        String SXR1 = entity.SXR1;
        String SXR2 = entity.SXR2;
        String JSR = entity.JSR;

        if (!EmptyTool.isBlank(SXR1) || !EmptyTool.isBlank(SXR2)) {

            holder.stateTxt.setVisibility(View.VISIBLE);
            holder.SXR.setVisibility(View.VISIBLE);

            StringBuilder sb = new StringBuilder("开始人：");
            if (!EmptyTool.isBlank(SXR1)) {
                sb.append(SXR1).append("[执行]");
                if (!EmptyTool.isBlank(SXR2)) {
                    sb.append(",");
                }
            }
            if (!EmptyTool.isBlank(SXR2)) {
                sb.append(SXR2).append("[核对]");
            }
            holder.SXR.setText(sb.toString());

            if (!EmptyTool.isBlank(JSR)) {
                holder.stateTxt.setText("已");
                holder.JSR.setText("结束人：" + JSR);
                holder.JSR.setVisibility(View.VISIBLE);
            } else {
                holder.stateTxt.setText("→");
                holder.JSR.setVisibility(View.GONE);
            }

        } else {

            holder.stateTxt.setVisibility(View.INVISIBLE);
            holder.SXR.setVisibility(View.GONE);
            holder.JSR.setVisibility(View.GONE);
        }
        // adapter 复用 view 时，响应OnCheckedChangeListener事件错乱
//        holder.mCheckBox.setOnCheckedChangeListener(null);

        holder.mCheckBox.setChecked(position == checkedPostion);
        holder.mCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedPostion = position;
                notifyDataSetChanged();
            }
        });
       /* holder.mCheckBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            checkedPostion = position;
                        }
                        notifyDataSetChanged();
                    }
                });*/

        return convertView;
    }

    class ViewHolder {

        public View view, boxView;

        public TextView MC;

        public TextView ABO;

        public TextView stateTxt;

        public CheckBox mCheckBox;

        public TextView XL;

        public TextView allNameTxt;

        public TextView XDH;

        public TextView SXR;

        public TextView JSR;

        public TextView PXFF;

        public TextView YYRQ;

        public TextView DQRQ;

        public TextView KSSJ;

        public TextView JSSJ;
    }

}
