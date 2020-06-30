package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.TransfusionTourRecordVo;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @医嘱计划适配器
 */
public class TransfusionTourRecordAdapter extends BaseAdapter {

	private ArrayList<TransfusionTourRecordVo> list;
	// private ArrayList<TransfusionTourReactionVo> dlist;
	private LayoutInflater inflater;

	public TransfusionTourRecordAdapter(Context context,
			ArrayList<TransfusionTourRecordVo> _list) {
		this.list = _list;
		// this.dlist = new ArrayList<TransfusionTourReactionVo>();
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public TransfusionTourRecordVo getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder vHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_text_four_secondary,parent,false);
			vHolder = new ViewHolder();

			vHolder.XSSJ = (TextView) convertView.findViewById(R.id.XSSJ);
			vHolder.XSGH = (TextView) convertView.findViewById(R.id.XSGH);
			vHolder.SYDS = (TextView) convertView.findViewById(R.id.SYDS);
			vHolder.SYFY = (TextView) convertView.findViewById(R.id.SYFY);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		TransfusionTourRecordVo vo = list.get(position);
		Date date = DateUtil.getDateCompat(vo.XSSJ);
		String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
		vHolder.XSSJ.setText(dateStr);
		vHolder.XSGH.setText(vo.XSXM);
		vHolder.SYDS.setText(StringUtil.getText("滴速：", vo.SYDS));
		vHolder.SYFY.setText(StringUtil.getText("反应：", vo.FYMC));
		return convertView;
	}

	class ViewHolder {
		public TextView XSSJ, XSGH, SYDS, SYFY;
	}

}
