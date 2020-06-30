package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.blood.BloodReciveInfo;

import java.util.ArrayList;

public class BloodRecieveAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BloodReciveInfo> mList=new ArrayList<>();
    private boolean mIsShow;

    public BloodRecieveAdapter(Context mContext, ArrayList<BloodReciveInfo> mList,
                               boolean isShow) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        this.mIsShow = isShow;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public BloodReciveInfo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void  refreshData(ArrayList<BloodReciveInfo> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }
    public void  changeItemSelectedStatus(int pos){
        mList.get(pos).isSelected = !mList.get(pos).isSelected;
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_bloodrecieve,parent,false);
            vHolder = new ViewHolder();
            vHolder.cb_check = (CheckBox) convertView
                    .findViewById(R.id.checkBox);

            vHolder.tv_brxm = (TextView) convertView
                    .findViewById(R.id.list_item_brxm);
            vHolder.tv_brxb = (TextView) convertView
                    .findViewById(R.id.list_item_brxb);
            vHolder.tv_brnl = (TextView) convertView
                    .findViewById(R.id.list_item_brnl);
            vHolder.tv_brch = (TextView) convertView
                    .findViewById(R.id.list_item_brch);
            vHolder.tv_sjsj = (TextView) convertView
                    .findViewById(R.id.list_item_sjsj);
            vHolder.tv_sjhs = (TextView) convertView
                    .findViewById(R.id.list_item_sjhs);
            vHolder.tv_sjhg = (TextView) convertView
                    .findViewById(R.id.list_item_sjhg);
            vHolder.tv_qshs = (TextView) convertView
                    .findViewById(R.id.list_item_qshs);
            vHolder.tv_qssj = (TextView) convertView
                    .findViewById(R.id.list_item_qssj);
            vHolder.ll_root = (LinearLayout) convertView
                    .findViewById(R.id.list_item_root);
            vHolder.ll_container1 = (LinearLayout) convertView
                    .findViewById(R.id.list_item_container1);
            vHolder.ll_container2 = (LinearLayout) convertView
                    .findViewById(R.id.list_item_container2);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        if (mIsShow) {
            vHolder.ll_container1.setVisibility(View.VISIBLE);
            vHolder.cb_check.setVisibility(View.GONE);
            //vHolder.ll_container2.setVisibility(View.VISIBLE);
            vHolder.ll_container2.setVisibility(View.GONE);
        } else {
            vHolder.ll_container1.setVisibility(View.GONE);
            vHolder.ll_container2.setVisibility(View.GONE);
        }

        vHolder.ll_root.setBackgroundColor(ContextCompat .getColor(mContext,android.R.color.white));

//		String status = mList.get(position).Status;
//		if (status.equals("0")) {
//			vHolder.ll_root.setBackgroundColor(mContext.getResources()
//					.getColor(android.R.color.white));
//		} else if (status.equals("1")) {
//			vHolder.ll_root.setBackgroundColor(mContext.getResources()
//					.getColor(R.color.bright_foreground_holo_dark));
//		}
        // adapter 复用 view 时，响应OnCheckedChangeListener事件错乱
        vHolder.cb_check.setOnCheckedChangeListener(null);
        if (mList.get(position).isSelected) {
            vHolder.cb_check.setChecked(true);
        } else {
            vHolder.cb_check.setChecked(false);
        }
        vHolder.cb_check
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        mList.get(position).isSelected = isChecked;
                    }
                });
//		String cjsj = DateUtil.format_yyyyMMdd_HHmm.format(DateUtil.getDateCompat(mList.get(position).CJSJ));
        vHolder.cb_check.setText(mList.get(position).XMMC);
        vHolder.tv_brxm.setText(mList.get(position).BRXM);
        vHolder.tv_brxb.setText(mList.get(position).XBMC);
        vHolder.tv_brnl.setText(mList.get(position).BRNL + "岁");
        vHolder.tv_brch.setText(mList.get(position).BRCH + "床");
        //vHolder.tv_sjsj.setText(mList.get(position).CJSJ);
        vHolder.tv_sjsj.setText(mList.get(position).SJSJ);

        //vHolder.tv_sjhs.setText(mList.get(position).CJXM);
        vHolder.tv_sjhs.setText(mList.get(position).QSXM);
        vHolder.tv_sjhg.setText(mList.get(position).SXXM);

        return convertView;
    }

    class ViewHolder {
        CheckBox cb_check;
        TextView tv_brxm, tv_brxb, tv_brnl, tv_brch, tv_sjsj, tv_sjhs,
                tv_sjhg, tv_qshs, tv_qssj;
        LinearLayout ll_root, ll_container1, ll_container2;
    }
}
