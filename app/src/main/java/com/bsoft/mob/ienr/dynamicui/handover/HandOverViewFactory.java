package com.bsoft.mob.ienr.dynamicui.handover;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.HandOverActivity;
import com.bsoft.mob.ienr.activity.user.LifeSymptomActivity;
import com.bsoft.mob.ienr.activity.user.RiskEvaluateActivity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.handover.HandOverClassify;
import com.bsoft.mob.ienr.model.handover.HandOverForm;
import com.bsoft.mob.ienr.model.handover.HandOverOption;
import com.bsoft.mob.ienr.model.handover.HandOverProject;
import com.bsoft.mob.ienr.model.handover.RelativeItem;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.List;


public class HandOverViewFactory {

    /**
     * 以2开头，避免重复
     */
    public static final int REQ_RISK = 22;
    public static final int REQ_LIFE = 25;

    private Context mContext;

    private HandOverActivity mActivity;

    private HandOverForm mForm;

    public String Txsj;

    public boolean Enable;

    private DateTimePickerListener listener;

    public static interface DateTimePickerListener {
        public void onDateTimeClick(View view);
    }

    public HandOverViewFactory(Context context, HandOverActivity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.Enable = true;
    }

    public HandOverViewFactory(Context context, HandOverActivity activity, boolean enable, DateTimePickerListener dateTimePickerListener) {
        this.mContext = context;
        this.mActivity = activity;
        this.Enable = enable;
        this.listener = dateTimePickerListener;
    }

    //核对交接单数据时候专用 因不用对照项目链接 故不需要传如activity
    public HandOverViewFactory(Context context, boolean enable, DateTimePickerListener dateTimePickerListener) {
        this.mContext = context;
        this.mActivity = null;
        this.Enable = enable;
        this.listener = dateTimePickerListener;
    }

    public static class RelativeEntity {
        ViewGroup Root;
        int KJID;
        int Id;
        // 控件类型 1:TextView;2:EditText;3:CheckBox;4:RadioBox;
        int Type;

        public RelativeEntity(ViewGroup root, int id, int type, int kjid) {
            this.Id = id;
            this.Root = root;
            this.Type = type;
            this.KJID = kjid;
        }
    }

    private List<RelativeEntity> mRisks = new ArrayList<>();
    private List<RelativeEntity> mLife = new ArrayList<>();

    /**
     * 动态生成UI
     *
     * @param form
     * @param offset4DateTimeAtBatchHandOverAtyHasDoubleViewId 用于 放viewpager 左右2份数据所对应 给予充当View id一致的时候的 偏移量
     * @return
     */
    public LinearLayout builderUi(HandOverForm form, int offset4DateTimeAtBatchHandOverAtyHasDoubleViewId) {

        if (form == null || mContext == null) {
            return null;
        }
        this.mForm = form;

        final LinearLayout root = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);

        // 遍历Classification
        for (HandOverClassify cf : form.HandOverClassifyList) {

            if (cf.FLJB.equals("2")) {
                continue;
            }

            // 生成Classification 标题
            Pair<LinearLayout, TextView> classTextViewLayout = ViewBuildHelper.buildClassTextViewLayout(mContext, cf.FLMC);
            LinearLayout classLayout = classTextViewLayout.first;
            TextView textView = classTextViewLayout.second;
            root.addView(classLayout);
            textView.setTag(cf.YSFL);
            if (!Enable) {
                textView.setTextColor(Color.GRAY);
            }
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //标题下的内容布局
                    LinearLayout ItemNodes = (LinearLayout) root.findViewWithTag(v.getTag() + "-" + v.getTag());
                    if (ItemNodes.getVisibility() == View.GONE) {
                        ItemNodes.setVisibility(View.VISIBLE);
                        //定位到指定组件
                        scrollToView(v);
                    } else {
                        ItemNodes.setVisibility(View.GONE);
                    }
                }
            });

            LinearLayout itemNodesView = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);
            itemNodesView.setVisibility(View.GONE);
            itemNodesView.setTag(cf.YSFL + "-" + cf.YSFL);
            LinearLayout.LayoutParams ll_lp = (LinearLayout.LayoutParams) itemNodesView.getLayoutParams();
            ll_lp.setMargins(SizeHelper.getMarginPrimary(), 0, SizeHelper.getMarginPrimary(), 0);
            if (cf.HandOverProjectList != null) {
                // 遍历ItemNode
                for (HandOverProject project : cf.HandOverProjectList) {
                    // 生成ItemNode 标题
                    itemNodesView.addView(addTextView(project));
                    initItemNode(project, itemNodesView, true, offset4DateTimeAtBatchHandOverAtyHasDoubleViewId);
                }
                root.addView(itemNodesView);
            }
        }
        root.setTag(form);
        return root;
    }


    /**
     * 滚动到指定组件
     *
     * @param v
     */
    private void scrollToView(final View v) {
        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                View curView = v;
                int vToScY = v.getTop();        //View到ScrollView在Y上的距离

                while (true) {
                    curView = (View) curView.getParent();
                    if (curView == null) break;
                    if (curView instanceof NestedScrollView) break;
                    vToScY += curView.getTop();
                }

                if (curView != null) {
                    ((NestedScrollView) curView).scrollTo(0, vToScY);
                }
            }
        }, 10);
    }

    private void initItemNode(HandOverProject project, LinearLayout root, boolean isFirstClass, int offset) {

        if (project == null || root == null) {
            return;
        }
        if (project.CZLX.equals("1")) {//输入框

            //add by louis 2017-5-26 14:17:04
            if (project.SJLX.equals("3")) {//时间和日期
                String formatStr = "yyyy-MM-dd";
                if (!EmptyTool.isBlank(project.XLLB)) {
                    formatStr = project.XLLB;
                }
                //
                HandOverOption option = project.HandOverOptionList.get(0);
                String dateStr = DateTimeHelper.getServerDateTime();
                if (!TextUtils.isEmpty(option.XXNR)) {
                    dateStr = DateTimeFactory.getInstance().custom2DateTime(option.XXNR, formatStr);
                }
                //
                initDateTime(dateStr, option, root, project, offset, formatStr);
            } else {
                //
                initInput(project.HandOverOptionList.get(0), root, project);
            }

        } else if (project.CZLX.equals("2")) {//单选框

            RadioGroup rg = new RadioGroup(mContext);
            rg.setOrientation(LinearLayout.VERTICAL);
            rg.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (HandOverOption option : project.HandOverOptionList) {
                initRadioBox(option, rg, project, isFirstClass);
            }
            if (rg.getChildCount() > 0) {
                root.addView(rg);
            }

        } else if (project.CZLX.equals("3")) {//多选框
            LinearLayout content = new LinearLayout(mContext);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (HandOverOption option : project.HandOverOptionList) {
                initCheckBox(option, content, project, isFirstClass);
            }
            if (content.getChildCount() > 0) {
                root.addView(content);
            }

        } else if (project.CZLX.equals("4")) {//下拉框
            initSpinner(root, project);

        }
    }

    private void initSpinner(LinearLayout root, final HandOverProject project) {
        Spinner sp = new Spinner(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        sp.setLayoutParams(params);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(project.HandOverOptionList);
        sp.setAdapter(spinnerAdapter);
        int id = 0;
        for (int i = 0; i < project.HandOverOptionList.size(); i++) {
            if (project.HandOverOptionList.get(i).ISSELECT) {
                id = i;
                break;
            }
        }
        sp.setSelection(id);
        sp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (arg2 == 0)
                    return;
                project.HandOverOptionList.get(arg2).ISSELECT = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        root.addView(sp);
    }

    /**
     * 生成 文字
     *
     * @param text
     * @param root
     */
    private void initText(String text, LinearLayout root) {

        if (text == null || root == null) {
            return;
        }
        TextView txtView = ViewBuildHelper.buildTextView(mContext, text);
        if (txtView != null) {
            txtView.setTag(txtView);
            txtView.setEnabled(Enable);
            root.addView(txtView);
        }
    }

    /**
     * 生成 时间选择框
     *
     * @param root
     */
    private void initDateTime(final String dateStr, final HandOverOption option, LinearLayout root, final HandOverProject project, int offset, String formatStr) {

        if (root == null) {
            return;
        }

        final TextView txt = ViewBuildHelper.buildTimeTextView(mContext, dateStr);
        txt.setId(Integer.valueOf(project.XMBS + offset));
        txt.setEnabled(Enable);
        txt.setTag(project);
        txt.setTag(R.id.id_hold_time_format, formatStr);

        if (mActivity != null && !EmptyTool.isBlank(project.DZLX) && !project.DZLX.equals("-1")) {
            if (project.DZLX.equals("2")) {
                // 风险
                txt.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                mRisks.add(new RelativeEntity(root, Integer.parseInt(project.DZBD), 1, Integer.parseInt(option.XXBS)));
            } else if (project.DZLX.equals("5")) {
                // 体征
                txt.setTextColor(ContextCompat.getColor(mContext, R.color.pink));
                mLife.add(new RelativeEntity(root, Integer.parseInt(project.DZBD), 1, Integer.parseInt(option.XXBS)));
            }
            txt.setEnabled(false);
        }
        if (mActivity == null && project.ISDIFFERENT) {//核对
            txt.setTextColor(ContextCompat.getColor(mContext,
                    R.color.red));
        }


        // 设置点击监听事件
        txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDateTimeClick(v);
                }
            }
        });

        txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!EmptyTool.isBlank(s.toString()) && !s.toString().equals(dateStr)) {
                    option.XXNR = s.toString();
                    option.ISMODIFY = true;
                    option.ISSELECT = true;
                    project.ISMODIFY = true;
                    project.ISSELECT = true;

                }
            }
        });


        root.addView(txt);

    }


    /**
     * 生成输入框
     *
     * @param root
     * @param project
     */
    private void initInput(final HandOverOption option, LinearLayout root, final HandOverProject project) {

        if (project == null || root == null) {
            return;
        }

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.HORIZONTAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // 加入前置文本
        if (!EmptyTool.isBlank(project.QZWB)) {
            initText(project.QZWB, child);
        }
        final String value = option.XXNR;
        //add
        final EditText edit = ViewBuildHelper.buildEditTextAutoWrap(mContext, value);
        edit.setEnabled(Enable);
        edit.setId(Integer.parseInt(project.XMBS));

        if (mActivity != null && !EmptyTool.isBlank(project.DZLX) && !project.DZLX.equals("-1")
                && !project.DZLX.equals("0")) {
            if (project.DZLX.equals("2")) {
                // 风险
                edit.setTextColor(mContext.getResources().getColor(
                        R.color.green));
                mRisks.add(new RelativeEntity(root, Integer.parseInt(project.DZBD), 2, Integer.parseInt(project.XMBS)));
            } else if (project.DZLX.equals("5")) {
                // 体征
                edit.setTextColor(mContext.getResources()
                        .getColor(R.color.pink));
                mLife.add(new RelativeEntity(root, Integer.parseInt(project.DZXM), 2, Integer.parseInt(project.XMBS)));
            }
            edit.setEnabled(false);
        }

        if (mActivity == null && project.ISDIFFERENT) {//核对
            edit.setTextColor(ContextCompat.getColor(mContext,
                    R.color.red));
        }
        edit.setTag(project);
        child.addView(edit);

        edit.addTextChangedListener(new TextWatcher() {

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

                if (!EmptyTool.isBlank(s.toString()) && !s.toString().equals(value)) {
                    option.XXNR = s.toString();
                    option.ISMODIFY = true;
                    option.ISSELECT = true;
                    project.ISMODIFY = true;
                    project.ISSELECT = true;

                }
            }
        });
        // 加入后置文本
        if (!EmptyTool.isBlank(project.HZWB)) {
            initText(project.HZWB, child);
        }

        root.addView(child);
    }


    /**
     * 初始化单选项
     *
     * @param option
     * @param root
     * @param project
     * @param isFirstClass 一级分类为true 二级分类为false
     */
    private void initRadioBox(final HandOverOption option, RadioGroup root, final HandOverProject project, boolean isFirstClass) {
        if (option == null || root == null || project == null) {
            return;
        }

        final boolean isSelected = option.ISSELECT;

        RadioButton rButton = new AppCompatRadioButton(mContext);
        rButton.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        rButton.setEnabled(Enable);
        rButton.setText(option.XXMC);
        rButton.setId(Integer.parseInt(option.XXBS));
        rButton.setTag(option);
        if (mActivity != null && !EmptyTool.isBlank(project.DZLX)) {
            if (project.DZLX.equals("2")) {
                // 风险
                rButton.setTextColor(mContext.getResources().getColor(
                        R.color.green));
                mRisks.add(new RelativeEntity(root, Integer.parseInt(project.DZBD), 4, Integer.parseInt(option.XXBS)));
            } else if (project.DZLX.equals("5")) {
                // 体征
                rButton.setTextColor(mContext.getResources().getColor(
                        R.color.pink));
                mLife.add(new RelativeEntity(root, Integer.parseInt(project.DZXM), 4, Integer.parseInt(option.XXBS)));
            }
            rButton.setEnabled(false);
        }

        if (mActivity == null && project.ISDIFFERENT) {
            rButton.setTextColor(ContextCompat.getColor(mContext,
                    R.color.red));
        }
        root.addView(rButton);

        if (isSelected) {
            int id = rButton.getId();
            root.check(id);
        }

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        child.setTag(R.id.tag_KJ, option.XXBS);
        root.addView(child);
        if (!EmptyTool.isBlank(option.XJFL) && !option.XJFL.equals("0")) {
            HandOverClassify classify = getEJFL(option.XJFL);
            initEJFLView(classify, child);
        }
        if (EmptyTool.isBlank(project.DZLX)) {
            // 当是一级控件时，隐藏二级项
            if (isFirstClass && !isSelected) {
                child.setVisibility(View.GONE);
            }
            // 激活二级控件
            rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    ViewGroup parent = (ViewGroup) buttonView.getParent();
                    int childCount = parent.getChildCount();
                    for (int itemPos = 0; itemPos < childCount; itemPos++) {
                        View view = parent.getChildAt(itemPos);
                        if (view instanceof LinearLayout) {
                            Object obj = view.getTag(R.id.tag_KJ);
                            if (obj == null) {
                                break;
                            }
                            int tag = Integer.parseInt(obj.toString());
                            if (tag == buttonView.getId()) {
                                view.setVisibility(isChecked ? View.VISIBLE
                                        : View.GONE);
                                break;
                            }
                        }
                    }
                    if (isChecked != isSelected) {
                        option.XXNR = option.XXMC;
                        option.ISMODIFY = true;
                        option.ISSELECT = isChecked;
                        project.ISMODIFY = true;
                        project.ISSELECT = true;
                    }
                }
            });
        }
    }

    /**
     * 初始化多选项
     *
     * @param option
     * @param root
     * @param project
     */
    private void initCheckBox(final HandOverOption option, LinearLayout root, final HandOverProject project, boolean isFirstClass) {
        if (option == null || root == null || project == null) {
            return;
        }
        final boolean isSelected = option.ISSELECT;
        CheckBox checkBox = new AppCompatCheckBox(mContext);
        checkBox.setEnabled(Enable);
        checkBox.setText(option.XXMC);
        checkBox.setId(Integer.parseInt(option.XXBS));
        checkBox.setChecked(isSelected);
        checkBox.setTag(option);
        if (mActivity != null && !EmptyTool.isBlank(project.DZLX)) {
            if (project.DZLX.equals("2")) {
                // 风险
                checkBox.setTextColor(mContext.getResources().getColor(
                        R.color.green));
                mRisks.add(new RelativeEntity(root, Integer.parseInt(project.DZBD), 3, Integer.parseInt(option.XXBS)));
            } else if (project.DZLX.equals("5")) {
                // 体征
                checkBox.setTextColor(mContext.getResources().getColor(
                        R.color.pink));
                mLife.add(new RelativeEntity(root, Integer.parseInt(project.DZXM), 3, Integer.parseInt(option.XXBS)));
            }
            checkBox.setEnabled(false);
        }

        if (mActivity == null && project.ISDIFFERENT) {
            checkBox.setTextColor(ContextCompat.getColor(mContext,
                    R.color.red));
        }
        root.addView(checkBox);

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        child.setTag(R.id.tag_KJ, option.XXBS);
        if (!EmptyTool.isBlank(option.XJFL) && !option.XJFL.equals("0")) {
            HandOverClassify classify = getEJFL(option.XJFL);
            initEJFLView(classify, child);
            root.addView(child);
        }
        if (EmptyTool.isBlank(project.DZLX)) {
            // 当是一级控件时，隐藏二级项
            if (isFirstClass && !isSelected) {
                child.setVisibility(View.GONE);
            }
            // 激活二级控件
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // 激活二级控件
                    ViewGroup parent = (ViewGroup) buttonView.getParent();
                    for (int itemPos = 0; itemPos < parent.getChildCount(); itemPos++) {
                        View view = parent.getChildAt(itemPos);
                        if (view instanceof LinearLayout) {
                            Object obj = view.getTag(R.id.tag_KJ);
                            if (obj == null) {
                                break;
                            }
                            int tag = Integer.parseInt(obj.toString());
                            if (tag == buttonView.getId()) {
                                view.setVisibility(isChecked ? View.VISIBLE
                                        : View.GONE);
                                break;
                            }
                        }
                    }

                    if (isChecked != isSelected) {
                        option.XXNR = option.XXMC;
                        option.ISSELECT = isChecked;
                        option.ISMODIFY = true;
                        project.ISMODIFY = true;
                        boolean isSelect = false;
                        for (HandOverOption item : project.HandOverOptionList) {
                            if (item.ISSELECT) {
                                isSelect = true;
                                break;
                            }
                        }
                        project.ISSELECT = isSelect;
                    }
                }
            });
        }
    }

    /**
     * 根据选项中的下级分类获取获取二级分类
     */
    private HandOverClassify getEJFL(String xjfl) {
        HandOverClassify handOverClassify = null;
        for (HandOverClassify classify : mForm.HandOverClassifyList) {
            if (classify.YSFL.equals(xjfl) && classify.FLJB.equals("2")) {
                handOverClassify = classify;
                break;
            }
        }
        return handOverClassify;
    }

    private void initEJFLView(HandOverClassify cf, LinearLayout root) {

        if (cf.FLJB.equals("1")) {
            return;
        }

        LinearLayout itemNodesView = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);

        if (cf.HandOverProjectList != null) {
            // 遍历ItemNode
            for (HandOverProject project : cf.HandOverProjectList) {
                // 生成ItemNode 标题
                itemNodesView.addView(addTextView(project));
                initItemNode(project, itemNodesView, false, 0);
            }
            root.addView(itemNodesView);
        }
    }


    private TextView addTextView(final HandOverProject project) {
        TextView typeView = ViewBuildHelper.buildTextView(mContext, project.XMMC);
        typeView.setEnabled(Enable);
        if (!EmptyTool.isBlank(project.DZLX) && !project.DZLX.equals("-1")) {
            if (project.DZLX.equals("2")) {
                // 风险
                typeView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.green));
            } else if (project.DZLX.equals("5")) {
                // 体征
                typeView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.pink));
            }
        }

        if (mActivity == null && project.ISDIFFERENT) {
            typeView.setTextColor(ContextCompat.getColor(mContext,
                    R.color.red));
        }

        typeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (!EmptyTool.isBlank(project.DZLX) && project.DZLX.equals("2")) {
                    if (mActivity != null) {
                        // 风险
                        intent = new Intent(mActivity, RiskEvaluateActivity.class);
                        intent.putExtra("PGDH", "");
                        intent.putExtra("PGLX", project.DZBD);//改PGDH=DZBD 为 PGLX=DZBD
                        intent.putExtra("FROMOUT", true);
                        intent.putExtra("TXSJ", Txsj);
                        mActivity.startActivityForResult(intent, REQ_RISK);
                    }
                } else if (!EmptyTool.isBlank(project.DZLX) && project.DZLX.equals("5")) {
                    if (mActivity != null) {
                        // 体征
                        intent = new Intent(mActivity, LifeSymptomActivity.class);
                        intent.putExtra("TXSJ", Txsj);
                        mActivity.startActivityForResult(intent, REQ_LIFE);
                    }
                }
            }
        });
        return typeView;
    }


    class SpinnerAdapter extends BaseAdapter {
        private List<HandOverOption> mData;

        public SpinnerAdapter(List<HandOverOption> datas) {
            this.mData = datas;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public HandOverOption getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.simple_spinner_dropdown_item, parent, false);
                tv = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }
            tv.setText(mData.get(position).XXMC);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.simple_spinner_item, parent, false);
                tv = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }
            tv.setText(mData.get(position).XXMC);
            return convertView;
        }

    }

    public void resetRisk(List<RelativeItem> values) {
        for (RelativeEntity entity : mRisks) {
            boolean isFind = false;
            for (RelativeItem value : values) {
                if (value.ID == entity.Id) {
                    switch (entity.Type) {
                        case 1:
                            ((TextView) entity.Root.findViewById(entity.Id))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    if (String.valueOf(entity.KJID).equals(project.XMBS)) {
                                        project.HandOverOptionList.get(0).DZBDJL = value.DZBDJL;
                                        project.HandOverOptionList.get(0).DZLX = value.DZLX;
                                    }
                                }
                            }
                            break;
                        case 2:
                            ((EditText) entity.Root.findViewById(entity.Id))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    if (String.valueOf(entity.KJID).equals(project.XMBS)) {
                                        project.HandOverOptionList.get(0).DZBDJL = value.DZBDJL;
                                        project.HandOverOptionList.get(0).DZLX = value.DZLX;
                                    }
                                }
                            }
                            break;
                        case 3:
                            ((android.widget.CheckBox) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    for (HandOverOption option : project.HandOverOptionList) {
                                        if (option.XXBS.equals(entity.KJID)) {
                                            option.DZBDJL = value.DZBDJL;
                                            option.DZLX = value.DZLX;
                                        }
                                    }
                                }
                            }
                            break;
                        case 4:
                            ((android.widget.RadioButton) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    for (HandOverOption option : project.HandOverOptionList) {
                                        if (option.XXBS.equals(entity.KJID)) {
                                            option.DZBDJL = value.DZBDJL;
                                            option.DZLX = value.DZLX;
                                        }
                                    }
                                }
                            }
                            break;
                    }
                    if (isFind) {
                        break;
                    }
                }
            }
        }
    }

    public void resetLife(List<RelativeItem> values) {
        for (RelativeEntity entity : mLife) {
            boolean isFind = false;
            for (RelativeItem value : values) {
                if (value.ID == entity.Id) {
                    switch (entity.Type) {
                        case 1:
                            ((TextView) entity.Root.findViewById(entity.KJID))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    if (String.valueOf(entity.KJID).equals(project.XMBS)) {
                                        project.HandOverOptionList.get(0).DZBDJL = value.DZBDJL;
                                        project.HandOverOptionList.get(0).DZLX = value.DZLX;
                                    }
                                }
                            }
                            break;
                        case 2:
                            ((EditText) entity.Root.findViewById(entity.KJID))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    if (String.valueOf(entity.KJID).equals(project.XMBS)) {
                                        project.HandOverOptionList.get(0).DZBDJL = value.DZBDJL;
                                        project.HandOverOptionList.get(0).DZLX = value.DZLX;
                                    }
                                }
                            }
                            break;
                        case 3:
                            ((android.widget.CheckBox) entity.Root
                                    .findViewById(entity.KJID)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    for (HandOverOption option : project.HandOverOptionList) {
                                        if (option.XXBS.equals(entity.KJID)) {
                                            option.DZBDJL = value.DZBDJL;
                                            option.DZLX = value.DZLX;
                                        }
                                    }
                                }
                            }
                            break;
                        case 4:
                            ((android.widget.RadioButton) entity.Root
                                    .findViewById(entity.KJID)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            isFind = true;
                            for (HandOverClassify classify : mForm.HandOverClassifyList) {
                                for (HandOverProject project : classify.HandOverProjectList) {
                                    for (HandOverOption option : project.HandOverOptionList) {
                                        if (option.XXBS.equals(entity.KJID)) {
                                            option.DZBDJL = value.DZBDJL;
                                            option.DZLX = value.DZLX;
                                        }
                                    }
                                }
                            }
                            break;
                    }
                    if (isFind) {
                        break;
                    }
                }
            }
        }
    }
}
