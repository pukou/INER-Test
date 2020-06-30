package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.model.clinicalevent.ClinicalEventInfo;
import com.bsoft.mob.ienr.model.clinicalevent.ClinicalEventType;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

public class ClincialEventListAdapter extends BaseExpandableListAdapter {
    Context mContext;
    List<ClinicalEventType> mList;
    boolean isFirst = true;
    String YHID;


    public ClincialEventListAdapter(Context mContext, List<ClinicalEventType> list,String YHID) {
        this.mContext = mContext;
        this.mList = list;
        this.YHID = YHID;
    }


    @Override
    public ClinicalEventInfo getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).ClinicalEventInfoList.get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(final int groupPosition,
                             final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final ChildHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_group_clinicalevent_child, parent,false);
            vHolder = new ChildHolder();
            vHolder.et_sjms = (EditText) convertView
                    .findViewById(R.id.clinicalevent_item_sjms);
            vHolder.tv_jlr = (TextView) convertView
                    .findViewById(R.id.clinicalevent_item_jlr);
            //
            vHolder.id_tv = (TextView) convertView
                    .findViewById(R.id.id_tv_for_bar_image);
            vHolder.tv_fssj = (TextView) convertView
                    .findViewById(R.id.id_tv_2_for_bar_image);
            vHolder.iv_time_set = (ImageView) convertView
                    .findViewById(R.id.id_iv_for_bar_image);
            //
            vHolder.id_tv2 = (TextView) convertView
                    .findViewById(R.id.id_tv_for_bar_image_copy);
            vHolder.tv_jlsj = (TextView) convertView
                    .findViewById(R.id.id_tv_2_for_bar_image_copy);
            vHolder.iv_delete = (ImageView) convertView
                    .findViewById(R.id.id_iv_for_bar_image_copy);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ChildHolder) convertView.getTag();
        }
        isFirst = true;
        vHolder.et_sjms.setText(mList.get(groupPosition).ClinicalEventInfoList
                .get(childPosition).SJMS);
        isFirst = false;
        vHolder.et_sjms.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!EmptyTool.isBlank(s.toString()) && !isFirst) {
                    mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).SJMS = s.toString().trim();
                    mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).MODIFIED = true;
                }

            }
        });
        int state = mList.get(groupPosition).TypeValue.equals("0") ? View.VISIBLE : View.GONE;
        vHolder.et_sjms.setVisibility(state);
        boolean enable = mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).JLGH.equals(YHID);
        vHolder.et_sjms.setEnabled(enable);
        vHolder.id_tv.setText("发生时间：");
        vHolder.id_tv2.setText("记录时间：");
        vHolder.iv_delete.setImageDrawable(ContextCompatHelper.getDrawable(mContext,R.drawable.ic_delete_forever_black_24dp));
        vHolder.tv_fssj.setText(mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).FSSJ);
        vHolder.tv_jlsj.setText(mList.get(groupPosition).ClinicalEventInfoList.get(childPosition).JLSJ);
        vHolder.tv_jlr.setText(mList.get(groupPosition).ClinicalEventInfoList
                .get(childPosition).JLR);
        int xtbs = mList.get(groupPosition).ClinicalEventInfoList
                .get(childPosition).XTBZ.equals("1") ? View.GONE : View.VISIBLE;
        vHolder.iv_delete.setVisibility(xtbs);
        vHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.xtbs(v,groupPosition,childPosition);
                }

            }
        });
        vHolder.iv_time_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.time(v,groupPosition,childPosition,vHolder.tv_fssj.getText().toString());
                }
            }
        });
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).ClinicalEventInfoList == null ? 0 : mList
                .get(groupPosition).ClinicalEventInfoList.size();
    }


    @Override
    public ClinicalEventType getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return mList.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ParentHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_group_clinicalevent, parent,false);
            vHolder = new ParentHolder();
            vHolder.tv_name = (TextView) convertView
                    .findViewById(R.id.clinicalevent_name);
            vHolder.tv_num = (TextView) convertView
                    .findViewById(R.id.clinicalevent_num);
            vHolder.iv_save = (ImageView) convertView
                    .findViewById(R.id.clinicalevent_save);
            vHolder.iv_add = (ImageView) convertView
                    .findViewById(R.id.clinicalevent_add);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ParentHolder) convertView.getTag();
        }
        vHolder.tv_name.setText(mList.get(groupPosition).TypeName);
        vHolder.tv_num.setText(mList.get(groupPosition).Count);
        vHolder.iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.save(v,groupPosition);
                }
            }
        });
        vHolder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null){
                    clickListener.add(v,groupPosition);
                }
            }
        });
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ParentHolder {
        TextView tv_name;
        TextView tv_num;
        ImageView iv_save;
        ImageView iv_add;
    }

    class ChildHolder {
        EditText et_sjms;
        TextView id_tv;
        TextView tv_fssj;
        TextView id_tv2;
        ImageView iv_time_set;
        TextView tv_jlr;
        TextView tv_jlsj;
        ImageView iv_delete;
    }

    private ClickListener clickListener;
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static abstract class ClickListener {
        public abstract void add(View view,int groupPosition);

        public abstract void save(View view,int groupPosition);

        public abstract void time(View view,int groupPosition,int childPosition, String fssj);

        public abstract void xtbs(View view,int groupPosition,int childPosition);
    }
}
