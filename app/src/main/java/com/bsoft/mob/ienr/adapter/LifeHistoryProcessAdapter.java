package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignDoubleCheckCoolingMeasure;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @类说明 降温处理
 */
public class LifeHistoryProcessAdapter extends BaseAdapter {

    private ArrayList<LifeSignDoubleCheckCoolingMeasure> list;
    private LayoutInflater inflater;
    public int choseId = -1;

    public LifeHistoryProcessAdapter(Context context) {
        this.list = new ArrayList<LifeSignDoubleCheckCoolingMeasure>();
        inflater = LayoutInflater.from(context);
    }

    public void addData(ArrayList<LifeSignDoubleCheckCoolingMeasure> _list) {
        this.list = _list;
//		if (choseId != -1) {
//			changeStatue(choseId);
//		}
        notifyDataSetChanged();
    }


    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public String getChoseValue() {
        // 未选择
        if (choseId != -1) {
            LifeSignDoubleCheckCoolingMeasure vo = new LifeSignDoubleCheckCoolingMeasure();
            vo.DMSB = choseId;
            return list.get(list.indexOf(vo)).DMMC;
        }
        return null;
    }

    public void clearChose() {
        choseId = -1;
    }

    public void changeStatue(int choseId) {
        this.choseId = choseId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LifeSignDoubleCheckCoolingMeasure getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_text_one_primary, parent,false);
            vHolder = new ViewHolder();

            vHolder.nameView = (TextView) convertView
                    .findViewById(R.id.name);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        LifeSignDoubleCheckCoolingMeasure vo = list.get(position);
        vHolder.nameView.setText(vo.DMMC);

        if (choseId == vo.DMSB) {
            convertView.setBackgroundResource(R.color.classicItemBgSelected);
        } else {
            convertView.setBackgroundResource(R.color.classicItemBg);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView nameView;
    }

}
