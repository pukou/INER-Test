package com.bsoft.mob.ienr.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignOptionItem;
import com.classichu.dialogview.ui.ClassicDialogFragment;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.List;

public class PullEditView extends FrameLayout {

    // public View mainView;
    public EditText edit;
    // public ViewGroup down;

    public List<LifeSignOptionItem> mDatas;
    private Context mContext;
    private ClassicDialogFragment mClassicDialogFragment;

    OnSelectListener selectListener;

    public int popWidth = 0;
    private boolean mIsChangGui = false;

    public PullEditView(Context context, boolean isChangGui) {
        super(context);
        mIsChangGui = isChangGui;
        init(context);
    }

    public PullEditView(Context context) {
        super(context);
        init(context);
    }

    public PullEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // requires by api level 11 , add by hy
    public PullEditView(Context context, AttributeSet attrs, int defStyle) {
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
//   todo     this.setActivated(flage);
        edit.setEnabled(flage);
    }


    private PullAdapter mPullAdapter;

    public void setDataList(List<LifeSignOptionItem> datas) {
        this.mDatas = datas;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mContext instanceof FragmentActivity)) {
                    Toast.makeText(mContext, "对话框信息有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentActivity fragmentActivity = (FragmentActivity) mContext;
                LinearLayout rootLa = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);
                //
                EditText fiterEdit = ViewBuildHelper.buildEditTextMatchWrap(mContext, null);
//                fiterEdit.setBackgroundColor(ContextCompat.getColor(context,R.color.windowBackground));
                fiterEdit.setBackgroundResource(R.drawable.shape_classic_bg_view_bar);
                fiterEdit.setHint("支持中文首字母筛选,tzx:特治星");
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
                ListView listView = new ListView(mContext);
                listView.setBackgroundResource(R.drawable.shape_classic_bg_view);
                mPullAdapter = new PullAdapter(mDatas);
                listView.setAdapter(mPullAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //
                        hidden();
                        //
                        LifeSignOptionItem lifeSignOptionItem = (LifeSignOptionItem) mPullAdapter.getItem(position);

                        edit.setText(lifeSignOptionItem.XZNR);
                        edit.setSelection(edit.length());
//                        edit.requestFocus();
//                        edit.requestFocusFromTouch();
//                        PullEditView.this.requestFocus();
                        edit.post(new Runnable() {
                            @Override
                            public void run() {
                                edit.requestFocus();
                            }
                        });
//                        edit.requestFocusFromTouch();
                        if (null != selectListener) {
                            selectListener.doSelect(lifeSignOptionItem.XZH);
                        }


                    }
                });
                //
//                fiterEdit.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                int fiterEditHeight = fiterEdit.getMeasuredHeight();
//                int maxHalfScreenHeight = ScreenUtil.getScreenHeight()/3;
//                Pair<Integer, Integer> widthHeightPair = ListViewHelper.getItemWidthHeightAfterSetAdapter(listView);
                //
//                ClassicPopupWindow.Builder builder = new ClassicPopupWindow.Builder(context);
              /*  if (widthHeightPair != null) {
                    //修正宽度高度 WRAP_CONTENT
                    builder.setWidth(widthHeightPair.first);
//                    builder.setHeight(widthHeightPair.second);
                    int height = fiterEditHeight + widthHeightPair.second;
                    builder.setHeight(Math.min(maxHalfScreenHeight,height));
                }*/
                rootLa.addView(fiterEdit);
                rootLa.addView(listView);
//                mClassicPopupWindow=builder.setView(listView).build();
//                mClassicPopupWindow = builder.setView(rootLa).build();
//                mClassicPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//                mClassicPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                mClassicPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//                mClassicPopupWindow.showSameWidth(v);

                mClassicDialogFragment = new ClassicDialogFragment.Builder(mContext)
                        .setContentView(rootLa)
                        .setCustomTitleView(ViewBuildHelper.buildDialogTitleTextView(mContext, "选择"))
                        .setCancelText("取消")
                        .build();
                mClassicDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "mContextdsa");
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
            String xznr = mData.XZNR.trim();
            if (xznr.toLowerCase().contains(queryStr.trim().toLowerCase())) {
                reslut.add(mData);
                //找到了  直接处理下一个
                continue;
            }
            //继续查找
            String pinyin = null;
            try {
//                pinyin = PinyinHelper.getShortPinyin(xznr);   //linchuagliujing
                pinyin = PinyinHelper.convertToPinyinString(xznr, ",", PinyinFormat.WITHOUT_TONE); // ni,hao,shi,jie
            } catch (PinyinException e) {
                e.printStackTrace();
            }
            if (pinyin != null) {
                //l
                String[] charArr = pinyin.split(",");
                StringBuilder firstCharB = new StringBuilder();
                for (int i = 0; i < charArr.length; i++) {
                    String temp = charArr[i];//lin / chuang / lu / jing
                    firstCharB.append(temp.charAt(0));// l / c / l / j
                }
                //lclj
                String firstChar = firstCharB.toString();
                if (firstChar.toLowerCase().startsWith(queryStr.toLowerCase())) {
                    reslut.add(mData);
                }
            }

        }
        return reslut;
    }

    void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(mIsChangGui ? R.layout.layout_edit_pull_wrap : R.layout.layout_edit_pull, this);
//        LayoutInflater.from(context).inflate(R.layout.layout_edit_pull, this);
        edit = (EditText) findViewById(R.id.edit);
        edit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // 会影响内部view 的 setSelected 状态
                PullEditView.this.setSelected(hasFocus);
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
            return convertView;
        }

        public final class ViewHolder {
            public TextView text_row;
        }
    }

    public void hidden() {
        if (mClassicDialogFragment != null) {
            mClassicDialogFragment.dismissAllowingStateLoss();
        }
    }

    public interface OnSelectListener {
        void doSelect(String id);
    }

}
