package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.visit.VisitHistory;

import java.util.ArrayList;

public class VisitHistoryAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<VisitHistory> list;
	private LayoutInflater inflater;

	public VisitHistoryAdapter(Context context, ArrayList<VisitHistory> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(
					R.layout.item_list_visit_history,  parent,false);
			viewHolder = new ViewHolder();
			viewHolder.mTime = (TextView) convertView
					.findViewById(R.id.show_time);
			viewHolder.mContent = (TextView) convertView
					.findViewById(R.id.show_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
//		String time = DateUtil.format_MMdd_HHmm.format(DateUtil.getDate(list
//				.get(position).XSSJ));
		String time = list.get(position).XSSJ.substring(5,16);
		String content = list.get(position).DYMS;
		viewHolder.mContent.setText(content);
		viewHolder.mTime.setText(time);
		return convertView;
	}

	static class ViewHolder {
		TextView mTime;
		TextView mContent;
	}
}
