package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.announce.AnnnouceTopIdx;

import java.util.ArrayList;

/**
 * 病区列表adapter
 * 
 * @author hy
 * 
 */
public class AnnTopAdapter extends BaseAdapter {

	private ArrayList<AnnnouceTopIdx> list;

	private Context mContext;

	public int selectPostion = 0;

	public AnnTopAdapter(Context context, ArrayList<AnnnouceTopIdx> _list) {
		this.list = _list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public AnnnouceTopIdx getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.popup_area_item, parent,false);

			vHolder = new ViewHolder();

			vHolder.nameView = (TextView) convertView
					.findViewById(R.id.areaname);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		AnnnouceTopIdx vo = list.get(position);
		vHolder.nameView.setText(vo.YSMC);
		if (selectPostion == position) {
			vHolder.nameView.setTextColor(mContext.getResources().getColor(
					R.color.colorAccent));
		} else {
			vHolder.nameView.setTextColor(Color.WHITE);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView nameView;
	}

}
