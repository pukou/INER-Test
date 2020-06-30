package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.dailywork.DailyWork;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;

public class DailyWorkDetailAdapter extends BaseAdapter {
	private ArrayList<DailyWork> list;
	private Context mContext;

	public DailyWorkDetailAdapter(ArrayList<DailyWork> list, Context mContext) {
		super();
		this.list = list;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_list_daily_work_detail, parent,false);
			vHolder = new ViewHolder();
			vHolder.tv_zyhm = (TextView) convertView
					.findViewById(R.id.work_zyhm);
			vHolder.tv_brch = (TextView) convertView
					.findViewById(R.id.work_brch);
			vHolder.tv_brxm = (TextView) convertView
					.findViewById(R.id.work_brxm);
			vHolder.tv_bdmc = (TextView) convertView
					.findViewById(R.id.work_bdmc);
			vHolder.tv_zkms = (TextView) convertView
					.findViewById(R.id.work_zkms);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}
		DailyWork work = list.get(position);
		vHolder.tv_zyhm.setText(work.ZYHM);

		if (work.BRCH!=null){
			vHolder.tv_brch.setText(work.BRCH.contains("床") ? work.BRCH : work.BRCH
					+ "床");
		}

		vHolder.tv_brxm.setText(work.BRXM);
		if (!EmptyTool.isBlank(work.BDMC)){
			vHolder.tv_bdmc.setText(work.BDMC);
			vHolder.tv_zkms.setText(work.ZKMS);
			vHolder.tv_bdmc.setVisibility(View.VISIBLE);
			vHolder.tv_zkms.setVisibility(View.VISIBLE);
		}else {
			vHolder.tv_bdmc.setVisibility(View.GONE);
			vHolder.tv_zkms.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		TextView tv_zyhm;
		TextView tv_brch;
		TextView tv_brxm;
		TextView tv_bdmc;
		TextView tv_zkms;
	}
}
