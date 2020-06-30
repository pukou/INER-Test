package com.bsoft.mob.ienr.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.ListViewHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignOptionItem;
import com.classichu.popupwindow.ui.ClassicPopupWindow;
import com.classichu.popupwindow.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PullEditViewOld extends FrameLayout {

    // public View mainView;
    public EditText edit;
    // public ViewGroup down;

    public List<LifeSignOptionItem> mDatas;

    private ClassicPopupWindow mClassicPopupWindow;

    OnSelectListener selectListener;

    public int popWidth = 0;
    private boolean mIsChangGui = false;

    public PullEditViewOld(Context context, boolean isChangGui) {
        super(context);
        mIsChangGui = isChangGui;
        init(context);
    }

    public PullEditViewOld(Context context) {
        super(context);
        init(context);
    }

    public PullEditViewOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // requires by api level 11 , add by hy
    public PullEditViewOld(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.selectListener = listener;
    }

    public OnSelectListener getOnSelectListener() {
        return this.selectListener;
    }

    public void setEditHitText(String text) {
        edit.setHint(text);
    }

    public EditText getEditText() {
        return edit;
    }

    public void setEditIsAdble(boolean flage) {
        edit.setEnabled(flage);
    }

    public void hidden() {
        if (null != mClassicPopupWindow) {
            mClassicPopupWindow.dismiss();
        }
    }

    private PullAdapter mPullAdapter;

    public void setDataList(List<LifeSignOptionItem> datas) {
        this.mDatas = datas;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout rootLa = LayoutParamsHelper.buildLinearMatchWrap_V(v.getContext());
                //
                EditText fiterEdit = ViewBuildHelper.buildEditTextMatchWrap(v.getContext(), null);
//                fiterEdit.setBackgroundColor(ContextCompat.getColor(v.getContext(),R.color.windowBackground));
                fiterEdit.setBackgroundResource(R.drawable.shape_classic_bg_view_bar);
                fiterEdit.setHint("可输入筛选内容");
                fiterEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (mPullAdapter == null && mDatas == null) {
                            return;
                        }
                        String queryStr = s.toString();
                        if (TextUtils.isEmpty(queryStr)) {
                            mPullAdapter.refreshData(mDatas);
                        } else {
                            mPullAdapter.refreshData(fiterDatas(queryStr));
                        }

                    }
                });
                //
                ListView listView = new ListView(v.getContext());
                listView.setBackgroundColor(Color.WHITE);
                mPullAdapter = new PullAdapter(mDatas);
                listView.setAdapter(mPullAdapter);
                //
                fiterEdit.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int fiterEditHeight = fiterEdit.getMeasuredHeight();
                int maxHalfScreenHeight = ScreenUtil.getScreenHeight()/3;
                Pair<Integer, Integer> widthHeightPair = ListViewHelper.getItemWidthHeightAfterSetAdapter(listView);
                //
                ClassicPopupWindow.Builder builder = new ClassicPopupWindow.Builder(v.getContext());
                if (widthHeightPair != null) {
                    //修正宽度高度 WRAP_CONTENT
                    builder.setWidth(widthHeightPair.first);
//                    builder.setHeight(widthHeightPair.second);
                    int height = fiterEditHeight + widthHeightPair.second;
                    builder.setHeight(Math.min(maxHalfScreenHeight,height));
                }
                rootLa.addView(fiterEdit);
                rootLa.addView(listView);
//                mClassicPopupWindow=builder.setView(listView).build();
                mClassicPopupWindow = builder.setView(rootLa).build();
//                mClassicPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//                mClassicPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                mClassicPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                mClassicPopupWindow.showSameWidth(v);
            }
        });
        // addView(mainView);
    }

    private List<LifeSignOptionItem> fiterDatas(String queryStr) {
        List<LifeSignOptionItem> reslut = new ArrayList<>();
        if (mDatas == null) {
            return reslut;
        }
        for (LifeSignOptionItem mData : mDatas) {
            if (TextUtils.isEmpty(mData.XZNR)) {
                continue;
            }
            if (mData.XZNR.trim().toLowerCase().contains(queryStr.trim().toLowerCase())) {
                reslut.add(mData);
            }
        }
        return reslut;
    }

    void init(Context context) {
        LayoutInflater.from(context).inflate(mIsChangGui ? R.layout.layout_edit_pull_wrap : R.layout.layout_edit_pull, this);
//        LayoutInflater.from(context).inflate(R.layout.layout_edit_pull, this);
        edit = (EditText) findViewById(R.id.edit);
        edit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // 会影响内部view 的 setSelected 状态
                PullEditViewOld.this.setSelected(hasFocus);
                // PullEditView.this.setActivated(hasFocus);
            }
        });

    }

    class PullAdapter extends BaseAdapter {
        public List<LifeSignOptionItem> mLifeSignOptionItemList = new ArrayList<>();

        public PullAdapter(List<LifeSignOptionItem> dats) {
            mLifeSignOptionItemList.clear();
            mLifeSignOptionItemList.addAll(dats);
        }

        private void refreshData(List<LifeSignOptionItem> dats) {
            mLifeSignOptionItemList.clear();
            mLifeSignOptionItemList.addAll(dats);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLifeSignOptionItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLifeSignOptionItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_text_one, parent, false);
                holder.text_row = (TextView) convertView
                        .findViewById(R.id.text_row);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text_row.setText(mLifeSignOptionItemList.get(position).XZNR);
            holder.text_row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    edit.setText(mLifeSignOptionItemList.get(position).XZNR);
                    // 移动光标到到后
                    Editable etext = edit.getText();
                    int eposition = edit.length();
                    Selection.setSelection(etext, eposition);

                    if (null != selectListener) {
                        selectListener.doSelect(mLifeSignOptionItemList.get(position).XZH);
                    }
                    //
                    hidden();
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            public TextView text_row;
        }
    }

    public interface OnSelectListener {
        void doSelect(String id);
    }

}
