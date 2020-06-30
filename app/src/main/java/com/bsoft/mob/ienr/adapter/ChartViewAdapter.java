package com.bsoft.mob.ienr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.inspection.InspectionXMBean;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.Date;
import java.util.List;


public class ChartViewAdapter extends BaseAdapter {

    private List<InspectionXMBean> listInspectionXMBean;

    public ChartViewAdapter(List<InspectionXMBean> list) {
        this.listInspectionXMBean = list;
    }
    public void refreshDataList(List<InspectionXMBean> dataList){
        listInspectionXMBean.clear();
        listInspectionXMBean.addAll(dataList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listInspectionXMBean != null ? listInspectionXMBean.size() : 0;
    }

    @Override
    public InspectionXMBean getItem(int arg0) {
        return listInspectionXMBean.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {

           convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_list_text_two_primary,  parent,false);
            vHolder = new ViewHolder();

            vHolder.one = (TextView) convertView
                    .findViewById(R.id.name);
            vHolder.two = (TextView) convertView
                    .findViewById(R.id.time);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        InspectionXMBean inspectionXMBean    = listInspectionXMBean.get(position);

        Date date= DateUtil.getDateCompat(inspectionXMBean.SHSJ);
        String oneStr=inspectionXMBean.SHSJ;
        if (date!=null) {
            oneStr=DateUtil.dateToString(date,"yyyy-MM-dd");
        }
        vHolder.one.setText(oneStr);
        String twoStr="";
        if (!EmptyTool.isBlank(inspectionXMBean.DW)) {
            twoStr=inspectionXMBean.DW;
        }
        vHolder.two.setText(inspectionXMBean.HYJG+" "+twoStr);

        return convertView;
    }

    class ViewHolder {
        public TextView one;
        public TextView two;
    }

}
