package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

/**
 * 输液巡视类型adapter
 * 
 * @author hy
 * 
 */
@Deprecated
public class TranTypeAdapter extends BaseAdapter {

	private String[] list;

	private Context mContext;

	public String mCurType;

	public TranTypeAdapter(Context context, String[] list) {
		this.list = list;
		this.mContext = context;
		setCurType(list);
	}

	private void setCurType(String[] list) {

		if (list != null && list.length > 0) {
			mCurType = list[0];
		}
	}

	@Override
	public int getCount() {
		return list != null ? list.length : 0;
	}

	@Override
	public String getItem(int position) {
		return list[position];
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
					R.layout.popup_area_item,  parent,false);

			vHolder = new ViewHolder();

			vHolder.nameView = (TextView) convertView
					.findViewById(R.id.areaname);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		String name = list[position];
		if (!EmptyTool.isBlank(name) && name.equals(mCurType)) {
			vHolder.nameView.setTextColor(mContext.getResources().getColor(
					R.color.colorAccent));
		} else {
			vHolder.nameView.setTextColor(Color.WHITE);
		}
		vHolder.nameView.setText(list[position]);

		return convertView;
	}

	class ViewHolder {
		public TextView nameView;
	}

}
