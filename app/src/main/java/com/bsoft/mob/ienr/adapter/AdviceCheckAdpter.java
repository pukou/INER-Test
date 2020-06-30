package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advicecheck.AdviceForm;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;

public class AdviceCheckAdpter extends BaseAdapter {
    private ArrayList<AdviceForm> list;
    private Context mContext;
    private String type;

    public AdviceCheckAdpter(ArrayList<AdviceForm> list, String type,
                             Context mContext) {
        super();
        this.list = list;
        this.mContext = mContext;
        this.type = type;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public AdviceForm getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_advice_check,  parent,false);
            vHolder = new ViewHolder();
            vHolder.tv_sydh = (TextView) convertView.findViewById(R.id.sydh);
            vHolder.tv_jhsj = (TextView) convertView.findViewById(R.id.jhsj);
            vHolder.tv_brxm = (TextView) convertView.findViewById(R.id.brxm);
            vHolder.tv_brch = (TextView) convertView.findViewById(R.id.brch);
            vHolder.tv_jyhdsj = (TextView) convertView
                    .findViewById(R.id.jyhdsj);
            vHolder.tv_jyhdr = (TextView) convertView.findViewById(R.id.jyhdr);
            vHolder.tv_byhdr = (TextView) convertView.findViewById(R.id.byhdr);
            vHolder.tv_byhdsj = (TextView) convertView
                    .findViewById(R.id.byhdsj);
            vHolder.tv_hdxx = (TextView) convertView.findViewById(R.id.hdxx);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        vHolder.tv_sydh.setText(list.get(position).SYDH);
        vHolder.tv_jhsj.setText(parserDate(list.get(position).SYSJ));
        vHolder.tv_brxm.setText(list.get(position).BRXM);
        vHolder.tv_brch.setText(list.get(position).BRCH + "床");
        //1 摆药 2 加药
        if ("1".equals(type)&&list.get(position).BYHDBZ.equals("1")) {
            // vHolder.tv_byhdr.setText(list.get(position).BYHDR);
            // vHolder.tv_byhdsj.setText(parserDate(list.get(position).BYHDSJ));
            vHolder.tv_hdxx.setText(list.get(position).BYHDR + "  于"
                    + parserDate(list.get(position).BYHDSJ) + " 摆药");
        }
        //1 摆药 2 加药
        if ("2".equals(type)&&list.get(position).JYHDBZ.equals("1")) {
            //
            // vHolder.tv_jyhdr.setText(list.get(position).JYHDR);
            //
            // vHolder.tv_jyhdsj.setText(parserDate(list.get(position).JYHDSJ));

            vHolder.tv_hdxx.setText(list.get(position).JYHDR + " 于  "
                    + parserDate(list.get(position).JYHDSJ) + " 加药");
        }
        // if (type.equals("1")) {
        // vHolder.tv_byhdr.setVisibility(View.VISIBLE);
        // vHolder.tv_byhdsj.setVisibility(View.VISIBLE);
        // vHolder.tv_jyhdr.setVisibility(View.GONE);
        // vHolder.tv_jyhdsj.setVisibility(View.GONE);
        // } else if (type.equals("2")) {
        // vHolder.tv_byhdr.setVisibility(View.GONE);
        // vHolder.tv_byhdsj.setVisibility(View.GONE);
        // vHolder.tv_jyhdr.setVisibility(View.VISIBLE);
        // vHolder.tv_jyhdsj.setVisibility(View.VISIBLE);
        // }

        return convertView;
    }

    class ViewHolder {
        TextView tv_sydh;
        TextView tv_jhsj;
        TextView tv_brxm;
        TextView tv_brch;
        TextView tv_byhdr;
        TextView tv_byhdsj;
        TextView tv_jyhdr;
        TextView tv_jyhdsj;
        TextView tv_hdxx;
    }

    private String parserDate(String date) {
        String dateStr = "";
        if ("".equals(date) || EmptyTool.isBlank(date)) {
            return dateStr;
        } else {
            try {
                dateStr = DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(date));
            } catch (Exception e) {
                dateStr = "";
            }
            return dateStr;
        }
    }
}
