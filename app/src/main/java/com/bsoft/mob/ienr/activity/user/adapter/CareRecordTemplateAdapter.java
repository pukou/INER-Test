package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurserecord.Template;

import java.util.List;

/**
 * 护理记录二级
 * 
 * @author hy
 * 
 */
public class CareRecordTemplateAdapter extends BaseAdapter {

	private Context mContext;
	private List<Template> list;

	String[] arrays = null;

	public CareRecordTemplateAdapter(Context mContext, List<Template> list) {

		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list != null ? list.size() : 0;
	}

	@Override
	public Template getItem(int position) {

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
					R.layout.item_list_text_one_primary_icon, parent,false);
			holder = new ViewHoler();
			holder.nameTxt = (TextView) convertView.findViewById(R.id.name);


			convertView.setTag(holder);
		} else {
			holder = (ViewHoler) convertView.getTag();
		}

		Template item = list.get(position);
		holder.nameTxt.setText(item.JGMC);
		return convertView;
	}

	class ViewHoler {
		TextView nameTxt;
	}

}
