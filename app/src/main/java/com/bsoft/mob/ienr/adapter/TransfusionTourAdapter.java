package com.bsoft.mob.ienr.adapter;

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
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.AdvicePlanVo;
import com.bsoft.mob.ienr.util.StringUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @医嘱计划适配器 只能选中组号和时间编号一样的
 */
@Deprecated
public class TransfusionTourAdapter extends BaseAdapter {

    private ArrayList<AdvicePlanVo> list;
    private LayoutInflater inflater;
    // 记录Item是否选中
    //HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    SparseBooleanArray map = new SparseBooleanArray();

    // 记录医嘱组号标示
    //HashMap<Integer, Integer> zhmap = new HashMap<Integer, Integer>();
    SparseIntArray zhmap = new SparseIntArray();
    // 记录checkbox的选中情况
    //HashMap<Integer, Boolean> checkmap = new HashMap<Integer, Boolean>();
    SparseBooleanArray checkmap = new SparseBooleanArray();

    public TransfusionTourAdapter(Context context) {
        this.list = new ArrayList<AdvicePlanVo>();
        inflater = LayoutInflater.from(context);
    }

    // 选中的jhh-计划号
    public String getJHHValue() {
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (checkmap.get(i)) {
                    return list.get(i).JHH;
                }
            }
        }
        return null;
    }

    // 选中的sydh-输液单号
    public String getSYDHValue() {
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (checkmap.get(i)) {
                    return list.get(i).QRDH;
                }
            }
        }
        return null;
    }

    public void addData(ArrayList<AdvicePlanVo> _list) {
        this.list.addAll(_list);
        for (int i = 0; i < list.size(); i++) {
            map.put(i, false);
            checkmap.put(i, false);
            if (i == 0) {
                zhmap.put(i, 1);
            } else {
                if (list.get(i).YZZH.equals(list.get(i - 1).YZZH)
                        && list.get(i).SJBH.equals(list.get(i - 1).SJBH)) {
                    zhmap.put(i, zhmap.get(i - 1));
                } else {
                    if (zhmap.get(i - 1) == 1) {
                        zhmap.put(i, 0);
                    } else {
                        zhmap.put(i, 1);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    // 清空checkbox
    public void initCheckBox() {
        for (int i = 0; i < list.size(); i++) {
            checkmap.put(i, false);
        }
    }

    public void clearData() {
        list.clear();
        map.clear();
        checkmap.clear();
        zhmap.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AdvicePlanVo getItem(int arg0) {
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
            convertView = inflater.inflate(R.layout.item_list_drugs_advice, parent,false);
            vHolder = new ViewHolder();

            vHolder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox);
            vHolder.name = (TextView) convertView.findViewById(R.id.name);
            vHolder.time = (TextView) convertView.findViewById(R.id.time);
            vHolder.view = convertView.findViewById(R.id.view);
            vHolder.boxView = convertView.findViewById(R.id.boxView);
            vHolder.allName = (TextView) convertView.findViewById(R.id.allName);
            vHolder.JLXX = (TextView) convertView.findViewById(R.id.JLXX);
            vHolder.SLXX = (TextView) convertView.findViewById(R.id.SLXX);
            vHolder.ZXZT = (TextView) convertView.findViewById(R.id.ZXZT);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        AdvicePlanVo vo = list.get(position);

        vHolder.name.setText(StringUtil.getStringLength(vo.YZMC, 11));
        vHolder.time.setText(vo.SJMC);
        if (vo.YZMC.length() > 11) {
            vHolder.allName.setText(vo.YZMC);
            vHolder.allName.setVisibility(View.VISIBLE);
        } else {
            vHolder.allName.setVisibility(View.GONE);
        }

        vHolder.JLXX.setText(StringUtil.getText("剂量：", vo.JLXX));
        vHolder.SLXX.setText(StringUtil.getText("数量：", vo.SLXX));

        if (map.get(position)) {
            vHolder.boxView.setVisibility(View.VISIBLE);
        } else {
            vHolder.boxView.setVisibility(View.GONE);
        }
        if (zhmap.get(position) == 1) {
            vHolder.view.setBackgroundResource(R.color.classicViewBg);
        } else {
            vHolder.view.setBackgroundResource(R.color.white);
        }

        vHolder.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (vHolder.boxView.getVisibility() == View.GONE) {
                    map.put(position, true);
                    vHolder.boxView.setVisibility(View.VISIBLE);
                } else {
                    map.put(position, false);
                    vHolder.boxView.setVisibility(View.GONE);
                }
            }
        });
        // adapter 复用 view 时，响应OnCheckedChangeListener事件错乱
        vHolder.checkBox.setOnCheckedChangeListener(null);
        vHolder.checkBox.setChecked(checkmap.get(position));
        vHolder.checkBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        // 选中之后连选
                        if (arg1) {
                            initCheckBox();
                            checkmap.put(position, true);
                            int zh = zhmap.get(position);

                            for (int i = position + 1; i <= getCount() - 1; i++) {
                                if (zh == zhmap.get(i)) {
                                    checkmap.put(i, true);
                                } else {
                                    break;
                                }
                            }
                            if (position != 0) {
                                for (int i = position - 1; i >= 0; i--) {
                                    if (zh == zhmap.get(i)) {
                                        checkmap.put(i, true);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        } else {
                            checkmap.put(position, false);
                        }
                        notifyDataSetChanged();
                    }
                });
        // 1 已执行 2 正在执行 4 暂停 0 未执行 5 拒绝
        switch (vo.ZXZT) {
            case 0:
                vHolder.ZXZT.setVisibility(View.INVISIBLE);
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
        public CheckBox checkBox;
        public View view, boxView;
        public TextView name, time, allName, JLXX, SLXX, ZXZT;
    }

}
