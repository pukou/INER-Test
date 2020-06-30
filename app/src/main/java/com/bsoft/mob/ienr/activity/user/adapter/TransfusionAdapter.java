package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.TransfusionInfoVo;
import com.bsoft.mob.ienr.model.advice.TransfusionVo;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.Date;

public class TransfusionAdapter extends BaseAdapter {

    private ArrayList<TransfusionVo> list;

    private ArrayList<TransfusionInfoVo> infoList;

    private Context mContext;

    // 颜色分组
    private SparseIntArray groupArray = new SparseIntArray();

    // 记录Item是否选中
    private SparseBooleanArray selectedMap = new SparseBooleanArray();

    // 记录checkbox的选中情况
    SparseBooleanArray checkmap = new SparseBooleanArray();

    // 当前选中的输液单号
    public String mSYDH;

    public TransfusionAdapter(Context context, ArrayList<TransfusionVo> _list,
                              ArrayList<TransfusionInfoVo> infoList) {
        this.mContext = context;
        this.list = _list;
        this.infoList = infoList;
        init(infoList, groupArray, selectedMap, checkmap);

    }

    public void init(ArrayList<TransfusionInfoVo> list, SparseIntArray map,
                     SparseBooleanArray selectedMap, SparseBooleanArray checkmap) {

        if (list == null || map == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            selectedMap.put(i, false);
            checkmap.put(i, false);
            if (i == 0) {
                map.put(i, 1);
            } else {
                if (list.get(i).SYDH.equals(list.get(i - 1).SYDH)) {
                    map.put(i, map.get(i - 1));
                } else {
                    if (map.get(i - 1) == 1) {
                        map.put(i, 0);
                    } else {
                        map.put(i, 1);
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return infoList != null ? infoList.size() : 0;
    }

    @Override
    public TransfusionInfoVo getItem(int arg0) {
        return infoList.get(arg0);
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
                    R.layout.item_list_transfusion_tour, parent, false);
            vHolder = new ViewHolder();

            vHolder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox);

            vHolder.view = convertView.findViewById(R.id.tf_main_view);
            vHolder.boxView = convertView.findViewById(R.id.tf_boxview);
            vHolder.arrowImg = (ImageView) convertView
                    .findViewById(R.id.advice_arrow_img);

            vHolder.ZXZT = (TextView) convertView.findViewById(R.id.ZXZT);
            vHolder.YZMC = (TextView) convertView.findViewById(R.id.YZMC);
            vHolder.allName = (TextView) convertView.findViewById(R.id.allName);

            vHolder.JLXX = (TextView) convertView.findViewById(R.id.JLXX);
            vHolder.SLXX = (TextView) convertView.findViewById(R.id.SLXX);
            vHolder.SYSJ = (TextView) convertView.findViewById(R.id.SYSJ);
            vHolder.KSSJ = (TextView) convertView.findViewById(R.id.KSSJ);
            vHolder.JSSJ = (TextView) convertView.findViewById(R.id.JSSJ);

            vHolder.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (vHolder.boxView.getVisibility() == View.GONE) {
                        selectedMap.put(position, true);
                        vHolder.boxView.setVisibility(View.VISIBLE);
                        vHolder.arrowImg
                                .setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    } else {
                        selectedMap.put(position, false);
                        vHolder.arrowImg
                                .setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                        vHolder.boxView.setVisibility(View.GONE);
                    }
                }
            });

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        TransfusionInfoVo vo = infoList.get(position);
        vHolder.checkBox.setTag(vo.SYDH);

        vHolder.YZMC.setText(vo.YZMC);
        vHolder.allName.setText(vo.YZMC);
        vHolder.JLXX.setText(StringUtil.getText("剂量：", vo.JLXX));
        vHolder.SLXX.setText(StringUtil.getText("数量：", vo.SLXX));

        if (groupArray.get(position) == 1) {
            vHolder.view.setBackgroundResource(R.color.classicViewBg);//item
            vHolder.boxView.setBackgroundResource(R.color.classicViewBgDark);//item 展开
        } else {
            vHolder.view.setBackgroundResource(R.color.classicViewBgLight);//item
            vHolder.boxView.setBackgroundResource(R.color.classicViewBgDark);//item 展开
        }

        if (selectedMap.get(position)) {
            vHolder.boxView.setVisibility(View.VISIBLE);
        } else {
            vHolder.boxView.setVisibility(View.GONE);
        }

        final String sydh = vo.SYDH;
        // adapter 复用 view 时，响应OnCheckedChangeListener事件错乱
        vHolder.checkBox.setOnCheckedChangeListener(null);
        vHolder.checkBox.setChecked(checkmap.get(position));
        vHolder.checkBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        resetSelected(position, isChecked, sydh);
                        notifyDataSetChanged();
                    }

                });

        TransfusionVo transfusionVo = getTransfusionVo(sydh);

        if (transfusionVo != null) {

            // 1 已执行 2 正在执行 4 暂停 0 未执行 5 拒绝
            switch (transfusionVo.SYZT) {
                case 0:
                    vHolder.ZXZT.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    vHolder.ZXZT.setText("已");
                    vHolder.ZXZT.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    vHolder.ZXZT.setText("➔");
                    vHolder.ZXZT.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    vHolder.ZXZT.setText("||");
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

            Date date = DateUtil.getDateCompat(transfusionVo.SYSJ);
            String dateStr = DateUtil.format_HHmm.format(date);
            vHolder.SYSJ.setText(StringUtil.getText("计划：", dateStr));

            vHolder.KSSJ.setText(StringUtil.getStringText("开始：",
                    transfusionVo.KSSJ, transfusionVo.KSGH));
            vHolder.JSSJ.setText(StringUtil.getStringText("结束：",
                    transfusionVo.JSSJ, transfusionVo.JSGH));

            vHolder.SYSJ.setVisibility(View.VISIBLE);
            vHolder.KSSJ.setVisibility(View.VISIBLE);
            vHolder.JSSJ.setVisibility(View.VISIBLE);
        } else {
            vHolder.ZXZT.setVisibility(View.INVISIBLE);
            vHolder.SYSJ.setVisibility(View.GONE);
            vHolder.KSSJ.setVisibility(View.GONE);
            vHolder.JSSJ.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 重置选择顶
     *
     * @param position
     * @param isChecked
     * @param sydh
     */
    protected void resetSelected(int position, boolean isChecked, String sydh) {

        if (isChecked) {
            checkmap.clear();
            mSYDH = sydh;
        }
        // 选中之后连选
        checkmap.put(position, isChecked);
        int zh = groupArray.get(position);
        int count = getCount();
        for (int i = position + 1; i < count; i++) {
            if (zh == groupArray.get(i)) {
                checkmap.put(i, isChecked);
            } else {
                break;
            }
        }
        if (position != 0) {
            for (int i = position - 1; i >= 0; i--) {
                if (zh == groupArray.get(i)) {
                    checkmap.put(i, isChecked);
                } else {
                    break;
                }
            }
        }
    }

    private TransfusionVo getTransfusionVo(String sYDH) {

        if (list == null || EmptyTool.isBlank(sYDH)) {
            return null;
        }
        ArrayList<TransfusionVo> localList = list;
        for (TransfusionVo item : localList) {
            if (sYDH.equals(item.SYDH)) {
                return item;
            }
        }
        return null;
    }

    class ViewHolder {

        public TextView SYSJ, KSSJ, JSSJ, YZMC, allName, JLXX, SLXX, ZXZT;
        public View view, boxView;
        // public ImageButton mVisitBtn;
        public ImageView arrowImg;
        public CheckBox checkBox;

    }

    public boolean hasCheckedItm() {

        int size = selectedMap.size();
        for (int i = 0; i < size; i++) {
            boolean checked = checkmap.get(i);
            if (checked)
                return true;
        }
        return false;

    }

}
