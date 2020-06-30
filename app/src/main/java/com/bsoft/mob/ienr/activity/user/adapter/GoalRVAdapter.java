package com.bsoft.mob.ienr.activity.user.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurseplan.Goal;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;

import java.util.List;

/**
 * Created by Classichu on 2018/2/28.
 */

public class GoalRVAdapter extends ClassicRecyclerViewAdapter<Goal> {

    public GoalRVAdapter(List<Goal> mDataList, int mItemLayoutId) {
        super(mDataList, mItemLayoutId);
    }

    @Override
    public void findBindView(final int position, ClassicRecyclerViewHolder vHolder) {

        EditText tv_name = vHolder.findBindItemView(R.id.nurseplan_item_name);
        final CheckBox cb_selected = vHolder.findBindItemView(R.id.checkBox);
        View id_ll_more = vHolder.findBindItemView(R.id.id_ll_more);
        id_ll_more.setVisibility(View.GONE);
     /*   final NursePlanActivity.ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(NursePlanActivity.this)
                    .inflate(R.layout.item_list_nurseplan_measure, parent, false);
            vHolder = new NursePlanActivity.ViewHolder();
            vHolder.tv_name = (EditText) convertView
                    .findViewById(R.id.nurseplan_item_name);
            vHolder.cb_selected = (CheckBox) convertView
                    .findViewById(R.id.checkBox);
            convertView.setTag(vHolder);
        } else {
            vHolder = (NursePlanActivity.ViewHolder) convertView.getTag();
        }*/
        tv_name.setText(mDataList.get(position).MBMS);
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
                    mDataList.get(position).MBMS = s.toString();
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
