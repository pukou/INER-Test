package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurserecord.OtherRefer;

import java.util.List;

/**
 * 护理记录一级
 * 
 * @author hy
 * 
 */
@Deprecated
public class OtherAdapter extends BaseAdapter {

	private Context mContext;
	private List<OtherRefer> list;

	String[] arrays = null;

	public OtherAdapter(Context mContext, List<OtherRefer> list) {

		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list != null ? list.size() : 0;
	}

	@Override
	public OtherRefer getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHoler holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_list_text_two_primary,  parent,false);
			holder = new ViewHoler();
			holder.txt1 = (TextView) convertView
					.findViewById(R.id.name);
			holder.txt2 = (TextView) convertView
					.findViewById(R.id.time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHoler) convertView.getTag();
		}

		OtherRefer item = list.get(position);
		if (item != null) {
			holder.txt1.setText(item.LBMC);
			holder.txt2.setText(item.LBMC);

		} else {
			holder.txt1.setVisibility(View.GONE);
			holder.txt2.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHoler {
		TextView txt1;
		TextView txt2;

	}

}
