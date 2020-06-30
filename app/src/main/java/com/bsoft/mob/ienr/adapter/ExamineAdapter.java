package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.inspection.ExamineVo;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @检查适配器
 */
public class ExamineAdapter extends BaseAdapter {

    private ArrayList<ExamineVo> list;
    private LayoutInflater inflater;

    public ExamineAdapter(Context context) {
        this.list = new ArrayList<ExamineVo>();
        inflater = LayoutInflater.from(context);
    }

    public void addData(ArrayList<ExamineVo> _list) {
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
    public ExamineVo getItem(int arg0) {
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
            convertView = inflater.inflate(R.layout.item_list_text_four,  parent,false);
            vHolder = new ViewHolder();

            vHolder.JCMC = (TextView) convertView.findViewById(R.id.JCMC);
            vHolder.JCSJ = (TextView) convertView.findViewById(R.id.JCSJ);
            vHolder.JCYS = (TextView) convertView.findViewById(R.id.JCYS);
            vHolder.BWMS = (TextView) convertView.findViewById(R.id.BWMS);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        ExamineVo vo = list.get(position);

        vHolder.JCMC.setText(StringUtil.getText("检查名称：", vo.JCMC));
        vHolder.JCSJ.setText(StringUtil.getText("报告时间：", TextUtils.isEmpty(vo.BGSJ)?"": DateUtil.dateToString(DateUtil.getDateCompat(vo.BGSJ))));
        vHolder.JCYS.setText(StringUtil.getText("报告医生：", vo.BGYSXM));
        vHolder.BWMS.setText(StringUtil.getText("检查部位：", vo.BWMS));

        return convertView;
    }

    class ViewHolder {
        public TextView JCMC, JCSJ, JCYS, BWMS;
    }

}
