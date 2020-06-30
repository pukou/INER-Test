package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.AdviceDetail;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * 医嘱列表页-医嘱项详情
 *
 * @author hy
 */
public class AdviceDetailAdapter extends BaseAdapter {

    private ArrayList<AdviceDetail> list;
    private LayoutInflater inflater;

    public AdviceDetailAdapter(Context context, ArrayList<AdviceDetail> list) {

        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public AdviceDetail getItem(int arg0) {
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
            convertView = inflater.inflate(R.layout.item_list_advice_detail, parent, false);
            vHolder = new ViewHolder();

            vHolder.ZXZT = (TextView) convertView.findViewById(R.id.ZXZT);
            vHolder.JHSJ = (TextView) convertView.findViewById(R.id.JHSJ);
            vHolder.KSSJ = (TextView) convertView.findViewById(R.id.KSSJ);
            vHolder.JSSJ = (TextView) convertView.findViewById(R.id.JSSJ);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        AdviceDetail vo = list.get(position);
        Date date = DateUtil.getDateCompat(vo.KSSJ);
        String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
        vHolder.KSSJ.setText(StringUtil.getText("开始时间：", dateStr));
        Date date2 = DateUtil.getDateCompat(vo.KSSJ);
        String dateStr2 = DateUtil.format_yyyyMMdd_HHmm.format(date2);
        vHolder.JSSJ.setText(StringUtil.getText("结束时间：", dateStr2));
        Date date3 = DateUtil.getDateCompat(vo.KSSJ);
        String dateStr3 = DateUtil.format_yyyyMMdd_HHmm.format(date3);
        vHolder.JHSJ.setText(StringUtil.getText("计划时间：", dateStr3));

        // 1 已执行 2 正在执行 4 暂停 0 未执行 5 拒绝
        switch (vo.ZXZT) {
            case 0:
                vHolder.ZXZT.setText("未");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
//			vHolder.ZXZT.setVisibility(View.INVISIBLE);
                break;
            case 1:
                vHolder.ZXZT.setText("已");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                break;
            case 2:
                vHolder.ZXZT.setText("→");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                break;
            case 4:
                vHolder.ZXZT.setText("■");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                break;
            case 5:
                vHolder.ZXZT.setText("拒");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                break;
            default:
                vHolder.ZXZT.setVisibility(View.INVISIBLE);
                break;
        }

        return convertView;
    }

    class ViewHolder {

        public TextView ZXZT, JHSJ, KSSJ, JSSJ;

    }

}
