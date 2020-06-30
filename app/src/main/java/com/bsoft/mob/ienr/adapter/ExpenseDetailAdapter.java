package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.expense.ExpenseDaysDetail;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @明细适配器
 */
public class ExpenseDetailAdapter extends BaseAdapter {

	private ArrayList<ExpenseDaysDetail> list;
	private LayoutInflater inflater;

	public ExpenseDetailAdapter(Context context) {
		this.list = new ArrayList<ExpenseDaysDetail>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(ArrayList<ExpenseDaysDetail> _list) {
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
	public ExpenseDaysDetail getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.item_list_expense_detail, parent,false);
			vHolder = new ViewHolder();

			vHolder.FYMC = (TextView) convertView.findViewById(R.id.FYMC);
			vHolder.FYDJ = (TextView) convertView.findViewById(R.id.FYDJ);
			vHolder.FYSL = (TextView) convertView.findViewById(R.id.FYSL);
			vHolder.ZJJE = (TextView) convertView.findViewById(R.id.ZJJE);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		ExpenseDaysDetail vo = list.get(position);

		vHolder.FYMC.setText(vo.FYMC);
		double temp = Double.valueOf(TextUtils.isEmpty(vo.FYDJ) ? "0" : vo.FYDJ);
		vHolder.FYDJ.setText(StringUtil.getText("单价:", String.format(Locale.CHINA,"%.2f", temp)));
		vHolder.FYSL.setText(StringUtil.getText("数量:", vo.FYSL));
		vHolder.ZJJE.setText(StringUtil.getText("总金额:", vo.ZJJE));

		return convertView;
	}

	class ViewHolder {
		public TextView FYMC, FYDJ, FYSL, ZJJE;
	}

}
