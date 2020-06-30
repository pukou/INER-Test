package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.DailyWorkDetailActivity;
import com.bsoft.mob.ienr.model.dailywork.DailyWork;
import com.bsoft.mob.ienr.view.waterdropcard.DropCover.OnDragCompeteListener;
import com.bsoft.mob.ienr.view.waterdropcard.WaterDrop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DailyWorkAdapter extends BaseAdapter {
	private final Context context;
	private ArrayList<String> items;
	private List<DailyWork>[] works;
	private static final String YZJH = "WORK_YZJH";
	private static final String BDYZ = "WORK_BDYZ";
	private static final String JYCJ = "WORK_JYCJ";

	public DailyWorkAdapter(Context context, ArrayList<String> items,
			List<DailyWork>... works) {
		super();
		this.context = context;
		this.items = items;
		this.works = works;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {

		return works[position];

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list_daily_work,  parent,false);

			holder = new ViewHolder();
			holder.itemText = (TextView) convertView
					.findViewById(R.id.list_item_card_text);
			holder.itemButton1 = (Button) convertView
					.findViewById(R.id.list_item_card_button_1);
			holder.itemButton2 = (Button) convertView
					.findViewById(R.id.list_item_card_button_2);
			holder.drop = (WaterDrop) convertView.findViewById(R.id.drop);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemText.setText(items.get(position));
		holder.drop.setText(String.valueOf(works[position].size()));
		if (works[position].size() == 0) {
			holder.itemButton2.setEnabled(false);
			holder.itemButton2.setText("暂无");
			holder.drop.setVisibility(View.GONE);
		} else {
			holder.itemButton2.setEnabled(true);
			if(items.get(position).equals("病区当前变动医嘱")){
				holder.itemButton2.setText("查看");
			}else{
				holder.itemButton2.setText("去处理");
			}
			holder.drop.setVisibility(View.VISIBLE);
		}
		holder.drop.setOnDragCompeteListener(new OnDragCompeteListener() {

			@Override
			public void onDrag() {
			}
		});
		holder.itemButton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.drop.setVisibility(View.GONE);
			}
		});
		holder.itemButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						DailyWorkDetailActivity.class);
				intent.putExtra("DETAIL", (Serializable) works[position]);
				intent.putExtra("POSITION", position);
				context.startActivity(intent);
			}
		});
		holder.itemButton1.setVisibility(View.GONE);
		return convertView;
	}

	private static class ViewHolder {
		private TextView itemText;
		private Button itemButton1;
		private Button itemButton2;
		private WaterDrop drop;
	}

	// class saveData extends AsyncTask<Void, Void, Void> {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// SharedPreferences pref_bdyz = context.getSharedPreferences(BDYZ,
	// Context.MODE_PRIVATE);
	// SharedPreferences pref_yzjh = context.getSharedPreferences(YZJH,
	// Context.MODE_PRIVATE);
	// SharedPreferences pref_jycj = context.getSharedPreferences(JYCJ,
	// Context.MODE_PRIVATE);
	// return null;
	// }
	// }
}
