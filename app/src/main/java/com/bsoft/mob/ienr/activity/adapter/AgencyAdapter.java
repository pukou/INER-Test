package com.bsoft.mob.ienr.activity.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.model.kernel.Agency;

import java.util.ArrayList;

public class AgencyAdapter extends BaseAdapter {

	private ArrayList<Agency> list;
	private Context mContext;

	public AgencyAdapter(Context mContext, ArrayList<Agency> list) {
		this.list = list;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {

		return list != null ? list.size() : 0;
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

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_spinner_item,  parent,false);
			convertView.setMinimumHeight(100);
			((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
		}
		((TextView) convertView).setText(list.get(position).JGMC);
		return convertView;
	}

}
