package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurserecord.HelpTree;

import java.util.List;

/**
 * 护理记录一级
 * 
 * @author hy
 * 
 */
public class CareRecordHelpTreeAdapter extends BaseAdapter {

	private Context mContext;
	private List<HelpTree> list;

	String[] arrays = null;

	public CareRecordHelpTreeAdapter(Context mContext, List<HelpTree> list) {

		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list != null ? list.size() : 0;
	}

	@Override
	public HelpTree getItem(int position) {

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
					R.layout.item_list_text_one_primary_icon,  parent,false);
			holder = new ViewHoler();
			holder.nameTxt = (TextView) convertView.findViewById(R.id.name);
			holder.numImg = (TextView) convertView.findViewById(R.id.id_tv_more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHoler) convertView.getTag();
		}

		HelpTree item = list.get(position);
		if (item != null) {
			holder.nameTxt.setText(item.MLMC);
			int size = item.Items != null ? item.Items.size() : 0;
			if (size > 0) {
				holder.numImg.setVisibility(View.VISIBLE);
				holder.numImg.setText("(" + String.valueOf(size) + ")");
			} else {
				holder.numImg.setVisibility(View.GONE);
			}
		} else {
			holder.nameTxt.setText(null);
			holder.numImg.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHoler {
		TextView nameTxt;
		TextView numImg;
	}

}
