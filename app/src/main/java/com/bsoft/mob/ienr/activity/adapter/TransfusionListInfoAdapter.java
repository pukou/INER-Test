package com.bsoft.mob.ienr.activity.adapter;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.TransfusionInfoVo;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @输液详情
 */
@Deprecated
public class TransfusionListInfoAdapter extends BaseAdapter {

	private ArrayList<TransfusionInfoVo> list;
	private LayoutInflater inflater;
	// 记录输液单号标示
	//HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    SparseIntArray map = new SparseIntArray();

	public TransfusionListInfoAdapter(Context context) {
		this.list = new ArrayList<TransfusionInfoVo>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(ArrayList<TransfusionInfoVo> _list) {
		this.list = _list;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				map.put(i, 1);
			} else {
				if (list.get(i).YZXH.equals(list.get(i - 1).YZXH)) {
					map.put(i, map.get(i - 1));
				} else {
					if (map.get(i - 1) == 1) {
						map.put(i, 0);
					} else {
						map.put(i, 1);
					}
				}
			}
		}
		notifyDataSetChanged();
	}
	
	public void clearData() {
		this.list.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public TransfusionInfoVo getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.transfusionlistinfo_item, parent,false);
			vHolder = new ViewHolder();

			vHolder.YZMC = (TextView) convertView.findViewById(R.id.YZMC);
			vHolder.JLXX = (TextView) convertView.findViewById(R.id.JLXX);
			vHolder.SLXX = (TextView) convertView.findViewById(R.id.SLXX);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		TransfusionInfoVo vo = list.get(position);
		vHolder.YZMC.setText(vo.YZMC);
		vHolder.JLXX.setText(StringUtil.getText("剂量：", vo.JLXX));
		vHolder.SLXX.setText(StringUtil.getText("数量：", vo.SLXX));

		if (map.get(position) == 1) {
			convertView.setBackgroundResource(R.color.classicViewBg);
		} else {
			convertView.setBackgroundResource(R.color.white);
		}

		return convertView;
	}

	class ViewHolder {
		public TextView YZMC, JLXX, SLXX;
	}

}
