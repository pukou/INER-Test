package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.expense.ExpenseVo;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @汇总适配器
 */
public class ExpenseAdapter extends BaseAdapter {

	private ArrayList<ExpenseVo> list;
	private LayoutInflater inflater;

	public ExpenseAdapter(Context context) {
		this.list = new ArrayList<ExpenseVo>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(ArrayList<ExpenseVo> _list) {
		this.list.addAll(_list);
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
	public ExpenseVo getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.item_list_expense,  parent,false);
			vHolder = new ViewHolder();

			vHolder.SFMC = (TextView) convertView.findViewById(R.id.SFMC);
			vHolder.ZJJE = (TextView) convertView.findViewById(R.id.ZJJE);
			vHolder.ZFJE = (TextView) convertView.findViewById(R.id.ZFJE);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		ExpenseVo vo = list.get(position);

		vHolder.SFMC.setText(StringUtil.getText("名称:", vo.SFMC));
		vHolder.ZJJE.setText(StringUtil.getText("金额:", vo.ZJJE));
		vHolder.ZFJE.setText(StringUtil.getText("自负:", vo.ZFJE));

		return convertView;
	}

	class ViewHolder {
		public TextView SFMC, ZJJE, ZFJE;
	}

}
