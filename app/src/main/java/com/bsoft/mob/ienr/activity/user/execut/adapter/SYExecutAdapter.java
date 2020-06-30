package com.bsoft.mob.ienr.activity.user.execut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.execut.SYModel;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40 @
 */
public class SYExecutAdapter extends BaseAdapter {

	private ArrayList<SYModel> list;
	private LayoutInflater inflater;
	private int i = 1;

	public SYExecutAdapter(Context context) {
		this.list = new ArrayList<SYModel>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(ArrayList<SYModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void clearData() {
		this.list.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	public SYModel contains(String tm) {
		if (null != this.list && this.list.size() > 0) {
			for (SYModel vo : list) {
				if (tm.equals(vo.TMBH)) {
					return vo;
				}
			}
		}
		return null;
	}

	@Override
	public SYModel getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.syexecut_item,  parent,false);
			vHolder = new ViewHolder();
			vHolder.ll_container = (LinearLayout) convertView
					.findViewById(R.id.container);
			vHolder.YDMS = (TextView) convertView.findViewById(R.id.YDMS);
			vHolder.TMBM = (TextView) convertView.findViewById(R.id.TMBM);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		SYModel vo = list.get(position);
		int[] backgroundColor = { R.color.classicViewBg, R.color.white };
		int currentColor;
		if (position > 0) {
			if (!vo.TMBH.equals(list.get(position - 1).TMBH)) {
				if (--i < 0)
					i = 1;
			}
		}
		currentColor = backgroundColor[i];
		vHolder.ll_container.setBackgroundResource(currentColor);
		vHolder.YDMS.setText(vo.YZMC);
		vHolder.TMBM.setText(vo.TMBH + "");

		return convertView;
	}

	class ViewHolder {
		public TextView YDMS, TMBM;
		public LinearLayout ll_container;
	}

}
