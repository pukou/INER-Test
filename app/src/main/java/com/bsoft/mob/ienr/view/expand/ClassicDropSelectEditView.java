package com.bsoft.mob.ienr.view.expand;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.VectorOrImageResHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.KeyBoardTool;
import com.bsoft.mob.ienr.util.tools.SizeTool;
import com.classichu.adapter.listener.SimpleOnRVItemTouchListener;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;
import com.classichu.popupwindow.ui.ClassicPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Classichu on 2017/11/2.
 */

public class ClassicDropSelectEditView extends LinearLayout {
    private Context mContext;
    private boolean isInputAble = true;//优先级大于mEditAble

    public void setInputAble(boolean inputAble) {
        isInputAble = inputAble;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isInputAble) {
            return true;
        }
        if (mEditAble) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }

    private boolean mEditAble;

    public ClassicDropSelectEditView(Context context) {
        this(context, null, true);
    }
    public ClassicDropSelectEditView(Context context, AttributeSet attrs) {
        this(context, attrs, true);
    }
    public ClassicDropSelectEditView(Context context, boolean editAble) {
        this(context, null, editAble);
    }

    public ClassicDropSelectEditView(Context context, AttributeSet attrs, boolean editAble) {
        this(context, attrs, 0, editAble);
    }

    public ClassicDropSelectEditView(Context context, AttributeSet attrs, int defStyleAttr, boolean editAble) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mEditAble = editAble;
        init();
        //initEditText();
        initClassicInputLayout();
        initDropDownImg();
        //
        if (mEditAble) {
            dropDownImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDropDownSelect(v);
                    KeyBoardTool.hideKeyboard(v);//点击下拉 收起键盘
                }
            });
        } else {
            mClassicInputLayout.getInput().setCursorVisible(false);
            //
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDropDownSelect(v);
                }
            });
        }
        if (this.getBackground() == null) {
            this.setBackgroundResource(R.drawable.selector_classic_bg_edit);
        }
    }

    private void init() {
        //
        LayoutParams ll_lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        this.setLayoutParams(ll_lp);
        this.setGravity(Gravity.CENTER_VERTICAL);
    }

    private ImageView dropDownImg;

    private void initDropDownImg() {
        dropDownImg = new ImageView(mContext);
        int paddingTopBottomRight = SizeTool.dp2px(8);//x dp
        dropDownImg.setPadding(0, paddingTopBottomRight, paddingTopBottomRight, paddingTopBottomRight);
        Drawable downDrawable = VectorOrImageResHelper.getDrawable(mContext, R.drawable.ic_keyboard_arrow_down_black_24dp);
        //### android 4.1.1 not work  downDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        dropDownImg.setImageDrawable(downDrawable);
        dropDownImg.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        //###!!!! dropDownImg.setBackgroundResource(R.drawable.selector_classic_item_bg_click);
        this.addView(dropDownImg);
    }

    /* private EditText editText;

     public EditText getEditText() {
         return editText;
     }*/
    private ClassicInputLayout mClassicInputLayout;

    public ClassicInputLayout getClassicInputLayout() {
        return mClassicInputLayout;
    }

    public void setHint(CharSequence hint) {
        mClassicInputLayout.setHint(hint);
    }

    public void setSelectAllOnFocus(boolean selectAllOnFocus) {
        mClassicInputLayout.getInput().setSelectAllOnFocus(selectAllOnFocus);
    }

    public void setText(CharSequence text) {
        mClassicInputLayout.setText(text);
    }

    public void setTextByKey(String key) {
        if (this.stringList != null && !this.stringList.isEmpty()) {
            for (Pair<String, String> pair : stringList) {
                if (pair.first.equals(key)) {
                    String value = pair.second;
                    mClassicInputLayout.setText(value);
                    break;
                }
            }
        }
    }

    public void setError(CharSequence error) {
        mClassicInputLayout.setError(error);
    }


    public String getText() {
        return mClassicInputLayout.getText();
    }

    private void initClassicInputLayout() {
        mClassicInputLayout = new ClassicInputLayout(mContext);
        LayoutParams ll_lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        //android 5.0 API 21上的EditText文字偏上 定义不同的dimen文件 兼容处理这个问题
        int api21_fixed_edittext_spacing = mContext.getResources().getDimensionPixelSize(R.dimen.api21FixedEditTextSpacing);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        mClassicInputLayout.setGravity(Gravity.CENTER_VERTICAL);
        mClassicInputLayout.setLayoutParams(ll_lp);
        //设置左padding
        mClassicInputLayout.setPadding(mClassicInputLayout.getPaddingLeft() + SizeTool.dp2px(5),
                mClassicInputLayout.getPaddingTop() + api21_fixed_edittext_spacing,
                mClassicInputLayout.getPaddingRight(),
                mClassicInputLayout.getPaddingBottom());
        //mClassicInputLayout.getInput().setHintTextColor(Color.parseColor("#42000000"));
        mClassicInputLayout.getInput().setLines(1);
        mClassicInputLayout.getInput().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.classic_text_size_secondary));
        ///## ViewCompat.setBackground(mClassicInputLayout.getInput(), null);
        ViewCompat.setBackground(mClassicInputLayout.getInput(), null);
        if (mEditAble) {
            mClassicInputLayout.getInput().setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // 会影响内部view 的 setSelected 状态
                    ClassicDropSelectEditView.this.setSelected(hasFocus);
                    // ClassicInputLayout.this.setActivated(hasFocus);
                }
            });
        }
        this.addView(mClassicInputLayout);
    }

    private void initEditText() {
        EditText editText = new EditText(mContext);
        LayoutParams ll_lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        //android 5.0 API 21上的EditText文字偏上 定义不同的dimen文件 兼容处理这个问题
        int api21_fixed_edittext_spacing = mContext.getResources().getDimensionPixelSize(R.dimen.api21FixedEditTextSpacing);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setLayoutParams(ll_lp);
        editText.setLines(1);
        //设置左padding
        editText.setPadding(editText.getPaddingLeft() + SizeTool.dp2px(5),
                editText.getPaddingTop() + api21_fixed_edittext_spacing,
                editText.getPaddingRight(),
                editText.getPaddingBottom());
        //  editText.setHintTextColor(Color.parseColor("#42000000"));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.classic_text_size_secondary));
        ///###  ViewCompat.setBackground(editText, null);
        ViewCompat.setBackground(editText, null);
        if (mEditAble) {
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // 会影响内部view 的 setSelected 状态
                    ClassicDropSelectEditView.this.setSelected(hasFocus);
                    // ClassicInputLayout.this.setActivated(hasFocus);
                }
            });
        }
        this.addView(editText);
    }

   /*  private TextView textView;

   private void initTextView() {
        textView = new TextView(mContext);
        LayoutParams ll_lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(ll_lp);
        textView.setLines(1);
        //设置左padding
        textView.setPadding(textView.getPaddingLeft() + SizeTool.dp2px(5),
                textView.getPaddingTop(),
                textView.getPaddingRight(),
                textView.getPaddingBottom());
        textView.setHintTextColor(Color.parseColor("#42000000"));
        this.addView(textView);
    }*/

    public void setupDropDownSelectData(List<Pair<String, String>> stringList) {
        if (stringList != null && stringList.size() > 0) {
            dropDownImg.setVisibility(VISIBLE);
            this.stringList.clear();
            this.stringList.addAll(stringList);
        } else {
            dropDownImg.setVisibility(GONE);
        }

    }

    private List<Pair<String, String>> stringList = new ArrayList<>();
    private SelectAdapter adapter;
    private ClassicPopupWindow mClassicPopupWindow;

    private void showDropDownSelect(View view) {
        //
        adapter = new SelectAdapter(stringList,
                R.layout.item_list_text_one_primary);

        RecyclerView recyclerView = ViewBuildHelper.buildRecyclerView(mContext);
        recyclerView.setBackgroundResource(R.drawable.shape_classic_bg_shadow);
        recyclerView.addOnItemTouchListener(new SimpleOnRVItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                // ToastTool.show("onItemClick");
                Pair<String, String> value = adapter.getData(position);
                mClassicInputLayout.setText(value.second);
                mClassicInputLayout.getInput().setSelection(mClassicInputLayout.getInput().length());//将光标移动最后一个字符后面
                //
                mClassicPopupWindow.dismiss();
            }
        });
        recyclerView.setAdapter(adapter);
        //
        int linearLayoutWidth = this.getMeasuredWidth();
        mClassicPopupWindow = new ClassicPopupWindow.Builder(mContext).setView(recyclerView).setWidth(linearLayoutWidth).build();
        mClassicPopupWindow.show(this);//注意是相对于当前的linearLayout

    }


    class SelectAdapter extends ClassicRecyclerViewAdapter<Pair<String, String>> {

        public SelectAdapter(List<Pair<String, String>> mDataList, int mItemLayoutId) {
            super(mDataList, mItemLayoutId);
        }

        @Override
        public void findBindView(int pos, ClassicRecyclerViewHolder classicRecyclerViewHolder) {
            if (!EmptyTool.isEmpty(mDataList)) {
                TextView id_tv_item_title = classicRecyclerViewHolder.findBindItemView(R.id.name);
                id_tv_item_title.setText(mDataList.get(pos).second);
            }
        }
    }
}
