package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurserecord.HelpLeaf;

import java.util.List;

/**
 * 护理记录一级
 *
 * @author hy
 */
public class CareRecordHelpLeafAdapter extends BaseAdapter {

    private Context mContext;
    private List<HelpLeaf> list;

    String[] arrays = null;

    public CareRecordHelpLeafAdapter(Context mContext, List<HelpLeaf> list) {

        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list != null ? list.size() : 0;
    }

    @Override
    public HelpLeaf getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHoler holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_one_primary,parent,false);
            holder = new ViewHoler();
            holder.nameTxt = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(holder);

        } else {
            holder = (ViewHoler) convertView.getTag();
        }

        HelpLeaf item = list.get(position);
        if (item != null) {
            holder.nameTxt.setText(item.ZSMC);
        } else {
            holder.nameTxt.setText(null);
        }

        return convertView;
    }

    class ViewHoler {
        TextView nameTxt;

    }

}
