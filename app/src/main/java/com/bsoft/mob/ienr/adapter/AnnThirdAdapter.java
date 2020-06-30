package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.bsoft.mob.ienr.model.announce.AnnnouceThirdIdx;

import java.util.ArrayList;

/**
 * 病区列表adapter
 * 
 * @author hy
 * 
 */
public class AnnThirdAdapter extends BaseAdapter {

	private ArrayList<AnnnouceThirdIdx> list;

	public ArrayList<AnnnouceThirdIdx> getList() {
		return list;
	}

	private Context mContext;

	public AnnThirdAdapter(Context context, ArrayList<AnnnouceThirdIdx> _list) {
		this.list = _list;
		this.mContext = context;
	}

	public void clear() {

		if (this.list != null) {
			list.clear();
			notifyDataSetChanged();
		}

	}

	/**
	 * 成功返回新增item位置
	 * 
	 * @param index
	 * @return
	 */
	public int addItem(AnnnouceThirdIdx index) {
		if (index == null) {
			return -1;
		}
		if (list == null) {
			list = new ArrayList<AnnnouceThirdIdx>();
		}

		if (!list.contains(index)) {
			list.add(index);
			return list.size() - 1;
		}

		return -1;
	}

	public boolean removeItem(AnnnouceThirdIdx index) {

		if (index == null || list == null) {
			return true;
		}

		if (list.contains(index)) {

			boolean ok = list.remove(index);
			if (ok) {
				notifyDataSetChanged();
			}
			return ok;
		}
		return true;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public AnnnouceThirdIdx getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// ViewHolder vHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_multiple_choice,  parent,false);
			((CheckedTextView) convertView).setTextSize(18);
			int color = mContext.getResources().getColor(android.R.color.black);
			((CheckedTextView) convertView).setTextColor(color);
		}

		AnnnouceThirdIdx vo = list.get(position);
		((CheckedTextView) convertView).setText(vo.XMMC);

		((CheckedTextView) convertView).setChecked(true);
		// ((CheckedTextView) convertView).toggle();
		return convertView;
	}

	class ViewHolder {
		public CheckedTextView nameView;
	}

}
