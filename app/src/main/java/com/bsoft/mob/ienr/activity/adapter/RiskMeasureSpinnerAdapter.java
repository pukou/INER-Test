package com.bsoft.mob.ienr.activity.adapter;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.ReciveAreaAdapter;
import com.bsoft.mob.ienr.model.kernel.Agency;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.model.risk.RiskEvaluate;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.List;

public class RiskMeasureSpinnerAdapter extends BaseAdapter {

    private List<RiskEvaluate> list;

    public RiskMeasureSpinnerAdapter(List<RiskEvaluate> list) {
        this.list = list;
    }

    @Override
    public int getCount() {

        return list != null ? list.size() : 0;
    }

    @Override
    public RiskEvaluate getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_spinner_dropdown_item, parent, false);
            ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
        }
        ((TextView) convertView).setText(list.get(position).PJMS);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    android.R.layout.simple_spinner_item, parent, false);
            ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
        }
        ((TextView) convertView).setText(list.get(position).PJMS);
        return convertView;
    }

}
