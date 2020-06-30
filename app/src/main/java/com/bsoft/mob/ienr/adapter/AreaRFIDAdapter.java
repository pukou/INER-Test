package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.kernel.AreaVo;

import java.util.Vector;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @区域适配器
 */
@Deprecated
public class AreaRFIDAdapter extends BaseAdapter {

	private Vector<AreaVo> list;
	private LayoutInflater inflater;

	public AreaRFIDAdapter(Context context) {
		this.list = new Vector<AreaVo>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(Vector<AreaVo> _list) {
		this.list = _list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public AreaVo getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.item_list_text_one_primary, parent,false);
			vHolder = new ViewHolder();

			vHolder.nameView = (TextView) convertView
					.findViewById(R.id.name);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		AreaVo vo = list.get(position);
		vHolder.nameView.setText(vo.KSMC);

		return convertView;
	}

	class ViewHolder {
		public TextView nameView;
	}

}
