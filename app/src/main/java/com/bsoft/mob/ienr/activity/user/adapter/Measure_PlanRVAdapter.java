package com.bsoft.mob.ienr.activity.user.adapter;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.nurseplan.Measure;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;

import java.util.List;

/**
 * Created by Classichu on 2018/2/28.
 */

public class Measure_PlanRVAdapter extends ClassicRecyclerViewAdapter<Measure>{
    private String previousCszh = "";
    private int cszhCount = 0;
    private int color0 = Color.WHITE;
    private int color1 = Color.LTGRAY;

    boolean isFirst = true;
    public Measure_PlanRVAdapter(List<Measure> mDataList, int mItemLayoutId) {
        super(mDataList, mItemLayoutId);
    }

    @Override
    public void findBindView(final int position, ClassicRecyclerViewHolder vHolder) {
        EditText tv_name = vHolder.findBindItemView(R.id.nurseplan_item_name);
        SpinnerLayout spinnerLayout = vHolder.findBindItemView(R.id.id_spinner_layout);
        Spinner sp_type = spinnerLayout.getSpinner();
        final CheckBox cb_selected = vHolder.findBindItemView(R.id.checkBox);
        TextView tv_starttime = vHolder.findBindItemView(R.id.nurseplan_item_starttime);
        TextView tv_endtime = vHolder.findBindItemView(R.id.nurseplan_item_endtime);
        TextView tv_startperson = vHolder.findBindItemView(R.id.nurseplan_item_startperson);
        TextView tv_endperson = vHolder.findBindItemView(R.id.nurseplan_item_endjperson);
        ImageView iv_edit = vHolder.findBindItemView(R.id.nurseplan_item_edit);
        View id_ll_more = vHolder.findBindItemView(R.id.id_ll_more);

        isFirst = true;
        final EditText _ed = tv_name;
        tv_name.setText(mDataList.get(position).CSMS);
        isFirst = false;
        _ed.addTextChangedListener(new TextWatcher() {

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
                    mDataList.get(position).CSMS = s.toString();
                    mDataList.get(position).MODIFIED = true;
                }

            }
        });
        spinnerLayout.setVisibility(View.VISIBLE);
        spinnerLayout.setEnabled(mDataList.get(position).ZDYBZ != 0);
        sp_type.setEnabled(mDataList.get(position).ZDYBZ != 0);
        sp_type.setSelection(mDataList.get(position).XJBZ);
        sp_type
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int arg2, long arg3) {
                       mDataList.get(position).XJBZ = arg2;
                       mDataList.get(position).MODIFIED = true;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

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
        if (!TextUtils.isEmpty(mDataList.get(position).KSSJ)
                ||!TextUtils.isEmpty(mDataList.get(position).JSSJ)
                ||!TextUtils.isEmpty(mDataList.get(position).KSXM)
                ||!TextUtils.isEmpty(mDataList.get(position).JSXM)
                ){
            id_ll_more.setVisibility(View.VISIBLE);
        }else{
            id_ll_more.setVisibility(View.GONE);
        }
        tv_starttime.setText(mDataList.get(position).KSSJ);
        tv_endtime.setText(mDataList.get(position).JSSJ);
        tv_startperson.setText(mDataList.get(position).KSXM);
        tv_endperson.setText(mDataList.get(position).JSXM);
        iv_edit.setVisibility(View.VISIBLE);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(v, position);
                }

            }
        });
        if (!previousCszh.equals(mDataList.get(position).CSZH)) {
            previousCszh = mDataList.get(position).CSZH;
            cszhCount++;
        }
        if (cszhCount % 2 == 0) {
            vHolder.itemView.setBackgroundColor(color0);
        } else {
            vHolder.itemView.setBackgroundColor(color1);
        }


    }

    private MeasureRVAdapter.OnEditClickListener onEditClickListener;

    public void setOnEditClickListener(MeasureRVAdapter.OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public interface OnEditClickListener {
        void onEditClick(View view, int pos);
    }
}
