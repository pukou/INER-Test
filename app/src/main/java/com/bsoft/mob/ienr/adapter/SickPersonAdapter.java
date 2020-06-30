package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.kernel.State;
import com.bsoft.mob.ienr.model.risk.RecondBean;
import com.bsoft.mob.ienr.model.risk.ZKBean;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;
import com.bsoft.mob.ienr.view.horizontallistview.HorizontalListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40 病人列表适配器
 */
public class SickPersonAdapter extends BaseAdapter {

    private List<SickPersonVo> mList;
    private int itemLayoutResId;

    public SickPersonAdapter(ArrayList<SickPersonVo> list, int itemLayoutResId) {
        this.mList = list;
        this.itemLayoutResId = itemLayoutResId;
    }

    /*
     * public void addData(ArrayList<SickPersonVo> _list) {
     * this.list.addAll(_list); notifyDataSetChanged(); }
     */

    /*
     * public void clearData() { this.list.clear(); notifyDataSetChanged(); }
     */

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

  /*  private ArrayList<SickPersonVo> getItems() {

        if (levelDrawable == R.drawable.img_menu_level_max) {
            return visitList(0);
        } else if (levelDrawable == R.drawable.img_menu_level_1) {
            return visitList(1);
        } else if (levelDrawable == R.drawable.img_menu_level_2) {
            return visitList(2);
        } else if (levelDrawable == R.drawable.img_menu_level_3) {
            return visitList(3);
        }
        return mAlllist;

    }*/

  /*  private ArrayList<SickPersonVo> visitList(int HLJB) {
        ArrayList<SickPersonVo> result = new ArrayList<SickPersonVo>();
        if (mAlllist == null) {
            return result;
        }
        for (SickPersonVo person : mAlllist) {
            if (person.HLJB == HLJB) {
                result.add(person);
            }
        }
        return result;
    }*/

    @Override
    public SickPersonVo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResId, parent, false);
            //convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sickperson_grid_item, parent, false);
            vHolder = new ViewHolder();

            vHolder.tv_brxm = (TextView) convertView
                    .findViewById(R.id.patient_list_brxm);
            /*
            升级编号【56010014】============================================= start
今日出院的病人，系统会提早出院，但是该病人当天的药品需要执行完，目前PDA无法处理该类病人的医嘱执行。（需要将当日出院的病人也显示在病人类别中，并标识）
			================= Classichu 2017/10/11 16:25
			1.
			*/
            vHolder.id_tv_today_out = (TextView) convertView
                    .findViewById(R.id.id_tv_today_out);
            /* =============================================================== end */
            vHolder.tv_hljb = (TextView) convertView
                    .findViewById(R.id.patient_list_hljb);
            /*vHolder.tv_hljb = (ImageView) convertView
                    .findViewById(R.id.patient_list_hljb);*/
            vHolder.tv_brch = (TextView) convertView
                    .findViewById(R.id.patient_list_ch);
            vHolder.tv_brnl = (TextView) convertView
                    .findViewById(R.id.patient_list_nl);
            vHolder.tv_brxb = (TextView) convertView
                    .findViewById(R.id.patient_list_brxb);

            vHolder.id_tv_bah = (TextView) convertView
                    .findViewById(R.id.id_tv_bah);
            // 中文加粗
            vHolder.id_tv_bah.getPaint().setFakeBoldText(true);
            //

            convertView.setTag(vHolder);
            vHolder.id_tv_xx = (TextView) convertView
                    .findViewById(R.id.id_tv_xx);
            vHolder.id_tv_zt_bi = (TextView) convertView.findViewById(R.id.id_tv_zt_bi);
            vHolder.id_tv_zt_zhui = (TextView) convertView.findViewById(R.id.id_tv_zt_zhui);
            vHolder.id_tv_zt_vte = (TextView) convertView.findViewById(R.id.id_tv_zt_vte);
            vHolder.id_tv_zt_ya = (TextView) convertView.findViewById(R.id.id_tv_zt_ya);

            vHolder.id_tv_lclj = (TextView) convertView
                    .findViewById(R.id.id_tv_lclj);

            vHolder.id_tv_zd = (TextView) convertView
                    .findViewById(R.id.id_tv_zd);
            vHolder.id_tv_ys = (TextView) convertView
                    .findViewById(R.id.id_tv_ys);


            vHolder.id_tv_day_in = (TextView) convertView
                    .findViewById(R.id.id_tv_day_in);

            vHolder.statelist = (HorizontalListView) convertView
                    .findViewById(R.id.statelist);
            // 中文加粗
            TextPaint tp = vHolder.tv_brxm.getPaint();
            tp.setFakeBoldText(true);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        Context context = parent.getContext();
        SickPersonVo vo = mList.get(position);

        vHolder.tv_brxm.setText(vo.BRXM);
        if (vo.hasGMYP) {
            vHolder.tv_brxm.setTextColor(Color.RED);
        } else {
            vHolder.tv_brxm.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
        }
        /*
        升级编号【56010014】============================================= start
今日出院的病人，系统会提早出院，但是该病人当天的药品需要执行完，目前PDA无法处理该类病人的医嘱执行。（需要将当日出院的病人也显示在病人类别中，并标识）
		================= Classichu 2017/10/11 16:24
		2.
		*/
        if ("1".equals(vo.CYPB)) {
            vHolder.id_tv_today_out.setText("[今日出院]");
            vHolder.id_tv_today_out.setVisibility(View.VISIBLE);
        } else {
            vHolder.id_tv_today_out.setVisibility(View.GONE);
        }
        /* =============================================================== end */
        vHolder.tv_brxb.setText(vo.BRXB == 1 ? "男" : "女");
//        vHolder.tv_hljb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        switch (vo.HLJB) {
            case 0:
//              vHolder.tv_hljb.setImageResource(R.drawable.img_level_max);
                vHolder.tv_hljb.setText("特级");
                vHolder.tv_hljb.setTextColor(ContextCompat.getColor(context, R.color.white));
                vHolder.tv_hljb.setBackgroundResource(R.drawable.shape_classic_bg_view_max);
                break;
            case 1:
//                vHolder.tv_hljb.setImageResource(R.drawable.img_level_1);
                vHolder.tv_hljb.setText("Ⅰ级");
                vHolder.tv_hljb.setTextColor(ContextCompat.getColor(context, R.color.white));
                vHolder.tv_hljb.setBackgroundResource(R.drawable.shape_classic_bg_view_1);
                break;
            case 2:
//                vHolder.tv_hljb.setImageResource(R.drawable.img_level_2);
                vHolder.tv_hljb.setText("Ⅱ级");
                vHolder.tv_hljb.setTextColor(ContextCompat.getColor(context, R.color.white));
                vHolder.tv_hljb.setBackgroundResource(R.drawable.shape_classic_bg_view_2);
                break;
            case 3:
//                vHolder.tv_hljb.setImageResource(R.drawable.img_level_3);
                vHolder.tv_hljb.setText("Ⅲ级");
                vHolder.tv_hljb.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                vHolder.tv_hljb.setBackgroundResource(R.drawable.shape_classic_bg_view_border_2_black);
                break;
            default:
//                vHolder.tv_hljb.setImageResource(R.drawable.img_level_0);
                vHolder.tv_hljb.setText("—");
                vHolder.tv_hljb.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                vHolder.tv_hljb.setBackgroundResource(R.color.transparent);
        }
        vHolder.tv_brch.setText(EmptyTool.isBlank(vo.BRCH) ? "无" : vo.BRCH + "床");
        // 中文加粗
        TextPaint tp = vHolder.tv_brch.getPaint();
        tp.setFakeBoldText(true);
        vHolder.tv_brnl.setText(EmptyTool.isBlank(vo.BRNL) ? "无" : vo.BRNL + "");
        if (vo.state != null && vo.state.size() > 0) {
            vHolder.statelist.setAdapter(new StateAdapter(vo.state));
        } else {
            vHolder.statelist.setAdapter(new StateAdapter(new ArrayList<State>()));
        }
//        vHolder.id_tv_bah.setText(EmptyTool.isBlank(vo.ZYHM) ? "无" : "病案号:" + vo.ZYHM);
        vHolder.id_tv_bah.setText(EmptyTool.isBlank(vo.ZYHM) ? "" : vo.ZYHM);
        if (TextUtils.isEmpty(vo.BRXX)) {
            vHolder.id_tv_xx.setVisibility(View.GONE);
        } else {
            vHolder.id_tv_xx.setText(vo.BRXX);
            vHolder.id_tv_xx.setVisibility(View.VISIBLE);
        }

        String lclj = vo.LCLJ;
        if ("1".equals(lclj)) {
            //在临床路径
            lclj = "★";
        } else if ("2".equals(lclj)) {
            //已出临床路径
            lclj = "☆";
        } else {
            //无
            lclj = "";
        }
        vHolder.id_tv_lclj.setText(EmptyTool.isBlank(lclj) ? "" : lclj);
        //to
//        vHolder.id_tv_zd.setText(EmptyTool.isBlank(vo.BRZDMC)?"":vo.BRZDLB+":"+vo.BRZDMC);
//        vHolder.id_tv_zd.setText(EmptyTool.isBlank(vo.BRZDMC) ? "诊断:" : "诊断:" + vo.BRZDMC);
        vHolder.id_tv_zd.setText(EmptyTool.isBlank(vo.BRZDMC) ? "" : vo.BRZDMC);
        String ryrq = vo.RYRQ;
        if (!EmptyTool.isBlank(ryrq)) {
            ryrq = DateTimeTool.dateTime2Custom(ryrq, "MM-dd");
        }
        vHolder.id_tv_day_in.setText("入院:" + ryrq);
//        vHolder.id_tv_ys.setText("主诊医生:"+vo.ZZYS);
        vHolder.id_tv_ys.setText(vo.ZZYS);
        //1 YA 2 ZHUI 3 VTE 4 BI
        int bi = 0;
        int ya = 0;
        int zhui = 0;
        int vte = 0;
        if (vo.recondBeanList4Sicker != null && vo.recondBeanList4Sicker.size() > 0) {
            for (int i = 0; i < vo.recondBeanList4Sicker.size(); i++) {
                RecondBean recondBean = vo.recondBeanList4Sicker.get(i);
                switch (recondBean.PGLX) {
                    case "1":
                        ya = TextUtils.isEmpty(recondBean.PGZF) ? 0 : Integer.valueOf(recondBean.PGZF);
                        break;
                    case "2":
                        zhui = TextUtils.isEmpty(recondBean.PGZF) ? 0 : Integer.valueOf(recondBean.PGZF);
                        break;
                    case "7":
                        vte = TextUtils.isEmpty(recondBean.PGZF) ? 0 : Integer.valueOf(recondBean.PGZF);
                        break;
                    case "8":
                        bi = TextUtils.isEmpty(recondBean.PGZF) ? 0 : Integer.valueOf(recondBean.PGZF);
                        break;
                    default:
                }
            }

        }
        boolean ya_wx = false;
        boolean bi_wx = false;
        boolean zhui_wx = false;
        boolean vte_wx = false;
        //
        boolean ya_tx = false;
        boolean bi_tx = false;
        boolean zhui_tx = false;
        boolean vte_tx = false;

        if (vo.zKbeanList4Sicker != null && vo.zKbeanList4Sicker.size() > 0) {
            for (int i = 0; i < vo.zKbeanList4Sicker.size(); i++) {
                ZKBean zkBean = vo.zKbeanList4Sicker.get(i);
                //
                boolean txComm = false;
                if (!TextUtils.isEmpty(zkBean.TXRQ)) {
                    Date date = DateUtil.getDateCompat(zkBean.TXRQ);
                    String dateStr = DateUtil.format_yyyyMMdd_HHmmss.format(date);
                    txComm = DateTimeTool.compareTo(dateStr, DateTimeHelper.getServerDateTime()) <= 0;
                }
                switch (zkBean.PGLX) {
                    case "1":
//                        ya_tx = TextUtils.isEmpty(zkBean.TXRQ) ? false : DateUtil.getBetween(zkBean.TXRQ) <= 0;
                        ya_tx = txComm;
                        // 1、压疮：≤14 分需7天评估一次，PDA显示红框。
                        ya_wx = ya <= 14;
                        break;
                    case "2":
                        zhui_tx = txComm;
//                        2、坠床：≥4 分需7天评估一次，PDA显示红框。
                        zhui_wx = zhui >= 4;
                        break;
                    case "7":
                        vte_tx = txComm;
//                        4、ⅤTE≥2 分，同上。
                        vte_wx = vte >= 2;
                        break;
                    case "8":
                        bi_tx = txComm;
//                        3、BI：≤40 分，同上。
                        bi_wx = bi <= 40;
                        break;
                    default:
                }
            }
        }
        vHolder.id_tv_zt_bi.setText("BI:" + bi);
        vHolder.id_tv_zt_bi.setBackgroundResource(backBackgroundResId(bi_wx, bi_tx));
        vHolder.id_tv_zt_bi.setTextColor(backTextColor(bi_wx, bi_tx, context));
        vHolder.id_tv_zt_zhui.setText("坠床:" + zhui);
        vHolder.id_tv_zt_zhui.setBackgroundResource(backBackgroundResId(zhui_wx, zhui_tx));
        vHolder.id_tv_zt_zhui.setTextColor(backTextColor(zhui_wx, zhui_tx, context));
        vHolder.id_tv_zt_ya.setText("压疮:" + ya);
        vHolder.id_tv_zt_ya.setBackgroundResource(backBackgroundResId(ya_wx, ya_tx));
        vHolder.id_tv_zt_ya.setTextColor(backTextColor(ya_wx, ya_tx, context));
        vHolder.id_tv_zt_vte.setText("VTE:" + vte);
        vHolder.id_tv_zt_vte.setBackgroundResource(backBackgroundResId(vte_wx, vte_tx));
        vHolder.id_tv_zt_vte.setTextColor(backTextColor(vte_wx, vte_tx, context));
        return convertView;
    }

    private int backTextColor(boolean wx, boolean tx, Context context) {
        // wx 红字  tx 亮底色
        // wx 黑字  tx 无底色
        if (wx && tx) {
//            红字-亮底色
            return Color.RED;
        }
        if (wx) {
            //红字-无底色
            return Color.RED;
        }
        if (tx) {
            //黑字-亮底色
            return ContextCompat.getColor(context, R.color.textColorPrimary);
        }
        //黑字-无底色
        return ContextCompat.getColor(context, R.color.textColorPrimary);
    }

    private int backBackgroundResId(boolean wx, boolean tx) {
        // wx 红框  tx 亮底色
        // wx 黑框  tx 无底色
        if (wx && tx) {
//            红框-亮底色
            return R.drawable.shape_classic_bg_view_1_1;
        }
        if (wx) {
            //红框-无底色
            return R.drawable.shape_classic_bg_view_border_1;
        }
        if (tx) {
            //黑框-亮底色
            return R.drawable.shape_classic_bg_view_2_2;
        }
        //黑框-无底色
        return R.drawable.shape_classic_bg_view_border_2_black;
    }

    class ViewHolder {
        //        public ImageView iv_hljb;
        public TextView tv_hljb;
        public TextView tv_brxm;
        /*
        升级编号【】============================================= start
今日出院的病人，系统会提早出院，但是该病人当天的药品需要执行完，目前PDA无法处理该类病人的医嘱执行。（需要将当日出院的病人也显示在病人类别中，并标识）
        ================= Classichu 2017/10/11 16:25
        3.
        */
        public TextView id_tv_today_out;
        /* =============================================================== end */
        public TextView tv_brch;
        public TextView tv_brnl;

        public TextView tv_brxb;
        public HorizontalListView statelist;
        public TextView id_tv_bah;
        public TextView id_tv_xx;
        public TextView id_tv_zt_bi;
        public TextView id_tv_zt_ya;
        public TextView id_tv_zt_zhui;
        public TextView id_tv_zt_vte;
        public TextView id_tv_lclj;
        public TextView id_tv_zd;
        public TextView id_tv_ys;
        public TextView id_tv_day_in;
    }

    public void refreshData(List<SickPersonVo> sickPersonVoArrayList) {
        mList.clear();
        mList.addAll(sickPersonVoArrayList);
        notifyDataSetChanged();
    }


    class StateAdapter extends BaseAdapter {
        ArrayList<State> mStateList;

        public StateAdapter(ArrayList<State> list) {
            mStateList = list;
        }

        @Override
        public int getCount() {
            return mStateList.size();
        }

        @Override
        public State getItem(int position) {
            return mStateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            final ViewGroup parent) {
            StateHolder sHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_root_image, parent, false);
                sHolder = new StateHolder();
                sHolder.iv_state = (ImageView) convertView
                        .findViewById(R.id.id_iv);
                convertView.setTag(sHolder);
            } else {
                sHolder = (StateHolder) convertView.getTag();
            }
            sHolder.iv_state.setImageResource(AppApplication.getInstance().getState(mStateList.get(position).ztlx));
            sHolder.iv_state.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    BSToast.showToast(parent.getContext(), mStateList.get(position).ztnr,
                            BSToast.LENGTH_LONG);
                }
            });
            return convertView;
        }
    }

    class StateHolder {
        ImageView iv_state;
    }
}
