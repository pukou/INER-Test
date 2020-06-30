package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.daily.DailySecondItem;

import java.util.ArrayList;

/**
 * 病区列表adapter
 *
 * @author hy
 */
public class DailySecondAdapter extends BaseAdapter {

    private ArrayList<DailySecondItem> list;

    public ArrayList<DailySecondItem> getList() {
        return list;
    }

    private Context mContext;

    public DailySecondAdapter(Context context, ArrayList<DailySecondItem> _list) {
        this.list = _list;
        this.mContext = context;
    }

    public void clear() {

        if (this.list != null) {
            list.clear();
            notifyDataSetChanged();
        }

    }

    /**
     * 成功返回新增item位置
     *
     * @param index
     * @return
     */
    public int addItem(DailySecondItem index) {
        if (index == null) {
            return -1;
        }
        if (list == null) {
            list = new ArrayList<DailySecondItem>();
        }

        if (!list.contains(index)) {
            list.add(index);
            return list.size() - 1;
        }

        return -1;
    }

    public boolean removeItem(DailySecondItem index) {

        if (index == null || list == null) {
            return true;
        }

        if (list.contains(index)) {

            boolean ok = list.remove(index);
            if (ok) {
                notifyDataSetChanged();
            }
            return ok;
        }
        return true;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public DailySecondItem getItem(int arg0) {
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
            vHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_bar_check_no_clickable, parent,false);
            TextView id_tv_for_bar_check = convertView.findViewById(R.id.id_tv_for_bar_check);
            id_tv_for_bar_check.setVisibility(View.GONE);
            vHolder.nameView = convertView.findViewById(R.id.healthguid_datetime_txt);
            vHolder.healthguid_cbpre = (CheckBox) convertView.findViewById(R.id.healthguid_cbpre);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        DailySecondItem vo = list.get(position);
        vHolder.nameView.setText(vo.XMMC);

        vHolder.healthguid_cbpre.setChecked(vo.checked);
        // ((CheckedTextView) convertView).toggle();
        return convertView;
    }

    public void setItemCheck(int position, boolean checked) {
        DailySecondItem vo = list.get(position);
        vo.checked = checked;
        notifyDataSetChanged();
    }

    class ViewHolder {
        public TextView nameView;
        public CheckBox healthguid_cbpre;
    }

}
