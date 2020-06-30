package com.bsoft.mob.ienr.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryDataItem;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @区域适配器
 */
public class LifeSymptomInquiryAdapter extends BaseAdapter {

	private ArrayList<LifeSignHistoryDataItem> list;
	private LayoutInflater inflater;

	public LifeSymptomInquiryAdapter(Context context) {
		this.list = new ArrayList<LifeSignHistoryDataItem>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(ArrayList<LifeSignHistoryDataItem> _list) {
		this.list.addAll(_list);
		notifyDataSetChanged();
	}

	public void clearData() {
		this.list.clear();
		notifyDataSetChanged();
	}

	public void remove(int index) {
		this.list.remove(index);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public LifeSignHistoryDataItem getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.item_list_text_five_vert_primary,parent,false);
			vHolder = new ViewHolder();

			vHolder.XMMC = (TextView) convertView.findViewById(R.id.XMMC);
			vHolder.TZNR = (TextView) convertView.findViewById(R.id.TZNR);
			vHolder.CJSJ = (TextView) convertView.findViewById(R.id.CJSJ);
			vHolder.FCBZ = (TextView) convertView.findViewById(R.id.FCBZ);
			vHolder.XMXB = (TextView) convertView.findViewById(R.id.XMXB);
		/*	vHolder.boxview = (LinearLayout) convertView
					.findViewById(R.id.boxview);*/

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		LifeSignHistoryDataItem vo = list.get(position);
		vHolder.XMMC.setText(StringUtil.getText("体征名称：", vo.XMMC));
		vHolder.TZNR.setText(StringUtil.getText("体征值：", vo.TZNR));
//		vHolder.CJSJ.setText(StringUtil.getText("录入时间：",
//				DateUtil.get8To7Sstr(vo.CJSJ)));
		vHolder.CJSJ.setText(StringUtil.getText("录入时间：", vo.CJSJ));
		if ("1".equals(vo.FCBZ)){
			vHolder.FCBZ.setText(StringUtil.getText("复测标志：","复测体温"));
			vHolder.FCBZ.setVisibility(View.VISIBLE);
		}else{
			vHolder.FCBZ.setVisibility(View.GONE);
		}

		vHolder.XMXB.setText(StringUtil.getText("备注：", vo.XMXB));
		if (position != getCount() - 1) {
			if (vo.XMH == list.get(position + 1).XMH) {
			//	vHolder.boxview.setVisibility(View.GONE);
			} else {
			//	vHolder.boxview.setVisibility(View.VISIBLE);
			}
		} else {
			//vHolder.boxview.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		public TextView XMMC, TZNR, CJSJ,FCBZ, XMXB;
		public LinearLayout boxview;
	}

}
