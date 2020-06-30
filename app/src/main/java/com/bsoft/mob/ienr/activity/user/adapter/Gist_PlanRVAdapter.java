package com.bsoft.mob.ienr.activity.user.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurseplan.DiagnosticBasis;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;

import java.util.List;

/**
 * Created by Classichu on 2018/2/28.
 */

public class Gist_PlanRVAdapter extends ClassicRecyclerViewAdapter<DiagnosticBasis> {

    public Gist_PlanRVAdapter(List<DiagnosticBasis> mDataList, int mItemLayoutId) {
        super(mDataList, mItemLayoutId);
    }

    @Override
    public void findBindView(final int position, ClassicRecyclerViewHolder vHolder) {
        EditText tv_name = vHolder.findBindItemView(R.id.nurseplan_item_name);
        final CheckBox cb_selected = vHolder.findBindItemView(R.id.checkBox);
        View id_ll_more = vHolder.findBindItemView(R.id.id_ll_more);
        id_ll_more.setVisibility(View.GONE);

        tv_name.setText(mDataList.get(position).ZDMS);
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


    }
}
