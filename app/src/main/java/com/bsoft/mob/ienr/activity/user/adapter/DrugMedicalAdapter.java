package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurserecord.DrugMedical;

import java.util.List;

/**
 * 护理记录一级
 * 
 * @author hy
 * 
 */
public class DrugMedicalAdapter extends BaseAdapter {

	private Context mContext;
	private List<DrugMedical> list;

	// 记录医嘱组号标示
	SparseIntArray zhmap = new SparseIntArray();
	//
	private static final String TAG_START = "tag_start";
	private static final String TAG_CENTER = "tag_center";
	private static final String TAG_END = "tag_end";
	private static final String TAG_EMPTY = "tag_empty";
	SparseArray<String> yzTableMap = new SparseArray();
	public void init() {
		for (int i = 0; i < list.size(); i++) {
		/*	map.put(i, false);
			checkmap.put(i, false);*/
			if (i == 0) {
				zhmap.put(i, 0);
			} else {
				if (list.get(i).YZZH.equals(list.get(i - 1).YZZH)) {
					zhmap.put(i, zhmap.get(i - 1));
				} else {
					if (zhmap.get(i - 1) == 1) {
						zhmap.put(i, 0);
					} else {
						zhmap.put(i, 1);
					}
				}
			}
		}
		initTag();
	}
	public void initTag() {
		if (list == null || list.size() <= 0) {
			return;
		}
		yzTableMap.clear();
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				//第一个 开始标记
				yzTableMap.put(i, TAG_START);
			} else {
				if (list.get(i).YZZH.equals(list.get(i - 1).YZZH)) {
					//如果和上一个同一组  中间标记
					yzTableMap.put(i, TAG_CENTER);
				} else {
					//如果和上一个不是同一组
					if (TAG_START.equals(yzTableMap.get(i - 1))) {
						//上一个是开始的话
						yzTableMap.put(i - 1, TAG_EMPTY); //上一个
						yzTableMap.put(i, TAG_START); //当前
					} else if (TAG_CENTER.equals(yzTableMap.get(i - 1))) {
						//上一个是中间的话
						yzTableMap.put(i - 1, TAG_END); //上一个
						yzTableMap.put(i, TAG_START);//当前
					} else if (TAG_END.equals(yzTableMap.get(i - 1))) {
						yzTableMap.put(i, TAG_END);//当前
					}
				}
			}
		}
		//Fix last
		String lastTag = yzTableMap.get(yzTableMap.size() - 1);
		if (lastTag != null) {
			if (TAG_EMPTY.equals(lastTag)) {
				lastTag = TAG_EMPTY;
			} else if (TAG_START.equals(lastTag)) {
				lastTag = TAG_EMPTY;
			} else if (TAG_CENTER.equals(lastTag)) {
				lastTag = TAG_END;
			} else if (TAG_END.equals(lastTag)) {
				lastTag = TAG_END;
			}
			yzTableMap.put(yzTableMap.size() - 1, lastTag);
		}
	}

	public DrugMedicalAdapter(Context mContext, List<DrugMedical> list) {
		this.mContext = mContext;
		this.list = list;
		init();
	}

	@Override
	public int getCount() {

		return list != null ? list.size() : 0;
	}

	@Override
	public DrugMedical getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHoler holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_list_text_two_primary, parent,false);
			holder = new ViewHoler();
			holder.itemView = convertView;
			holder.txt1 = (TextView) convertView
					.findViewById(R.id.name);
			holder.txt2 = (TextView) convertView
					.findViewById(R.id.time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHoler) convertView.getTag();
		}

		DrugMedical item = list.get(position);

		StringBuilder YZMCsb = new StringBuilder();
		//
		if (TAG_START.equals(yzTableMap.get(position))) {
			YZMCsb.append("┌ ");
		} else if (TAG_CENTER.equals(yzTableMap.get(position))) {
			YZMCsb.append("├ ");
		} else if (TAG_END.equals(yzTableMap.get(position))) {
			YZMCsb.append("└ ");
		} else if (TAG_EMPTY.equals(yzTableMap.get(position))) {
			YZMCsb.append(" — ");
		}
		if (item != null) {
			YZMCsb.append(item.YZMC);
			holder.txt1.setText(YZMCsb.toString());
			holder.txt2.setText(item.YYNR);
		} else {
			holder.txt1.setVisibility(View.GONE);
			holder.txt2.setVisibility(View.GONE);
		}
		if (zhmap.get(position) == 1) {
			holder.itemView.setBackgroundResource(R.color.classicViewBg);//item
		} else {
			holder.itemView.setBackgroundResource(R.color.classicViewBgLight);//item
		}
		return convertView;
	}

	class ViewHoler {
		TextView txt1;
		TextView txt2;
		 View itemView;
	}

}
