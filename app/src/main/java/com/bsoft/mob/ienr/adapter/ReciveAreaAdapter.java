package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.Vector;


public class ReciveAreaAdapter extends BaseAdapter {

    private Vector<AreaVo> list;

    private Context mContext;

    public String mCurArea;

    public ReciveAreaAdapter(Context context, Vector<AreaVo> _list, String mCurArea) {
        this.list = _list;
        this.mContext = context;
        this.mCurArea = mCurArea;
    }


    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public AreaVo getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.simple_spinner_dropdown_item, parent,false);
            vHolder = new ViewHolder();
            vHolder.nameView = (TextView) convertView
                    .findViewById(R.id.text1);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        AreaVo vo = list.get(position);
        vHolder.nameView.setText(vo.KSMC);
        if (!EmptyTool.isBlank(vo.KSDM) && vo.KSDM.equals(mCurArea)) {
            TextViewCompat.setTextAppearance(vHolder.nameView, R.style.ClassicTextAppearanceSecondaryColored);
        } else {
            TextViewCompat.setTextAppearance(vHolder.nameView, R.style.ClassicTextAppearanceSecondary);
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.simple_spinner_item,parent,false);
            vHolder = new ViewHolder();
            vHolder.nameView = (TextView) convertView
                    .findViewById(R.id.text1);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        AreaVo vo = list.get(position);
        vHolder.nameView.setText(vo.KSMC);
        if (!EmptyTool.isBlank(vo.KSDM) && vo.KSDM.equals(mCurArea)) {
            TextViewCompat.setTextAppearance(vHolder.nameView, R.style.ClassicTextAppearanceSecondaryColored);
        } else {
            TextViewCompat.setTextAppearance(vHolder.nameView, R.style.ClassicTextAppearanceSecondary);
        }
        convertView.setBackgroundResource(R.color.transparent);
        return convertView;
    }

    class ViewHolder {
        public TextView nameView;
    }

}
