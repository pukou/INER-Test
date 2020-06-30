package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.daily.DailyTopItem;

import java.util.ArrayList;

/**
 * 病区列表adapter
 * 
 * @author hy
 * 
 */
public class DailyTopAdapter extends BaseAdapter {

	private ArrayList<DailyTopItem> list;

	private Context mContext;

	public DailyTopAdapter(Context context, ArrayList<DailyTopItem> _list) {
		this.list = _list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public DailyTopItem getItem(int arg0) {
		return list.get(arg0);
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
					R.layout.item_list_text_one_secondary_selected, parent,false);

			vHolder = new ViewHolder();

			vHolder.nameView = (TextView) convertView
					.findViewById(R.id.id_tv);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		DailyTopItem vo = list.get(position);
		vHolder.nameView.setText(vo.LBMC);
		return convertView;
	}

	class ViewHolder {
		public TextView nameView;
	}

}
