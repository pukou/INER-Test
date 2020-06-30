package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryInfo;

import java.util.List;


public class LifeSignHistoryInfoAdapter extends BaseAdapter {

    private List<LifeSignHistoryInfo> list;

    private LayoutInflater inflater;

    public LifeSignHistoryInfoAdapter(Context context,
                                      List<LifeSignHistoryInfo> _list) {
        this.list = _list;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public LifeSignHistoryInfo getItem(int arg0) {
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
            //convertView = inflater.inflate(R.layout.item_list_text_two_vert_primary_icon, parent,false);
            convertView = inflater.inflate(R.layout.item_list_text_three_three, parent,false);
            vHolder = new ViewHolder();

            vHolder.tv_cjsj = (TextView) convertView.findViewById(R.id.id_tv_one);
            vHolder.tv_xmmc = (TextView) convertView.findViewById(R.id.id_tv_two);
            vHolder.tv_xmqz = (TextView) convertView.findViewById(R.id.id_tv_three);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        LifeSignHistoryInfo hInfo = list.get(position);

        vHolder.tv_cjsj.setText("采集时间: " + hInfo.CJSJ);
        vHolder.tv_xmmc.setText(hInfo.XMMC + (hInfo.XMXB == null ? "" : "(" + hInfo.XMXB + ")"));
        vHolder.tv_xmqz.setText("取值:" + hInfo.TZNR);

        return convertView;
    }

    class ViewHolder {
        public TextView tv_cjsj;
        public TextView tv_xmmc;
        public TextView tv_xmqz;
    }

}