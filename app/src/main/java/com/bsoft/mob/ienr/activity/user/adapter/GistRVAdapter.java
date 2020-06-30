package com.bsoft.mob.ienr.activity.user.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurseplan.DiagnosticBasis;
import com.bsoft.mob.ienr.model.nurseplan.ZDMS_DataWrapper;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;

import java.util.List;

/**
 * Created by Classichu on 2018/2/28.
 */

public class GistRVAdapter extends ClassicRecyclerViewAdapter<DiagnosticBasis>{
    private String need_replace;
    private ZDMS_DataWrapper zdms_dataWrapper;
    public GistRVAdapter(List<DiagnosticBasis> mDataList, int mItemLayoutId,String need_replace,ZDMS_DataWrapper zdms_dataWrapper) {
        super(mDataList, mItemLayoutId);
        this.need_replace = need_replace;
        this.zdms_dataWrapper = zdms_dataWrapper;
    }

    @Override
    public void findBindView(final int position, ClassicRecyclerViewHolder classicRecyclerViewHolder) {
        EditText tv_name = classicRecyclerViewHolder.findBindItemView(R.id.nurseplan_item_name);
        final CheckBox cb_selected = classicRecyclerViewHolder.findBindItemView(R.id.checkBox);
        View id_ll_more = classicRecyclerViewHolder.findBindItemView(R.id.id_ll_more);
        id_ll_more.setVisibility(View.GONE);
        //赋值 诊断描述
        if (zdms_dataWrapper != null && zdms_dataWrapper.getZdms_beanList() != null) {
            for (int i = 0; i < zdms_dataWrapper.getZdms_beanList().size(); i++) {
                String zdxh = zdms_dataWrapper.getZdms_beanList().get(i).ZDXH;
                String zdms = zdms_dataWrapper.getZdms_beanList().get(i).ZDMS;
                if (mDataList.get(position).ZDXH.equals(zdxh)) {
                    mDataList.get(position).ZDMS = zdms;
                    mDataList.get(position).SELECTED = true;
                    break;
                }
            }
        }

        mDataList.get(position).ZDMS = mDataList.get(position).ZDMS == null ? "" : mDataList.get(position).ZDMS;
        if (need_replace != null) {
            //赋值  需要赋值的地方
            String replace_key = "(*)";
            if (mDataList.get(position).ZDMS.contains(replace_key)) {
                mDataList.get(position).SELECTED = true;
            }
            mDataList.get(position).ZDMS = mDataList.get(position).ZDMS.replace(replace_key, need_replace);
        }
        tv_name.setText(mDataList.get(position).ZDMS);

       cb_selected.setChecked(mDataList.get(position).SELECTED);
       cb_selected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cb_selected.isChecked()) {
                    mDataList.get(position).SELECTED = true;
                    cb_selected.setChecked(true);
                } else {
                    mDataList.get(position).SELECTED = false;
                    cb_selected.setChecked(false);
                }
            }
        });
        tv_name.addTextChangedListener(new TextWatcher() {

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
                if (!EmptyTool.isBlank(s.toString())) {
                    mDataList.get(position).ZDMS = s.toString();
                    mDataList.get(position).MODIFIED = true;
                }
            }
        });
    }
}
