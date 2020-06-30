package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.visit.VisitPerson;

import java.util.List;


public class PersonListAdapter extends BaseAdapter {

	private List<VisitPerson> list;

	private Context mContext;

	public PersonListAdapter(Context context, List<VisitPerson> list) {
		this.list = list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public VisitPerson getItem(int position) {
		return list.get(position);
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
					R.layout.item_list_text_two_secondary, null);
			vHolder = new ViewHolder();

			vHolder.id_tv_one = (TextView) convertView
					.findViewById(R.id.detail_name);
			vHolder.id_tv_two = (TextView) convertView
					.findViewById(R.id.detail_num);
			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		VisitPerson vo = list.get(position);
		vHolder.id_tv_one.setText(vo.BRXM);
		vHolder.id_tv_two.setText(vo.BRCH);

		return convertView;
	}

	class ViewHolder {
		public TextView id_tv_one;
		public TextView id_tv_two;
	}


}
