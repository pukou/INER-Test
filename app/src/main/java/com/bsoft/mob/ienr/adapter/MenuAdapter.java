package com.bsoft.mob.ienr.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.MemuVo;

import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {

	// LayoutInflater inflater;
	ArrayList<MemuVo> list;
	private boolean isPressed[];
	private Context mContext;

	public MenuAdapter(Activity activity, ArrayList<MemuVo> list) {
		this.mContext = activity;
		// this.inflater = LayoutInflater.from(activity);
		this.list = list;
		if (list != null && list.size() > 0) {
			this.isPressed = new boolean[list.size()];
			this.isPressed[0] = true;
		}
	}

	public void changeState(int position) {
		for (int i = 0; i < getCount(); i++) {
			isPressed[i] = false;
		}
		isPressed[position] = true;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list != null ? this.list.size() : 0;
	}

	@Override
	public MemuVo getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ListItemsView listItemsView;
		if (convertView == null) {
			listItemsView = new ListItemsView();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_list_bar_image_text_white, parent,false);
			listItemsView.menuIcon = (ImageView) convertView
					.findViewById(R.id.menuIcon);
			listItemsView.menuText = (TextView) convertView
					.findViewById(R.id.menuText);
			// 中文加粗
			TextPaint tp = listItemsView.menuText.getPaint();
			tp.setFakeBoldText(true);
			convertView.setTag(listItemsView);
		} else {
			listItemsView = (ListItemsView) convertView.getTag();
		}

		listItemsView.menuIcon.setImageResource(getItem(position).headIconId);
		listItemsView.menuText.setText(getItem(position).name);


		if (this.isPressed[position] == true) {
			// convertView.setBackgroundResource(R.drawable.menu_item_bg_sel);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.colorPrimaryDark));
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}

	public final class ListItemsView {
		public ImageView menuIcon;
		public TextView menuText;
	}

}
