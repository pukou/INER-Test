package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.inspection.InspectionVo;
import com.bsoft.mob.ienr.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @检验 适配器
 */
public class InspectionAdapter extends BaseAdapter {

    private ArrayList<InspectionVo> list;
    private LayoutInflater inflater;

    public InspectionAdapter(Context context) {
        this.list = new ArrayList<InspectionVo>();
        inflater = LayoutInflater.from(context);
    }

    public void addData(ArrayList<InspectionVo> _list) {
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
    public InspectionVo getItem(int arg0) {
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
            convertView = inflater.inflate(R.layout.item_list_text_two_inspe, parent, false);
            vHolder = new ViewHolder();

            vHolder.XMMC = (TextView) convertView.findViewById(R.id.name);
            vHolder.SHSJ = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        InspectionVo vo = list.get(position);
        vHolder.XMMC.setText(vo.XMMC);
        Date date = DateUtil.getDateCompat(vo.SHSJ);
        String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
        vHolder.SHSJ.setText(dateStr);
        if (vo.YCBZ == 1) {
            vHolder.XMMC.setTextColor(Color.RED);
        } else {
            vHolder.XMMC.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView XMMC, SHSJ;
    }

}
