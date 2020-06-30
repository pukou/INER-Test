package com.bsoft.mob.ienr.activity.user.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.trad._SHFF;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;

import java.util.List;

public class TradOneRVAdapter extends ClassicRecyclerViewAdapter<_SHFF> {
    private Context mContext;
    private boolean isAllCanEdit;

    public TradOneRVAdapter(Context context, List<_SHFF> dataList, boolean isCanEdit) {
        this(context, dataList, R.layout.item_list_trad_one, isCanEdit);
    }

    public TradOneRVAdapter(Context context, List<_SHFF> mDataList, int mItemLayoutId, boolean isCanEdit) {
        super(mDataList, mItemLayoutId);
        mContext = context;
        isAllCanEdit = isCanEdit;
    }


    public void refreshData(List<_SHFF> shffList) {
        mDataList.clear();
        mDataList.addAll(shffList);
        notifyDataSetChanged();
    }

    @Override
    public void findBindView(int position, ClassicRecyclerViewHolder holder) {


        _SHFF item = mDataList.get(position);

        LinearLayout id_ll_cb_container_shff = holder.findBindItemView(R.id.id_ll_cb_container_shff);
        id_ll_cb_container_shff.removeAllViews();
        //
        for (final _SHFF.SHFF_Check shff_check : item.shffCheckList) {
            View layout_content_cb_shff = LayoutInflater.from(mContext).inflate(R.layout.item_list_bar_check_edit_icon, null,false);

            final CheckBox id_cb = layout_content_cb_shff.findViewById(R.id.id_cb);
            id_cb.setEnabled(isAllCanEdit);
            final ImageView id_iv = layout_content_cb_shff.findViewById(R.id.id_iv);
            if (!EmptyTool.isBlank(shff_check.BZXX)) {
                id_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onInfoClick(v, shff_check.BZXX);
                        }
                    }
                });
                id_iv.setVisibility(View.VISIBLE);
            } else {
                id_iv.setVisibility(View.INVISIBLE);
            }

            final EditText id_et_4_id_cb = layout_content_cb_shff.findViewById(R.id.id_et_4_id_cb);
            id_et_4_id_cb.setBackgroundResource("1".equals(shff_check.editable) ? R.drawable.selector_classic_bg_edit : 0);
            id_et_4_id_cb.setText(shff_check.name);
            id_et_4_id_cb.setEnabled(isAllCanEdit&&"1".equals(shff_check.editable));
            if (id_et_4_id_cb.isEnabled()) {
                id_et_4_id_cb.setHint(shff_check.name);
                id_et_4_id_cb.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!EmptyTool.isBlank(editable.toString())) {
                            id_cb.setChecked(true);
                            shff_check.status = "1";
                        } else {
                            id_cb.setChecked(false);
                            shff_check.status = "0";
                        }
                    }
                });
            }
            id_et_4_id_cb.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!EmptyTool.isBlank(editable.toString())) {
                        id_cb.setChecked(true);
                        shff_check.status = "1";
                    } else {
                        id_cb.setChecked(false);
                        shff_check.status = "0";
                    }

                }
            });
            id_cb.setChecked("1".equals(shff_check.status));
            id_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton == null || !compoundButton.isPressed()) {
                        //不响应非点击引起的改变
                        return;
                    }
                    shff_check.status = b ? "1" : "0";
                }
            });
            layout_content_cb_shff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取反
                    shff_check.status = id_cb.isChecked() ? "0" : "1";
                    id_cb.setChecked("1".equals(shff_check.status));
                }
            });
            ///
            id_ll_cb_container_shff.addView(layout_content_cb_shff);
        }


    }


    private OnInfoClickListener listener;

    public void setOnInfoClickListener(OnInfoClickListener listener) {
        this.listener = listener;
    }

    public interface OnInfoClickListener {
        void onInfoClick(View view, String text);
    }
}
