package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.catheter.CatheterBeenMeasurData;

import java.util.ArrayList;

public class CatheterHistoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CatheterBeenMeasurData> list;
    private LayoutInflater inflater;

    public CatheterHistoryAdapter(Context context, ArrayList<CatheterBeenMeasurData> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).JLXH;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(
                    R.layout.item_list_catheter_history,  parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mTime = (TextView) convertView
                    .findViewById(R.id.show_time);
            viewHolder.mContent = (TextView) convertView
                    .findViewById(R.id.show_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String time = list.get(position).JLSJ.substring(10,19);
        String sgmc = list.get(position).YLGMC;
        String jlr = list.get(position).JLGH;
        String jll = list.get(position).YLL;
        String content = sgmc +": "+ jll +"ml"+ " by:" + jlr ;
        viewHolder.mContent.setText(content);
        viewHolder.mTime.setText(time);
        return convertView;
    }

    static class ViewHolder {
        TextView mTime;
        TextView mContent;
    }

}
