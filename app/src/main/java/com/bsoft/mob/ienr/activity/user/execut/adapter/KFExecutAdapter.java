package com.bsoft.mob.ienr.activity.user.execut.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.execut.KFModel;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @区域适配器
 */
public class KFExecutAdapter extends BaseAdapter {

	private ArrayList<KFModel> list;
	private LayoutInflater inflater;
	// 记录Item是否选中
	//HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    SparseBooleanArray map = new SparseBooleanArray();

	public KFExecutAdapter(Context context) {
		this.list = new ArrayList<KFModel>();
		inflater = LayoutInflater.from(context);
		init();
	}

	public void addData(ArrayList<KFModel> list) {
		this.list = list;
	}

//	public String getQRDH() {
//		if (null != this.list && this.list.size() > 0) {
//			return this.list.get(0).QRDH;
//		}
//		return null;
//	}

	public void init() {
		for (int i = 0; i < list.size(); i++) {
			map.put(i, false);
		}
		notifyDataSetChanged();
	}

	public void clearData() {
		this.list.clear();
		notifyDataSetChanged();
	}

	public int getExecutCount() {
		int count = list.size();
		for (int i = 0; i < list.size(); i++) {
			if (map.get(i)) {
				count--;
			}
		}
		return count;
	}

	public KFModel changeStatue(String tm) {
		for (int i = 0; i < list.size(); i++) {
			if (tm.equals(list.get(i).TMBH)) {
				map.put(i, true);
				return list.get(i);
			}
		}
		return null;
	}

	public void changeStatue(int item) {
		map.put(item, true);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public KFModel getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_text_two_primary,  parent,false);
			vHolder = new ViewHolder();
			vHolder.YDMS = (TextView) convertView.findViewById(R.id.name);
			vHolder.TMBM = (TextView) convertView.findViewById(R.id.time);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		KFModel vo = list.get(position);
		vHolder.YDMS.setText(vo.YDMS);
		vHolder.TMBM.setText(vo.TMBH + "");

		if (map.get(position)) {
			convertView.setBackgroundResource(R.color.classicViewBg);
		} else {
			convertView.setBackgroundResource(R.color.white);
		}

		return convertView;
	}

	class ViewHolder {
		public TextView YDMS, TMBM;
	}

}
