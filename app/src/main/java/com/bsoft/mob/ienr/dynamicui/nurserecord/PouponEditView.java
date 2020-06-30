package com.bsoft.mob.ienr.dynamicui.nurserecord;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.PopupWindowHelper;

import java.util.ArrayList;
import java.util.List;

public class PouponEditView extends LinearLayout {

	public PouponEditView(Context context) {
		super(context);
		init(context);
	}

	public View mainView;
	public LayoutInflater mInflater;
	public EditText edit;
	public ImageView down;
	public PullAdapter adapter;
	public ListView listView;
	public ArrayList<PouponItem> datas;
	private TextView title;

	private PopupWindow pop;

	OnSelectListener selectListener;

	public void setOnSelectListener(OnSelectListener listener) {
		this.selectListener = listener;
	}

	public OnSelectListener getOnSelectListener() {
		return this.selectListener;
	}

	public void setEditHitText(String text) {
		edit.setHint(text);
	}

	public EditText getEditText() {
		return edit;
	}

	public void setEditIsAdble(boolean flage) {
		edit.setEnabled(flage);
	}

	public void hidden() {
		if (null != pop) {
			if (pop.isShowing()) {
				pop.dismiss();
			}
		}
	}
	public void setDataList(List<PouponItem> d) {

		this.datas = (ArrayList<PouponItem>) d;
		adapter = new PullAdapter();
		listView.setAdapter(adapter);
		listView.setBackgroundColor(ContextCompat.getColor(listView.getContext(),R.color.windowBackground));
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == pop) {
					pop = new PopupWindow(listView, edit.getWidth(),
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					pop.setTouchable(true);
					pop.setOutsideTouchable(true);
					pop.setBackgroundDrawable(new BitmapDrawable(
							getResources(), (Bitmap) null));
//					pop.showAsDropDown(edit);
                    PopupWindowHelper.show(pop, edit);
				} else {
					if (pop.isShowing()) {
						pop.dismiss();
					} else {
//						pop.showAsDropDown(edit);
                        PopupWindowHelper.show(pop, edit);
					}
				}
			}
		});
		addView(mainView, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mainView = mInflater.inflate(R.layout.pouponeditview, null,false);
		edit = (EditText) mainView.findViewById(R.id.edit);
		down = (ImageView) mainView.findViewById(R.id.down);
		title = (TextView) mainView.findViewById(R.id.text);
		listView = new ListView(context);

	}

	public void setTitle(String titleStr) {
		if (title != null) {
			title.setText(titleStr);
		}
	}

	public void setEditText(String content) {
		if (edit != null) {
			edit.setText(content);
		}
	}

	class PullAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater
                        .inflate(R.layout.item_list_text_one, parent, false);
				holder.text_row = (TextView) convertView
						.findViewById(R.id.text_row);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text_row.setText(datas.get(position).XZNR);
			holder.text_row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					edit.setText(datas.get(position).XZNR);
					edit.setSelection(datas.get(position).XZNR.length());
					if (null != selectListener) {
						selectListener.doSelect(datas.get(position).XZNR);
					}
					pop.dismiss();
				}
			});
			return convertView;
		}

		public final class ViewHolder {
			public TextView text_row;
		}
	}

	public interface OnSelectListener {
		public void doSelect(String id);
	}

}
