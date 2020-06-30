package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.skintest.SickerPersonSkinTest;

import java.util.List;

/**
 * SickerSkinTestHistoryAdapter
 */
public class SickerSkinTestHistoryAdapter extends BaseAdapter {
    private List<SickerPersonSkinTest> list;
    private Context mContext;

    public SickerSkinTestHistoryAdapter(Context context, List<SickerPersonSkinTest> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public SickerPersonSkinTest getItem(int position) {
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
                    R.layout.item_list_skin_test, parent, false);
            vHolder = new ViewHolder();
            vHolder.id_yzmc = (TextView) convertView.findViewById(R.id.id_yzmc);
            vHolder.id_pszt = (TextView) convertView.findViewById(R.id.id_pszt);
            vHolder.id_pslb = (TextView) convertView.findViewById(R.id.id_pslb);
            vHolder.id_pstm = (TextView) convertView.findViewById(R.id.id_pstm);
            vHolder.id_psjg = (TextView) convertView.findViewById(R.id.id_psjg);
            //
            vHolder.id_kssj = (TextView) convertView.findViewById(R.id.id_kssj);
            vHolder.id_ksgh = (TextView) convertView.findViewById(R.id.id_ksgh);
            vHolder.id_jssj = (TextView) convertView.findViewById(R.id.id_jssj);
            vHolder.id_jsgh = (TextView) convertView.findViewById(R.id.id_jsgh);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        SickerPersonSkinTest vo = list.get(position);
      /*  if (vo.dcjCount > 0) {
            vHolder.tv_brxm.setText(vo.BRXM + "(" + vo.dcjCount + ")");
        } else {
            vHolder.tv_brxm.setText(vo.BRXM);
        }*/
        vHolder.id_yzmc.setText(vo.YZMC);
        String psztmc = "";
        //皮试状态:0=未皮试;1=已皮试;2=皮试中
        if ("0".equals(vo.PSZT)) {
            psztmc = "未皮试";
        } else if ("1".equals(vo.PSZT)) {
            psztmc = "已皮试";
        } else if ("2".equals(vo.PSZT)) {
            psztmc = "皮试中";
        }
        vHolder.id_pszt.setText(psztmc);

        vHolder.id_pslb.setText("皮试类别:" + vo.PSLB);
        vHolder.id_pstm.setText("皮试条码:" + vo.PSTM);


        String psjgmc = "";
        // -1、阴性 1、阳性 0、未做 2、续注
        if ("-1".equals(vo.PSJG)) {
            psjgmc = "阴性";
        } else if ("1".equals(vo.PSJG)) {
            psjgmc = "阳性";
        } else if ("2".equals(vo.PSJG)) {
            psjgmc = "续注";
        } else if ("0".equals(vo.PSJG)) {
            psjgmc = "未做";
        }

        vHolder.id_psjg.setText(psjgmc);


        vHolder.id_ksgh.setText("开始:" + (vo.KSR == null ? "" : vo.KSR));
        vHolder.id_kssj.setText(vo.KSSJ == null ? "" : vo.KSSJ);
        //
        vHolder.id_jsgh.setText("结束:" + (vo.JSR == null ? "" : vo.JSR));
        vHolder.id_jssj.setText(vo.JSSJ == null ? "" : vo.JSSJ);
        //未皮试
        if ("0".equals(vo.PSZT)) {
            vHolder.id_psjg.setVisibility(View.INVISIBLE);
            vHolder.id_ksgh.setVisibility(View.GONE);
            vHolder.id_kssj.setVisibility(View.GONE);
            vHolder.id_jsgh.setVisibility(View.GONE);
            vHolder.id_jssj.setVisibility(View.GONE);
        } else if ("2".equals(vo.PSZT)) {
            //    皮试中
            vHolder.id_psjg.setVisibility(View.INVISIBLE);
            vHolder.id_ksgh.setVisibility(View.VISIBLE);
            vHolder.id_kssj.setVisibility(View.VISIBLE);
            vHolder.id_jsgh.setVisibility(View.GONE);
            vHolder.id_jssj.setVisibility(View.GONE);
        } else if ("1".equals(vo.PSZT)) {
            //   已皮试
            vHolder.id_psjg.setVisibility(View.VISIBLE);
            vHolder.id_ksgh.setVisibility(View.VISIBLE);
            vHolder.id_kssj.setVisibility(View.VISIBLE);
            vHolder.id_jsgh.setVisibility(View.VISIBLE);
            vHolder.id_jssj.setVisibility(View.VISIBLE);
        }


     /*   boolean isZC = false;
        if (vo.personalBloodSugarList != null) {
            for (PersonBloodSugar personBloodSugar : vo.personalBloodSugarList) {
                if (!TextUtils.isEmpty(personBloodSugar.YSZT)&&personBloodSugar.YSZT.contains("自测")) {
                    isZC = true;
                    break;
                }
            }
        }
        if (isZC) {
            vHolder.tv_brxm.setTextColor(Color.RED);
            vHolder.tv_brch.setTextColor(Color.RED);
        } else {
            vHolder.tv_brxm.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
            vHolder.tv_brch.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
        }*/
//        vHolder.tv_brxm.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
//        vHolder.tv_brch.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));

        return convertView;
    }

    class ViewHolder {
        public TextView id_yzmc;
        public TextView id_pszt;
        public TextView id_pslb;
        public TextView id_pstm;
        public TextView id_psjg;
        public TextView id_kssj;
        public TextView id_jssj;
        public TextView id_ksgh;
        public TextView id_jsgh;
    }


    public int getPersonPostion(String zYH) {

        if (list == null) {
            return -1;
        }
        int i = 0;
        for (SickerPersonSkinTest sickerPersonSkinTest : list) {
            if (sickerPersonSkinTest.ZYH.equals(zYH)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
