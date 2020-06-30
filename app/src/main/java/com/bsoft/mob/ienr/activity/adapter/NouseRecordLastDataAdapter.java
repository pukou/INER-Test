package com.bsoft.mob.ienr.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurserecord.LastDataBean;

import java.util.ArrayList;

/*
升级编号【56010022】============================================= start
护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
================= Classichu 2017/10/18 10:33
*/
public class NouseRecordLastDataAdapter extends BaseAdapter {

    private ArrayList<LastDataBean> list;

    private LayoutInflater inflater;

    public NouseRecordLastDataAdapter(Context context,
                                      ArrayList<LastDataBean> _list) {
        this.list = _list;
        // this.dlist = new ArrayList<TransfusionTourReactionVo>();
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public LastDataBean getItem(int arg0) {
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
            convertView = inflater.inflate(R.layout.item_list_text_two_vert_primary_icon, parent,false);
            vHolder = new ViewHolder();

            vHolder.XMQZ = (TextView) convertView.findViewById(R.id.id_tv_one);
            vHolder.SXSJ = (TextView) convertView.findViewById(R.id.id_tv_two);
             convertView.findViewById(R.id.id_tv_more).setVisibility(View.GONE);


            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        LastDataBean dataBean = list.get(position);
        vHolder.XMQZ.setText(dataBean.XMQZ);
        vHolder.SXSJ.setText("书写时间:"+dataBean.SXSJ);

        return convertView;
    }

    class ViewHolder {
        public TextView SXSJ;
        public TextView XMQZ;
    }

}
/* =============================================================== end */