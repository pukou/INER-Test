package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.announce.AnnnouceSecondIdx;

import java.util.ArrayList;

/**
 * 病区列表adapter
 * 
 * @author hy
 * 
 */
public class AnnSecondAdapter extends BaseAdapter {

	private ArrayList<AnnnouceSecondIdx> list;

	private Context mContext;

	public AnnSecondAdapter(Context context, ArrayList<AnnnouceSecondIdx> _list) {
		this.list = _list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public AnnnouceSecondIdx getItem(int arg0) {
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
					R.layout.item_list_text_one_primary, parent,false);

			vHolder = new ViewHolder();

			vHolder.nameView = (TextView) convertView
					.findViewById(R.id.name);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		AnnnouceSecondIdx vo = list.get(position);
		vHolder.nameView.setText(vo.XMMC);
		return convertView;
	}

	class ViewHolder {
		public TextView nameView;
	}

}
