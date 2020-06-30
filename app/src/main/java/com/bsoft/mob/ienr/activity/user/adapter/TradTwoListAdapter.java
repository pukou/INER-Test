package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.model.trad._HLJS;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

import static com.bsoft.mob.ienr.R.id.id_cb;
import static com.bsoft.mob.ienr.R.id.id_tv_4;
import static com.bsoft.mob.ienr.R.id.id_tv_5;

public class TradTwoListAdapter extends BaseAdapter {

    private Context mContext;
    private List<_HLJS> list;
    private boolean isAllCanEdit;

    public TradTwoListAdapter(Context mContext, List<_HLJS> hljs, boolean isAllCanEdit) {

        this.mContext = mContext;
        this.list = hljs;
        this.isAllCanEdit = isAllCanEdit;
    }

    @Override
    public int getCount() {

        return list != null ? list.size() : 0;
    }

    @Override
    public _HLJS getItem(int position) {

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
                    R.layout.item_list_trad_two, parent, false);
            holder = new ViewHolder();
            holder.id_tv = (TextView) convertView
                    .findViewById(R.id.id_tv);//主要症状
            holder.id_tv2 = (TextView) convertView
                    .findViewById(R.id.id_tv_2);//医嘱计划
            holder.id_tv3 = (TextView) convertView
                    .findViewById(R.id.id_tv_3);//频次
            holder.id_tv4 = (TextView) convertView
                    .findViewById(id_tv_4);//计划时间

            holder.id_cb = (CheckBox) convertView
                    .findViewById(id_cb);

            holder.id_tv5 = (TextView) convertView
                    .findViewById(id_tv_5);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final _HLJS hljs = list.get(position);
        holder.id_tv.setText(hljs.name);
        holder.id_tv2.setText(hljs.YZMC);
        holder.id_tv3.setText(hljs.SYPC);
        if (!EmptyTool.isBlank(hljs.JHSJ)) {
            String xxx = DateTimeTool.custom2DateTime(hljs.JHSJ,
                    DateTimeFormat.yyyy_MM_dd_HHmmss_S);
            holder.id_tv4.setText(xxx);
        }
        holder.id_tv5.setText(hljs.BZXX);
        holder.id_cb.setText(hljs.FFMC);
        holder.id_cb.setTag(hljs);
        holder.id_cb.setChecked("1".equals(hljs.status));
        holder.id_cb.setEnabled(isAllCanEdit);
        holder.id_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton == null || !compoundButton.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                _HLJS hljs = (_HLJS) compoundButton.getTag();
                hljs.status = b ? "1" : "0";
                //
                checkTheSameJHHCheckBox(hljs);
                Log.i(Constant.TAG_COMM, "onCheckedChanged: ");
            }
        });
        /*     holder.id_tv6.setText(item.name6);*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllCanEdit) {
                    return;
                }
                //取反
                hljs.status = holder.id_cb.isChecked() ? "0" : "1";
                holder.id_cb.setChecked("1".equals(hljs.status));
            }
        });

        return convertView;
    }

    private void checkTheSameJHHCheckBox(_HLJS nowhljs) {
        for (_HLJS hljs : list) {
            if (nowhljs.JHH.equals(hljs.JHH)) {
                hljs.status = nowhljs.status;
            }
        }
        notifyDataSetChanged();
    }

    public void refreshData(List<_HLJS> hljsList) {
        list.clear();
        list.addAll(hljsList);
        notifyDataSetChanged();
    }


    class ViewHolder {
        TextView id_tv;
        TextView id_tv2;
        TextView id_tv3;
        TextView id_tv4;
        CheckBox id_cb;
        TextView id_tv5;
    }

}
