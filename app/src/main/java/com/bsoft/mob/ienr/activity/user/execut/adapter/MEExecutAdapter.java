package com.bsoft.mob.ienr.activity.user.execut.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.execut.MEModel;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @
 */
public class MEExecutAdapter extends BaseAdapter {

	private ArrayList<MEModel> list;
	private LayoutInflater inflater;

	public MEExecutAdapter(Context context, ArrayList<MEModel> _list) {
		this.list = _list;
		inflater = LayoutInflater.from(context);
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
	public MEModel getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.item_list_text_one_primary,parent,false);
			vHolder = new ViewHolder();
			vHolder.YZMC = (TextView) convertView.findViewById(R.id.name);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		MEModel vo = list.get(position);
		vHolder.YZMC.setText(vo.YZMC);
		if(vo.TZBZ==1){
			vHolder.YZMC.setTextColor(Color.RED);
		}else{
			vHolder.YZMC.setTextColor(Color.BLACK);
		}


		return convertView;
	}

	class ViewHolder {
		public TextView YZMC;
	}

}
