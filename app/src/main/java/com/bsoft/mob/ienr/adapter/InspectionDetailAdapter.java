package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.inspection.InspectionDetailVo;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @检验 适配器
 */

  /*
         升级编号【56010026】============================================= start
         检验检查：详情页状态用颜色区分
         ================= Classichu 2017/10/18 9:34
         */
public class InspectionDetailAdapter extends BaseAdapter {

	private ArrayList<InspectionDetailVo> list;
	private LayoutInflater inflater;

	public InspectionDetailAdapter(Context context) {
		this.list = new ArrayList<InspectionDetailVo>();
		inflater = LayoutInflater.from(context);
	}

	public void addData(ArrayList<InspectionDetailVo> _list) {
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
	public InspectionDetailVo getItem(int arg0) {
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
			convertView = inflater.inflate(R.layout.item_list_inspection_detail,parent,false);
			vHolder = new ViewHolder();

			vHolder.ZWMC = (TextView) convertView.findViewById(R.id.JCSJ);
			vHolder.HYJG = (TextView) convertView.findViewById(R.id.JCMC);
			vHolder.CKXX = (TextView) convertView.findViewById(R.id.BWMS);

			vHolder.JGTS = (TextView) convertView.findViewById(R.id.JCYS);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		InspectionDetailVo vo = list.get(position);

		vHolder.ZWMC.setText(vo.ZWMC);
		// vHolder.HYJG.setText("检验结果："+vo.HYJG + vo.DW);
		// vHolder.CKXX.setText("参考范围："+vo.CKXX + "-" + vo.CKSX);
		if (!EmptyTool.isBlank(vo.JGTS)){
			vHolder.JGTS.setText(vo.JGTS);
		}
		vHolder.HYJG
				.setText(StringUtil.getStringTexts(vo.HYJG,vo.DW));

		//参考范围 CKFW 存放在CKSX中
		if (!EmptyTool.isBlank(vo.CKSX)) {
			vHolder.CKXX.setText(StringUtil.getStringTexts("",vo.CKSX));
		} else {
			vHolder.CKXX.setText("暂无");
		}
		/*if (!EmptyTool.isBlank(vo.CKXX) || !EmptyTool.isBlank(vo.CKSX)) {
			vHolder.CKXX.setText(StringUtil.getStringTexts("参考范围：", vo.CKXX,
					"-", vo.CKSX));
		} else {
			vHolder.CKXX.setText("暂无参考范围");
		}*/

		if (vo.YCBZ == 1) {
			vHolder.HYJG.setTextColor(Color.RED);
			vHolder.ZWMC.setTextColor(Color.RED);
			vHolder.JGTS.setTextColor(Color.RED);
			/*change by louis 2017-8-16 16:17:36
			try {
				float result = Float.valueOf(vo.HYJG);
				float down = Float.valueOf(vo.CKXX);
				float up = Float.valueOf(vo.CKSX);
				if (result > up) {
					vHolder.HYJG.setTextColor(Color.RED);
				} else if (result < down) {
					vHolder.HYJG.setTextColor(Color.BLUE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}*/

		} else {
			vHolder.HYJG.setTextColor(Color.BLACK);
			//add
			vHolder.ZWMC.setTextColor(Color.BLACK);
			vHolder.JGTS.setTextColor(Color.BLACK);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView ZWMC, HYJG, CKXX,JGTS;
	}

}
    /* =============================================================== end */