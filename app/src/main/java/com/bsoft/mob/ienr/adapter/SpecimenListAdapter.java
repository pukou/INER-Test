package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.inspection.SpecimenVo;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @标本适配器
 */
public class SpecimenListAdapter extends BaseAdapter {

    private ArrayList<SpecimenVo> list;
    private String brxxMy;

    private Context mContext;

    public SpecimenListAdapter(Context context, ArrayList<SpecimenVo> list) {
        this(context,list,null);
    }
    public SpecimenListAdapter(Context context, ArrayList<SpecimenVo> list,String brxxMy) {

        this.brxxMy = brxxMy;
        //
        this.list = list;
        if (list == null) {
            this.list = new ArrayList<SpecimenVo>();
        }

        this.mContext = context;

    }

    public void addData(ArrayList<SpecimenVo> _list) {

        if (_list == null || _list.size() < 1) {
            return;
        }
        this.list.addAll(_list);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public SpecimenVo contain(String tm) {
        for (SpecimenVo vo : list) {
            if (tm.equals(vo.TMBH)) {
                return vo;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SpecimenVo getItem(int arg0) {
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
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_six_vert_primary, parent, false);
            vHolder = new ViewHolder();

            vHolder.XMMC = (TextView) convertView.findViewById(R.id.XMMC);
            vHolder.KDSJ = (TextView) convertView.findViewById(R.id.TZNR);
            vHolder.TMBH = (TextView) convertView.findViewById(R.id.CJSJ);
            vHolder.FFZT = (TextView) convertView.findViewById(R.id.FCBZ);
            vHolder.BZXX = (TextView) convertView.findViewById(R.id.XMXB);
            vHolder.CYRQ = (TextView) convertView.findViewById(R.id.XMXB2);
            vHolder.CYR = (TextView) convertView.findViewById(R.id.XMXB3);
            vHolder.BRXX = (TextView) convertView.findViewById(R.id.XMXB4);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        SpecimenVo vo = list.get(position);

        vHolder.XMMC.setText(StringUtil.getText("项目名称:", vo.XMMC));
        Date date = DateUtil.getDateCompat(vo.KDSJ);
        String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
        vHolder.KDSJ.setText(StringUtil.getText("开单时间:",dateStr));
        vHolder.TMBH.setText(StringUtil.getText("条码编号:", vo.TMBH));
       /* //福建协和客户化：试管颜色跟备注信息组合显示
        String bzxx = EmptyTool.isBlank(vo.BZXX) ? vo.SGYS : vo.SGYS + "||" + vo.BZXX;
        vHolder.BZXX.setText(StringUtil.getText("备注信息:", bzxx));*/
        vHolder.BZXX.setText(StringUtil.getText("备注信息:", vo.BZXX));
        if (EmptyTool.isBlank(vo.CYRQ)) {
            vHolder.CYRQ.setVisibility(View.GONE);
        } else {
            vHolder.CYRQ.setVisibility(View.VISIBLE);
            //fixmee
             date = DateUtil.getDateCompat(vo.CYRQ);
             dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
            vHolder.CYRQ.setText(StringUtil.getText("采集时间:",dateStr));

        }
        if (EmptyTool.isBlank(vo.CYR)) {
            vHolder.CYR.setVisibility(View.GONE);
        } else {
            vHolder.CYR.setVisibility(View.VISIBLE);
            vHolder.CYR.setText(StringUtil.getText("采集人:", vo.CYR));

        }
        if (!TextUtils.isEmpty(brxxMy)){
            vHolder.BRXX.setVisibility(View.VISIBLE);
            vHolder.BRXX.setText(brxxMy);
        }else{
            vHolder.BRXX.setVisibility(View.GONE);
        }

        String stateStr = null;
        if (vo.FFZT != -1) {
            stateStr = vo.FFZT == 0 ? "待发放" : "待采集";
        } else {
            stateStr = vo.CYBZ == 0 ? "待采集" : "已采集";
        }

        vHolder.FFZT.setText(StringUtil.getText("当前状态:", stateStr));

        setXMMCColor(vHolder.XMMC, vo.SGYS);

        //福建协和客户化：试管颜色不是标准rgb色导致
        //setXMMCColor(vHolder.XMMC, vo.SGYS);

        return convertView;
    }

    /**
     * 设置项目名称颜色值
     *
     * @param xMMC
     * @param sGYS
     */
    private void setXMMCColor(TextView xMMC, String sGYS) {

        if (xMMC == null || EmptyTool.isBlank(sGYS)) {
            return;
        }
        int defaultColor = Color.BLACK;

        try {
            String[] rgbs = sGYS.split(",");
            //defaultColor = Color.rgb(Integer.valueOf(rgbs[0]), Integer.valueOf(rgbs[1]), Integer.valueOf(rgbs[1]));
            if(rgbs.length >= 3) {
                defaultColor = Color.rgb(Integer.valueOf(rgbs[0]), Integer.valueOf(rgbs[1]), Integer.valueOf(rgbs[2]));
            }
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }
        xMMC.setTextColor(defaultColor);

    }

    class ViewHolder {

        public TextView XMMC;

        public TextView KDSJ;

        public TextView TMBH;

        public TextView FFZT;

        public TextView BZXX;

        public TextView CYRQ;
        public TextView CYR;
        public TextView BRXX;

    }

}
