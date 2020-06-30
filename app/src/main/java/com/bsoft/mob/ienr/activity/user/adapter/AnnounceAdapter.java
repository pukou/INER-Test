package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.announce.AnnounceItem;
import com.bsoft.mob.ienr.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;

public class AnnounceAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<AnnounceItem> list;

	public AnnounceAdapter(Context mContext, ArrayList<AnnounceItem> list) {

		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list != null ? list.size() : 0;
	}

	@Override
	public AnnounceItem getItem(int position) {

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
					R.layout.item_list_ann_history, parent,false);
			holder = new ViewHoler();
			holder.mNurseTxt = (TextView) convertView
					.findViewById(R.id.ann_history_nurse);
			holder.mTimeTxt = (TextView) convertView
					.findViewById(R.id.ann_history_time);
			holder.mPaitentTxt = (TextView) convertView
					.findViewById(R.id.ann_history_paitent);
			holder.mProTxt = (TextView) convertView
					.findViewById(R.id.ann_history_pro);
			convertView.setTag(holder);
		} else {
			holder = (ViewHoler) convertView.getTag();
		}

		AnnounceItem item = list.get(position);
		Date date = DateUtil.getDateCompat(item.XJSJ);
		String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
		holder.mTimeTxt.setText(dateStr);
		holder.mProTxt.setText(item.SJXMMC + "-" + item.XMMC);

		holder.mNurseTxt.setText(item.XJYF);
		holder.mPaitentTxt.setText(item.XJDX);

		return convertView;
	}

	class ViewHoler {

		TextView mNurseTxt;
		TextView mTimeTxt;
		TextView mPaitentTxt;
		TextView mProTxt;
	}

}
