package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.trad.JSJL;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

public class TradRecordListAdapter extends BaseAdapter {

    private Context mContext;
    private List<JSJL> list;

    public TradRecordListAdapter(Context mContext, List<JSJL> list) {

        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list != null ? list.size() : 0;
    }

    @Override
    public JSJL getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_five_secondary, parent, false);
            holder = new ViewHolder();
            holder.id_tv = (TextView) convertView
                    .findViewById(R.id.id_tv);
            holder.id_tv2 = (TextView) convertView
                    .findViewById(R.id.id_tv_2);
            holder.id_tv3 = (TextView) convertView
                    .findViewById(R.id.id_tv_3);
            holder.id_tv4 = (TextView) convertView
                    .findViewById(R.id.id_tv_4);
            holder.id_tv5 = (TextView) convertView
                    .findViewById(R.id.id_tv_5);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSJL item = list.get(position);
        holder.id_tv.setText(item.ZZMC);
        holder.id_tv2.setText(item.XMLBMC);
        holder.id_tv3.setText(item.FFMC);
        if ("1".equals(item.ZDYBZ)) {
            holder.id_tv3.setTextColor(Color.RED);
        }
        holder.id_tv4.setText(item.ZXZTMC);
        if (!EmptyTool.isBlank(item.CZSJ)) {
            String xxx = DateTimeTool.dateTime2Custom(item.CZSJ, "yy-MM-dd\nHH:mm:ss");
            holder.id_tv5.setText(xxx);
        }


        return convertView;
    }

    public void refreshData(List<JSJL> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView id_tv;
        TextView id_tv2;
        TextView id_tv3;
        TextView id_tv4;
        TextView id_tv5;

    }

}
