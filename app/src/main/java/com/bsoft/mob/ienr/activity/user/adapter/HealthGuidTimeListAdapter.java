package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidClassifyData;

import java.util.ArrayList;

/**
 * Created by TXM on 2015-11-30.
 */
@Deprecated
public class HealthGuidTimeListAdapter extends BaseAdapter implements SpinnerAdapter {
    Context mContext;
    ArrayList<HealthGuidClassifyData> list;

    /**
     *
     * <p>
     * Title: PlanListAdapter
     * </p>
     * <p>
     * Description: 构造函数，初始化上下文和计划列表参数
     * </p>
     *
     * @param mContext
     * @param list
     */
    public HealthGuidTimeListAdapter(Context mContext, ArrayList<HealthGuidClassifyData> list) {
        super();
        this.mContext = mContext;
        this.list = list;
    }

    /*
     * (非 Javadoc) <p>Title: getGroup</p> <p>Description: 获取父项</p>
     *
     * @param groupPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public HealthGuidClassifyData getItem(int position) {
        return list.get(position);
    }

    /*
     * (非 Javadoc) <p>Title: getGroupCount</p> <p>Description:获取父项数量 </p>
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /*
     * (非 Javadoc) <p>Title: getGroupId</p> <p>Description: 获取父项的id</p>
     *
     * @param groupPosition
     *
     * @return
     *
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text = new TextView(mContext);
        text.setHeight(80);
        text.setTextSize(16);
        text.getPaint().setFakeBoldText(true);
        text.setGravity(Gravity.CENTER_VERTICAL);
        list.get(position).JLSJ = list.get(position).JLSJ
                .replace("T", " ");
        text.setText(list.get(position).JLSJ);
        return text;
    }
}
