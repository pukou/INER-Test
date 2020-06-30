package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignDoubleCheckHistoryDataItem;
import com.bsoft.mob.ienr.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @采集历史
 */
public class LifeHistoryAdapter extends BaseAdapter {

    private ArrayList<LifeSignDoubleCheckHistoryDataItem> list;
    private LayoutInflater inflater;
    public int choseId = -1;

    public LifeHistoryAdapter(Context context) {
        this.list = new ArrayList<LifeSignDoubleCheckHistoryDataItem>();
        inflater = LayoutInflater.from(context);
    }

    public void addData(ArrayList<LifeSignDoubleCheckHistoryDataItem> _list) {
        this.list = _list;
        notifyDataSetChanged();
    }

    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public void clearChose() {
        choseId = -1;
    }

    public int getChoseId() {
        // 还需未选择
        return choseId;
    }

    public String getChoseVal() {
        // 未选择
        if (choseId != -1) {
            for (LifeSignDoubleCheckHistoryDataItem lifeSignDoubleCheckHistoryDataItem :
                    list) {
                if (lifeSignDoubleCheckHistoryDataItem.CJH == choseId) {
                    return lifeSignDoubleCheckHistoryDataItem.TZNR;
                }
            }
        }
        return null;
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
    public LifeSignDoubleCheckHistoryDataItem getItem(int arg0) {
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
            convertView = inflater.inflate(R.layout.item_list_text_two_vert_primary, parent, false);
            vHolder = new ViewHolder();
            convertView.setPadding(0, 0, 0, 0);
            vHolder.time = (TextView) convertView.findViewById(R.id.id_tv_one);
            vHolder.name = (TextView) convertView.findViewById(R.id.id_tv_two);
            vHolder.name.setTextColor(Color.RED);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        LifeSignDoubleCheckHistoryDataItem vo = list.get(position);
        Date date = DateUtil.getDateCompat(vo.CJSJ);
        String cjsj = DateUtil.format_MMdd_HHmm.format(date);
        vHolder.time.setText(cjsj);
        vHolder.name.setText(vo.TZNR);

        if (choseId == vo.CJH) {
            convertView.setBackgroundResource(R.color.classicItemBgSelected);
        } else {
            convertView.setBackgroundResource(R.color.classicItemBg);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView time;
        public TextView name;
    }

}
