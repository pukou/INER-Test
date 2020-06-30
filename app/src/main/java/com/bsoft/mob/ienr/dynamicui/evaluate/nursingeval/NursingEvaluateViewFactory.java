package com.bsoft.mob.ienr.dynamicui.evaluate.nursingeval;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.method.NumberKeyListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NursingEvaluateActivity;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.nursingeval.KeyValue;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateItem;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecord;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecordDetail;
import com.bsoft.mob.ienr.model.nursingeval.RelationDataParamItem;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.SizeTool;
import com.bsoft.mob.ienr.view.expand.CheckBoxGroupLayout;
import com.bsoft.mob.ienr.view.expand.ClassicFormInputLayout;
import com.bsoft.mob.ienr.view.expand.ClassicInputLayout;
import com.bsoft.mob.ienr.view.expand.ClassicLinesEditView;
import com.bsoft.mob.ienr.view.expand.RadioGroupLayout;
import com.bsoft.mob.ienr.view.expand.dateselect.DateSelectView;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewAdapter;
import com.classichu.adapter.recyclerview.ClassicRecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Classichu on 2017/11/29.
 */

public class NursingEvaluateViewFactory {
    private Context mContext;
    private List<NursingEvaluateItem> neiList = new ArrayList<>();
    private List<Pair<String, String>> pairList = new ArrayList<>();
    private Map<String, List<RadioGroupLayout>> mRadioGroupLayoutListMap = new HashMap<>();
    private Map<String, List<LinearLayout>> mTableListMap = new HashMap<>();
    private List<Pair<LinearLayout, LinearLayout>> classic_organizeList = new ArrayList<>();
    //
    private NursingEvaluateRecord nursingEvaluateRecord;
    private Map<String, String> baseInfoMap;
    private Map<String, List<RelationDataParamItem>> relationDataParamItemListMap = new HashMap<>();
    private boolean mIsCreate;
    private boolean mGlobalInputAble;
    //
    public static final int REQ_CODE_RISK = 200;
    public static final int REQ_CODE_LIFE = 201;

    public NursingEvaluateViewFactory(Context mContext, List<NursingEvaluateItem> neiList) {
        this.mContext = mContext;
        this.neiList = neiList;
    }

    public void clearAll() {
        pairList.clear();
        mRadioGroupLayoutListMap.clear();
        mTableListMap.clear();
        classic_organizeList.clear();
        relationDataParamItemListMap.clear();
    }

    public void parseViewRoot(ViewGroup containerLayout, boolean globalInputAble) {
        LinearLayout rootLayout = new LinearLayout(mContext);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ll_lp_root = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = SizeTool.dp2px(5);
       // ll_lp_root.setMargins(margin, margin, margin, margin);
        //rootLayout.setBackgroundResource(R.drawable.shape_classic_bg_view);
        rootLayout.setLayoutParams(ll_lp_root);
        //
        parseViewOrganize(rootLayout, neiList, globalInputAble);
        containerLayout.addView(rootLayout);
        //
        parseViewEnd(containerLayout, neiList);
    }

    private void parseViewEnd(ViewGroup containerLayout, List<NursingEvaluateItem> neiList) {
        if (neiList == null || neiList.isEmpty()) {
            return;
        }
        for (NursingEvaluateItem nei : neiList) {
            if ("4".equals(nei.XMLB)) {
                //签名
                View view = containerLayout.findViewWithTag(nei.SJXM);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(containerLayout.getContext(), R.color.green));
                    view.setTag(R.id.id_holder_view_data_nei_child, nei);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳转到签名
                            if (mContext instanceof NursingEvaluateActivity) {
                                NursingEvaluateItem nei = (NursingEvaluateItem) v.getTag(R.id.id_holder_view_data_nei_child);
                                String signWho = "1";//护士
                                if (nei != null && "2".equals(nei.XMKZ)) {
                                    signWho = "2";//护士长
                                }
                                String mode = "sign";//护士
                                if (nei != null && !EmptyTool.isBlank(nei.XMQZ)) {
                                    mode = "unsign";//护士长
                                }
                                ((NursingEvaluateActivity) mContext).startSignNewActivityForResultFromItem(mode, signWho);
                            }
                        }
                    });
                }
            } else if ("6".equals(nei.XMLB)) {
                //数据关联
                View view = containerLayout.findViewWithTag(nei.SJXM);
                if (view instanceof TextView) {
                    if (nei.XMKZ.startsWith("2|")) {
                        ((TextView) view).setTextColor(ContextCompat.getColor(containerLayout.getContext(), R.color.hotpink));
                    } else if (nei.XMKZ.startsWith("5|")) {
                        ((TextView) view).setTextColor(ContextCompat.getColor(containerLayout.getContext(), R.color.dodgerblue));
                    }
                    String first = "";
                    String second = "";
                    //"XMKZ": "5|8",
                    String[] xmkzArr = nei.XMKZ.split("\\|");
                    if (xmkzArr != null && xmkzArr.length == 2) {
                        first = xmkzArr[0];
                        second = xmkzArr[1];
                    }
                    RelationDataParamItem relationDataParamItem = new RelationDataParamItem();
                    relationDataParamItem.GLXMH = second;
                    relationDataParamItem.PGXMH = nei.XMXH;
                    String KJLX_Web = parseDataToKJLXWeb(nei);
                    relationDataParamItem.XMKJLX = KJLX_Web;
                    if (relationDataParamItemListMap.containsKey(first)) {
                        relationDataParamItemListMap.get(first).add(relationDataParamItem);
                    } else {
                        List<RelationDataParamItem> relationDataParaItemmArrayList = new ArrayList<>();
                        relationDataParaItemmArrayList.add(relationDataParamItem);
                        relationDataParamItemListMap.put(first, relationDataParaItemmArrayList);
                    }
                    view.setTag(R.id.id_holder_view_xmkz_pair, Pair.create(first, second));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (v.getTag(R.id.id_holder_view_xmkz_pair) == null) {
                                return;
                            }
                            Pair<String, String> pair = (Pair<String, String>) v.getTag(R.id.id_holder_view_xmkz_pair);
                            if ("2".equals(pair.first)) {
                                // 风险
                                String pglx = pair.second;
                                ((NursingEvaluateActivity) mContext).startREQ_RISKActivityForResult(pglx);
                            } else if ("5".equals(pair.first)) {
                                // 体征
                                String xmh = pair.second;
                                ((NursingEvaluateActivity) mContext).startREQ_LIFEActivityForResult(xmh);

                            }
                        }
                    });
                }
            } else {
                //重点项目
                if ("1".equals(nei.ZDXM)) {
                    View view = containerLayout.findViewWithTag(nei.XMXH);
                    if (view instanceof TextView) {
                        ((TextView) view).setTextColor(ContextCompat.getColor(containerLayout.getContext(), R.color.red));
                    }
                }

            }
            //迭代
            parseViewEnd(containerLayout, nei.childItems);
        }

    }

   /* private int[] colorssss = {R.color.red, R.color.green, R.color.pink, R.color.blue, R.color.toastOkColor, R.color.toastInfoColor, R.color.classicButtonClickBg, R.color.classicEditItemFocusBorderColor};
    private int co = 0;*/

    private void parseViewOrganize(LinearLayout rootLayout, List<NursingEvaluateItem> neiList, boolean globalInputAble) {
        mGlobalInputAble = globalInputAble;
        for (NursingEvaluateItem nei : neiList) {
            switch (nei.XMLB) {
                case "1"://分类标签
                    break;
                case "2"://项目标签
                    break;
                case "3"://基本信息项目
                    break;
                case "4"://签名项目
                    break;
                case "5"://组合项目
                    break;
                case "6"://数据关联项目
                    break;
                case "7"://常规项目
                    break;
            }
            if ("0".equals(nei.SJXM)) {
                //顶级分类
                Pair<LinearLayout,TextView> classTextViewLayout = ViewBuildHelper.buildClassTextViewLayout(mContext,nei.XMMC);
                LinearLayout classicLayout = classTextViewLayout.first;
                TextView textView =classTextViewLayout.second;

               /* LinearLayout classicLayout = new LinearLayout(mContext);
                classicLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams ll_lp_classic = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                classicLayout.setGravity(Gravity.CENTER_VERTICAL);
                int padding = SizeTool.dp2px(10);
                classicLayout.setPadding(padding, padding, padding, padding);
                classicLayout.setLayoutParams(ll_lp_classic);*/
                //## classicLayout.setBackgroundResource(R.drawable.shape_classic_bg_expand_group);
                /*Drawable bg_expand_group = ContextCompatHelper.getDrawable(mContext, R.drawable.shape_classic_bg_expand_group,0);
                ViewCompat.setBackground(classicLayout, bg_expand_group);*/
           /*     LinearLayout.LayoutParams ll_lp_txt = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll_lp_txt.weight = 1;
                textView.setLayoutParams(ll_lp_txt);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                // textView.setTextColor(Color.WHITE);
                Drawable leftDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.ic_format_list_bulleted_black_24dp);
                textView.setCompoundDrawablePadding(DimensionTool.getDimensionPx(R.dimen.classic_drawable_padding_primary));
                textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                textView.setText(nei.XMMC);*/
                /*ImageView expandImageView = new ImageView(mContext);
                Drawable rightDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.selector_classic_icon_up_down);
                expandImageView.setImageDrawable(rightDrawable);*/
                classicLayout.setTag(R.id.id_holder_view_classicLayout_expandView, textView);
               /* classicLayout.addView(expandImageView);
                classicLayout.setTag(R.id.id_holder_view_classicLayout_expandImageView, expandImageView);*/
                /**
                 *转换子项!!!!!!!!!!!!!!!!!!!!!!!!!!!
                 */
                LinearLayout organizeLayout = parseViewInner(nei);
                /**
                 * 设置当前分类的监听
                 */
                classicLayout.setTag(R.id.id_holder_view_organizeLayout, organizeLayout);//当前organizeLayout子项分组layout
                classicLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        LinearLayout classicLayout = (LinearLayout) v;
                        TextView expandView = (TextView) classicLayout.getTag(R.id.id_holder_view_classicLayout_expandView);
                        //
                        LinearLayout organizeLayout = (LinearLayout) v.getTag(R.id.id_holder_view_organizeLayout);
                        hideOtherOrganize(organizeLayout);
                        if (organizeLayout.getVisibility() == View.VISIBLE) {
                            organizeLayout.setVisibility(View.GONE);
                            expandView.setSelected(false);
                        } else {
                            organizeLayout.setVisibility(View.VISIBLE);
                            expandView.setSelected(true);
                        }
                    }
                });
                ////
                rootLayout.addView(classicLayout);
                rootLayout.addView(organizeLayout);
                //
                classic_organizeList.add(Pair.create(classicLayout, organizeLayout));
            }

        }


    }

    private void hideOtherOrganize(LinearLayout organizeLayout) {
        if (classic_organizeList == null || classic_organizeList.isEmpty()) {
            return;
        }
        for (Pair<LinearLayout, LinearLayout> viewViewPair : classic_organizeList) {
            LinearLayout classicLayout = viewViewPair.first;
            LinearLayout organizeLayout_Temp = viewViewPair.second;
            if (!organizeLayout.equals(organizeLayout_Temp)) {
                TextView expandView = (TextView) classicLayout.getTag(R.id.id_holder_view_classicLayout_expandView);
                expandView.setSelected(false);
                organizeLayout_Temp.setVisibility(View.GONE);
            }
        }
    }

    public void hideAllOrganize() {
        if (classic_organizeList == null || classic_organizeList.isEmpty()) {
            return;
        }
        for (Pair<LinearLayout, LinearLayout> viewViewPair : classic_organizeList) {
            LinearLayout classicLayout = viewViewPair.first;
            LinearLayout organizeLayout = viewViewPair.second;
            TextView expandView = (TextView) classicLayout.getTag(R.id.id_holder_view_classicLayout_expandView);
            expandView.setSelected(false);
            organizeLayout.setVisibility(View.GONE);
        }
    }


    class TableAdapter extends ClassicRecyclerViewAdapter<Pair<String, String>> {

        public TableAdapter(List<Pair<String, String>> mDataList, int mItemLayoutId) {
            super(mDataList, mItemLayoutId);
        }

        @Override
        public void findBindView(int pos, ClassicRecyclerViewHolder classicRecyclerViewHolder) {
            if (!EmptyTool.isEmpty(mDataList)) {
                TextView id_tv_item_title = classicRecyclerViewHolder.findBindItemView(R.id.id_tv);
                id_tv_item_title.setText(mDataList.get(pos).second);
            }
        }
    }

    private LinearLayout parseViewInner(NursingEvaluateItem nei_parent) {
        if (nei_parent == null) {
            return null;
        }
        List<NursingEvaluateItem> neiList = nei_parent.childItems;
        if (neiList == null || neiList.isEmpty()) {
            return null;
        }
        //////分组容器
        LinearLayout organizeLayout = new LinearLayout(mContext);
        organizeLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ll_lp_organize = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = SizeTool.dp2px(5);
      //  ll_lp_organize.setMargins(margin, margin, margin, margin);
        organizeLayout.setLayoutParams(ll_lp_organize);
        //在下面设置organizeLayout.setBackgroundResource(R.color.orangered);


        for (int i = 0; i < neiList.size(); i++) {
            NursingEvaluateItem nei = neiList.get(i);
            //项目
            String KJLX = nei.KJLX;
            if (!EmptyTool.isBlank(KJLX)) {
                boolean needAddLayout = true;
                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int mar = SizeTool.dp2px(3);
                int xmjb = nei.XMJB == null ? 1 : Integer.valueOf(nei.XMJB);
                ll_lp.setMargins(margin * xmjb, mar, mar, mar);
                layout.setLayoutParams(ll_lp);
                layout.setGravity(Gravity.CENTER_VERTICAL);
                //  layout.setBackgroundResource(R.color.greenyellow);
                switch (KJLX) {
                    case "1"://单行输入
                        if ("3".equals(nei.SJLX)) {
                            ////时间
                            LinearLayout child51 = new LinearLayout(mContext);
                            LinearLayout.LayoutParams ll_lp_child51 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            ll_lp_child51.setMargins(margin, 0, 0, 0);
                            child51.setLayoutParams(ll_lp_child51);
                            child51.setOrientation(LinearLayout.HORIZONTAL);
                            TextView tv31 = new TextView(mContext);
                            tv31.setText("时间");
                            // child51.addView(tv31);
                            DateSelectView dateSelectView = new DateSelectView(mContext);
                            LinearLayout.LayoutParams ll_lp_dsv = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                            ll_lp_dsv.weight = 1;
                            ll_lp_dsv.setMargins(0, 0, margin, 0);
                            dateSelectView.setLayoutParams(ll_lp_dsv);
                            dateSelectView.setupDateText(null, nei.SJGS);
                            dateSelectView.setInputAble(mGlobalInputAble);
                            dateSelectView.setTag(R.id.id_holder_view_data_nei, nei);
                            dateSelectView.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                            dateSelectView.setTag(nei.XMXH);
                            child51.addView(dateSelectView);
                            layout.addView(child51);
                            LinearLayout childLayout8 = parseViewInner(nei);
                            if (childLayout8 != null) {
                                layout.addView(childLayout8);
                            }
                            ////
                        } else {
                            ////普通输入
                            LinearLayout child = new LinearLayout(mContext);
                            LinearLayout.LayoutParams ll_lp_child = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            child.setLayoutParams(ll_lp_child);
                            child.setOrientation(LinearLayout.HORIZONTAL);
                            ClassicFormInputLayout classicFormInputLayout_1 = new ClassicFormInputLayout(mContext);
                            classicFormInputLayout_1.addStartText(nei.QZWB);
                            boolean inputAble = !"0".equals(nei.XGBZ);
                            String hintText = null;
                            if ("1".equals(nei.SJLX)) {
                                //数字 设置上下限
                                if (EmptyTool.isBlank(nei.SJSX) || EmptyTool.isBlank(nei.SJXX)) {
                                    String SJSX = EmptyTool.isBlank(nei.SJSX) ? "100" : nei.SJSX;
                                    String SJXX = EmptyTool.isBlank(nei.SJXX) ? "0" : nei.SJXX;
                                    hintText = SJXX + "-" + SJSX;
                                }
                            }
                            classicFormInputLayout_1.addCenterEditView(null, hintText, null, true, false);
                            classicFormInputLayout_1.addEndText(nei.HZWB);
                            classicFormInputLayout_1.setInputAble(mGlobalInputAble && inputAble);
                            classicFormInputLayout_1.setTag(R.id.id_holder_view_data_nei, nei);
                            classicFormInputLayout_1.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                            classicFormInputLayout_1.setTag(nei.XMXH);
                            if ("1".equals(nei.SJLX)) {
                                //数字  设置输入限制
                                classicFormInputLayout_1.getInputLayoutInCenterLayout().getInput().setKeyListener(new NumberKeyListener() {
                                    @Override
                                    protected char[] getAcceptedChars() {
                                        char[] numberChars = {'1', '2', '3', '4', '5', '6', '7',
                                                '8', '9', '0', '.'};
                                        return numberChars;
                                    }

                                    @Override
                                    public int getInputType() {
                                        return android.text.InputType.TYPE_CLASS_PHONE;
                                    }
                                });
                            }
                            child.addView(classicFormInputLayout_1);
                            layout.addView(child);
                            LinearLayout childLayout1 = parseViewInner(nei);
                            if (childLayout1 != null) {
                                layout.addView(childLayout1);
                            }
                            ///
                        }
                        break;
                    case "2"://多行输入
                        LinearLayout child2 = new LinearLayout(mContext);
                        LinearLayout.LayoutParams ll_lp_child2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        child2.setLayoutParams(ll_lp_child2);
                        child2.setOrientation(LinearLayout.HORIZONTAL);
                  /*      ClassicFormInputLayout classicFormInputLayout_2 = new ClassicFormInputLayout(mContext);
                        classicFormInputLayout_2.addStartText(nei.QZWB);
                        classicFormInputLayout_2.addCenterEditView(null, "多行", true);
                        classicFormInputLayout_2.addEndText(nei.HZWB);
                        classicFormInputLayout_2.setTag(R.id.id_holder_view_data_nei, nei);
                        classicFormInputLayout_2.setTag(nei.XMXH);*/
                        ClassicLinesEditView classicLinesEditView = new ClassicLinesEditView(mContext);
                        classicLinesEditView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        classicLinesEditView.setTag(R.id.id_holder_view_data_nei, nei);
                        classicLinesEditView.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        boolean inputAble = !"0".equals(nei.XGBZ);
                        classicLinesEditView.setInputAble(mGlobalInputAble && inputAble);
                        classicLinesEditView.setTag(nei.XMXH);
                        child2.addView(classicLinesEditView);
                        layout.addView(child2);
                        LinearLayout childLayout2 = parseViewInner(nei);
                        if (childLayout2 != null) {
                            layout.addView(childLayout2);
                        }
                        break;
                    case "3"://单选
                           /* TextView tv2 = new TextView(mContext);
                            tv2.setText(nei.XMMC);
                            layout.addView(tv2);*/
                        RadioGroupLayout radioLayout = new RadioGroupLayout(mContext);
                        radioLayout.setInputAble(mGlobalInputAble);
                        radioLayout.setOrientation(LinearLayout.VERTICAL);
                        radioLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        AppCompatRadioButton radioButton = new AppCompatRadioButton(mContext);
                        radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        radioButton.setTag(R.id.id_holder_view_radioButton_key, nei.XMXH);
                        radioButton.setText(nei.XMMC);
                        //直接对radioButton操作会有问题
                        radioLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                View checkedView = group.findViewById(checkedId);
                                if (checkedView == null || !checkedView.isPressed()) {
                                    //防止setOnCheckedChangeListener循环监听
                                    return;
                                }
                                /**
                                 * 把当前同一父级其他的取消选中
                                 */
                                if (mRadioGroupLayoutListMap == null || mRadioGroupLayoutListMap.isEmpty()) {
                                    return;
                                }
                                NursingEvaluateItem nei = (NursingEvaluateItem) group.getTag(R.id.id_holder_view_data_nei);
                                if (nei == null) {
                                    return;
                                }
                                String SJXM = nei.SJXM;
                                List<RadioGroupLayout> radioGroupList = mRadioGroupLayoutListMap.get(SJXM);
                                if (radioGroupList == null || radioGroupList.isEmpty()) {
                                    return;
                                }
                                for (RadioGroupLayout rg : radioGroupList) {
                                    NursingEvaluateItem nei_rg = (NursingEvaluateItem) rg.getTag(R.id.id_holder_view_data_nei);
                                    LinearLayout childLayout = (LinearLayout) rg.getTag(R.id.id_holder_view_childLayout);
                                    if (!group.equals(rg)) {
                                        //直接挨个rb.setChecked(false)会出问题，下次点击不会变为选中状态
                                        rg.clearCheck();
                                        //
                                        //撤销选中 下级子项隐藏
                                        if (childLayout != null && nei_rg != null) {
                                            //  if ("0".equals(nei.XJZK)) {//不是默认展开的
                                            hideViewGroupAndSetTagAllChildView(childLayout);
                                           /* } else {
                                                childLayout.setVisibility(View.VISIBLE);
                                            }*/
                                            //需要操作的还有选择的下级
                                            // removeNursingEvaluateItemOperate(nei_rg);
                                        }
                                    } else {
                                        //选中 下级子项显示
                                        if (childLayout != null) {
                                            showViewGroupAndSetTagAllChildView(childLayout);
                                            // updateOrAddNursingEvaluateItemOperate(nei_rg);
                                        }
                                    }
                                }
                            }
                        });
                        radioLayout.addView(radioButton);
                        radioLayout.setTag(R.id.id_holder_view_data_nei, nei);
                        radioLayout.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        radioLayout.setTag(nei.XMXH);
                        //
                        if (mRadioGroupLayoutListMap.containsKey(nei.SJXM)) {
                            mRadioGroupLayoutListMap.get(nei.SJXM).add(radioLayout);
                        } else {
                            List<RadioGroupLayout> radioGroupArrayList = new ArrayList<>();
                            radioGroupArrayList.add(radioLayout);
                            mRadioGroupLayoutListMap.put(nei.SJXM, radioGroupArrayList);
                        }
                        /////
                        layout.addView(radioLayout);
                        LinearLayout childLayout = parseViewInner(nei);
                        if (childLayout != null) {
                            // if ("0".equals(nei.XJZK)){
                            hideViewGroupAndSetTagAllChildView(childLayout);
                           /* }else{
                                childLayout3.setVisibility(View.VISIBLE);
                            }*/
                            layout.addView(childLayout);
                            radioLayout.setTag(R.id.id_holder_view_childLayout, childLayout);
                        }
                        break;
                    case "4"://多选
                         /*   TextView tv22 = new TextView(mContext);
                            tv22.setText(nei.XMMC);
                            layout.addView(tv22);*/
                        CheckBoxGroupLayout checkBoxGroupLayout2 = new CheckBoxGroupLayout(mContext);
                        checkBoxGroupLayout2.setOrientation(LinearLayout.VERTICAL);
                        checkBoxGroupLayout2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        AppCompatCheckBox checkBox22 = new AppCompatCheckBox(mContext);
                        checkBox22.setTag(R.id.id_holder_view_checkBox_key, nei.XMXH);
                        checkBox22.setText(nei.XMMC);
                        checkBoxGroupLayout2.addView(checkBox22);
                        checkBoxGroupLayout2.addCheckBox(checkBox22);
                        checkBoxGroupLayout2.setTag(R.id.id_holder_view_data_nei, nei);
                        checkBoxGroupLayout2.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        checkBoxGroupLayout2.setTag(nei.XMXH);
                        checkBoxGroupLayout2.setInputAble(mGlobalInputAble);
                        layout.addView(checkBoxGroupLayout2);
                        LinearLayout childLayout4 = parseViewInner(nei);
                        if (childLayout4 != null) {
                            layout.addView(childLayout4);
                        }
                        break;
                    case "5"://下拉
                        LinearLayout child3 = new LinearLayout(mContext);
                        LinearLayout.LayoutParams ll_lp_child3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        child3.setLayoutParams(ll_lp_child3);
                        child3.setOrientation(LinearLayout.HORIZONTAL);
                        ClassicFormInputLayout classicFormInputLayout_5 = new ClassicFormInputLayout(mContext);
                        classicFormInputLayout_5.addStartText(nei.QZWB);
                        List<Pair<String, String>> stringList5 = new ArrayList<>();
                        if (EmptyTool.isNotEmpty(nei.XMXXKeyValueList)) {
                            List<KeyValue<String, String>> stringList5Temp = new ArrayList<>(nei.XMXXKeyValueList);
                            for (KeyValue<String, String> stringStringKeyValue : stringList5Temp) {
                                stringList5.add(Pair.create(stringStringKeyValue.key, stringStringKeyValue.value));
                            }
                        }
                        boolean inputAble2 = !"0".equals(nei.XGBZ);
                        classicFormInputLayout_5.addCenterEditView(null, "请选择", stringList5, false, false);
                        classicFormInputLayout_5.addEndText(nei.HZWB);
                        classicFormInputLayout_5.setTag(R.id.id_holder_view_data_nei, nei);
                        classicFormInputLayout_5.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        classicFormInputLayout_5.setTag(R.id.id_holder_is_downselect, "1");
                        classicFormInputLayout_5.setInputAble(mGlobalInputAble && inputAble2);
                        classicFormInputLayout_5.setTag(nei.XMXH);
                        child3.addView(classicFormInputLayout_5);
                        layout.addView(child3);
                        LinearLayout childLayout5 = parseViewInner(nei);
                        if (childLayout5 != null) {
                            layout.addView(childLayout5);
                        }
                        break;
                    case "6"://标签
                        LinearLayout child4 = new LinearLayout(mContext);
                        LinearLayout.LayoutParams ll_lp_child4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        child4.setLayoutParams(ll_lp_child4);
                        child4.setPadding(10, 10, 10, 10);
                        child4.setOrientation(LinearLayout.HORIZONTAL);
                        child4.setBackgroundColor(Color.GREEN);
                        TextView tv3 = new TextView(mContext);
                        tv3.setText(nei.XMMC);
                        tv3.setTag(R.id.id_holder_view_data_nei, nei);
                        tv3.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        tv3.setTag(nei.XMXH);
                        child4.addView(tv3);
                        layout.addView(child4);
                        LinearLayout childLayout6 = parseViewInner(nei);
                        if (childLayout6 != null) {
                            layout.addView(childLayout6);
                        }
                        break;
                    case "7"://表格
                        //在下面处理
                        needAddLayout = false;
                        break;
                    case "9"://文本
                        LinearLayout child6 = new LinearLayout(mContext);
                        LinearLayout.LayoutParams ll_lp_child6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        child6.setLayoutParams(ll_lp_child6);
                        child6.setOrientation(LinearLayout.HORIZONTAL);
                        TextView tv6 = new TextView(mContext);
                        tv6.setText(nei.XMMC);
                        tv6.setTag(R.id.id_holder_view_data_nei, nei);
                        tv6.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        tv6.setTag(nei.XMXH);
                        child6.addView(tv6);
                        layout.addView(child6);
                        LinearLayout childLayout9 = parseViewInner(nei);
                        if (childLayout9 != null) {
                            layout.addView(childLayout9);
                        }
                        break;
                    case "10"://普通输入
                        LinearLayout child5 = new LinearLayout(mContext);
                        LinearLayout.LayoutParams ll_lp_child5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        child5.setLayoutParams(ll_lp_child5);
                        child5.setOrientation(LinearLayout.HORIZONTAL);
                        EditText et = ViewBuildHelper.buildEditTextMatchWrap(mContext,nei.XMMC);
                        et.setTag(R.id.id_holder_view_data_nei, nei);
                        et.setTag(R.id.id_holder_view_data_nei_parent, nei_parent);
                        et.setTag(nei.XMXH);
                        child5.addView(et);
                        layout.addView(child5);
                        LinearLayout childLayout10 = parseViewInner(nei);
                        if (childLayout10 != null) {
                            layout.addView(childLayout10);
                        }
                        break;
                    default:
                        break;
                }

                ////////
                switch (nei.XJKJLX) {
                    case "7"://下级是表格
                        LinearLayout layout_table = new LinearLayout(mContext);
                        LinearLayout.LayoutParams ll_lp_child77 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layout_table.setLayoutParams(ll_lp_child77);
                        //layout_table.setPadding(10, 10, 10, 10);
                        layout_table.setOrientation(LinearLayout.VERTICAL);
                        layout_table.setBackgroundResource(R.drawable.shape_classic_bg_view);
                        LinearLayout layout_table_row = parseTable(nei, layout_table);
                        if (layout_table_row != null) {
                            layout_table.addView(layout_table_row);
                            layout_table_row.setTag(layout_table);//
                            //
                            String myKey = nei.SJXM + "_" + nei.XMXH;
                            if (mTableListMap.containsKey(myKey)) {
                                mTableListMap.get(myKey).add(layout_table_row);
                            } else {
                                List<LinearLayout> layout_rowArrayList = new ArrayList<>();
                                layout_rowArrayList.add(layout_table_row);
                                mTableListMap.put(myKey, layout_rowArrayList);
                            }
                            /////
                        }
                        layout.addView(layout_table);
                        //
                        TextView tv_add = new TextView(mContext);
                        LinearLayout.LayoutParams ll_lp_add = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        ll_lp_add.gravity = Gravity.CENTER;
                        tv_add.setLayoutParams(ll_lp_add);
                        tv_add.setText("添加");
                        tv_add.setPadding(0, 30, 0, 30);
                        tv_add.setTag(layout_table);
                        tv_add.setTag(R.id.id_holder_view_table_child, nei);
                        tv_add.setTextColor(Color.WHITE);
                        tv_add.setGravity(Gravity.CENTER);
                        tv_add.setBackgroundResource(R.drawable.selector_classic_bg_click_o_colored);
                        tv_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout layout_table = (LinearLayout) v.getTag();
                                //
                                if (v.getTag(R.id.id_holder_view_table_child) != null && v.getTag(R.id.id_holder_view_table_child) instanceof NursingEvaluateItem) {
                                    NursingEvaluateItem nei = (NursingEvaluateItem) v.getTag(R.id.id_holder_view_table_child);
                                    LinearLayout layout_table_row = parseTable(nei, layout_table);
                                    if (layout_table_row != null) {
                                        layout_table.addView(layout_table_row);
                                        //
                                        String myKey = nei.SJXM + "_" + nei.XMXH;
                                        if (mTableListMap.containsKey(myKey)) {
                                            mTableListMap.get(myKey).add(layout_table_row);
                                        } else {
                                            List<LinearLayout> layout_rowArrayList = new ArrayList<>();
                                            layout_rowArrayList.add(layout_table_row);
                                            mTableListMap.put(myKey, layout_rowArrayList);
                                        }
                                        /////
                                    }
                                }

                            }
                        });
                        layout.addView(tv_add);
                        break;
                }
                if (needAddLayout) {
                    organizeLayout.addView(layout);
                }
            }
        }
        //"0".equals(nei_parent.SJXM)  不是 最外层的layout
        if (organizeLayout.getChildCount() > 1&&!"0".equals(nei_parent.SJXM)) {
            organizeLayout.setBackgroundResource(R.drawable.shape_classic_bg_view);
        }

        return organizeLayout;
    }

    private void showViewGroupAndSetTagAllChildView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.setTag(R.id.id_holder_view_visibility, "VISIBLE");
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                showViewGroupAndSetTagAllChildView((ViewGroup) view);
            } else {
                view.setTag(R.id.id_holder_view_visibility, "VISIBLE");
            }
        }
    }

    private void hideViewGroupAndSetTagAllChildView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        viewGroup.setVisibility(View.GONE);
        viewGroup.setTag(R.id.id_holder_view_visibility, "GONE");
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                hideViewGroupAndSetTagAllChildView((ViewGroup) view);
            } else {
                view.setTag(R.id.id_holder_view_visibility, "GONE");
            }
        }
    }

    private LinearLayout parseTable(NursingEvaluateItem nei, LinearLayout layout_table_4_deleteClick) {
        if (nei == null) {
            return null;
        }
        List<NursingEvaluateItem> childItems = nei.childItems;
        if (childItems == null || childItems.isEmpty()) {
            return null;
        }
        LinearLayout layout_table_row = new LinearLayout(mContext);
        LinearLayout.LayoutParams ll_lp_table_row = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp_table_row.setMargins(0, 5, 0, 5);
        layout_table_row.setLayoutParams(ll_lp_table_row);
        layout_table_row.setOrientation(LinearLayout.VERTICAL);
        layout_table_row.setBackgroundColor(Color.parseColor("#0A000000"));
        layout_table_row.setGravity(Gravity.TOP | Gravity.RIGHT | Gravity.END);
        //
        RelativeLayout layout_title = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rl_lp_layout_title = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_title.setLayoutParams(rl_lp_layout_title);
        layout_title.setGravity(Gravity.CENTER_VERTICAL);
        //layout_title.setPadding(10,10,10,10);
        ImageView deleteIcon = new ImageView(mContext);
        RelativeLayout.LayoutParams rl_lp_iv = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl_lp_iv.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT);
        rl_lp_iv.setMargins(0, 15, 15, 0);
        deleteIcon.setImageDrawable(ContextCompatHelper.getDrawable(mContext, R.drawable.ic_cancel_black_24dp));
        deleteIcon.setLayoutParams(rl_lp_iv);
        deleteIcon.setTag(R.id.id_holder_view_layout_table_4_deleteClick, layout_table_4_deleteClick);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NursingEvaluateItem nei = (NursingEvaluateItem) v.getTag(R.id.id_holder_view_data_nei);
                LinearLayout layout_table_4_deleteClick = (LinearLayout) v.getTag(R.id.id_holder_view_layout_table_4_deleteClick);
                LinearLayout layout_table_row = (LinearLayout) v.getTag();
                if (layout_table_row != null && layout_table_4_deleteClick != null) {
                    layout_table_4_deleteClick.removeView(layout_table_row);
                    String myKey = nei.SJXM + "_" + nei.XMXH;
                    if (mTableListMap.containsKey(myKey)) {
                        List<LinearLayout> layout_table_row_List = mTableListMap.get(myKey);
                        layout_table_row_List.remove(layout_table_row);
                        for (int i = 0; i < layout_table_row_List.size(); i++) {
                            LinearLayout layout_table_row_temp = layout_table_row_List.get(i);
                            TextView tv_title_num = (TextView) layout_table_row_temp.getTag(R.id.id_holder_view_tv_title_num);
                            if (tv_title_num != null) {
                                tv_title_num.setText(String.valueOf(i + 1));
                            }
                        }
                    }
                }

            }
        });
        deleteIcon.setTag(layout_table_row);///
        deleteIcon.setTag(R.id.id_holder_view_data_nei, nei);
        TextView tv_title_num = new TextView(mContext);
        int num = 0;
        String myKey = nei.SJXM + "_" + nei.XMXH;
        if (mTableListMap != null && mTableListMap.get(myKey) != null && !mTableListMap.get(myKey).isEmpty()) {
            num = mTableListMap.get(myKey).size();
        }
        num++;//原来的数量累加1
        tv_title_num.setText(String.valueOf(num));
        tv_title_num.setTextColor(Color.WHITE);
        int sizeeee = SizeTool.dp2px(20);
        RelativeLayout.LayoutParams rl_lp_title = new RelativeLayout.LayoutParams(sizeeee, sizeeee);
        rl_lp_title.setMargins(15, 15, 0, 0);
        tv_title_num.setLayoutParams(rl_lp_title);
        tv_title_num.setGravity(Gravity.CENTER);
        tv_title_num.setBackgroundResource(R.drawable.shape_classic_bg_view_o);
        layout_title.addView(tv_title_num);
        layout_title.addView(deleteIcon);
        layout_table_row.setTag(R.id.id_holder_view_tv_title_num, tv_title_num);
        layout_table_row.addView(layout_title);
        for (NursingEvaluateItem childItem : childItems) {
            LinearLayout layout_table_row_child = new LinearLayout(mContext);
            LinearLayout.LayoutParams ll_lp_table_row_child = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_table_row_child.setLayoutParams(ll_lp_table_row_child);
            layout_table_row_child.setPadding(10, 10, 10, 10);
            layout_table_row_child.setOrientation(LinearLayout.VERTICAL);
            TextView tv_child = new TextView(mContext);
            tv_child.setText(childItem.XMMC);
            tv_child.setTag(R.id.id_holder_view_data_nei, childItem);
            tv_child.setTag(childItem.XMXH);
            layout_table_row_child.addView(tv_child);

            LinearLayout childLayout_child = parseViewInner(childItem);//!!!childItem
            if (childLayout_child != null) {
                layout_table_row_child.addView(childLayout_child);
            }
            layout_table_row.addView(layout_table_row_child);
        }
        return layout_table_row;
    }

    private void removeNursingEvaluateItemOperate(NursingEvaluateItem nei) {
        removeNursingEvaluateItem(nei.XMXH);
        removeNursingEvaluateItemChild(nei.childItems);
    }

    private void updateOrAddNursingEvaluateItemOperate(NursingEvaluateItem nei) {
        updateOrAddNursingEvaluateItem(nei.XMXH, nei);
        updateOrAddNursingEvaluateItemChild(nei.childItems);
    }

    public Map<String, List<RelationDataParamItem>> getRelationDataParamItemListMap() {
        return relationDataParamItemListMap;
    }

    public Pair<NursingEvaluateRecord, List<NursingEvaluateItem>> getAllValueSimple(ViewGroup vp) {
        getAllValueInner(vp);
        return Pair.create(nursingEvaluateRecord, neiList);
    }

    @Deprecated
    public List<Pair<String, String>> getAllValuePair(ViewGroup vp) {
        getAllValueInner(vp);
        return pairList;
    }

    public boolean isCreate() {
        return mIsCreate;
    }

    /**
     * 获取值  更新数据源
     */
    private void getAllValueInner(ViewGroup vp) {
        if (vp.getChildCount() <= 0) {
            return;
        }
        for (int i = 0; i < vp.getChildCount(); i++) {
            View view = vp.getChildAt(i);
            if (view instanceof ClassicFormInputLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    List<KeyValue<String, String>> XMXXKeyValueList = nei.XMXXKeyValueList;
                    ClassicFormInputLayout cfil = (ClassicFormInputLayout) view;
                    ClassicInputLayout cil = cfil.getInputLayoutInCenterLayout();
                    if (cil != null && cil.getInput() != null) {
                        String valueOrCode = cil.getInput().getText().toString();
                        //如果下拉选项Value替换为Code
                        if (XMXXKeyValueList != null && !XMXXKeyValueList.isEmpty()) {
                            for (KeyValue<String, String> stringKeyValue : XMXXKeyValueList) {
                                if (stringKeyValue.value.equals(valueOrCode)) {
                                    valueOrCode = stringKeyValue.key;
                                    break;
                                }
                            }
                        }
                        pairList.add(Pair.create(key, valueOrCode + "ClassicFormInputLayout"));
                        nei.XMQZ = valueOrCode;
                        if (nei_parent != null) {
                            nei_parent.childValueMap.put(key, valueOrCode);
                        }
                        updateViewIsGoneValueMap(nei_parent, view);
                        updateNursingEvaluateItem(key, nei);
                    }

                }
            } else if (view instanceof ClassicLinesEditView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    ClassicLinesEditView clev = (ClassicLinesEditView) view;
                    String value = clev.getContentText();
                    pairList.add(Pair.create(key, value + "ClassicLinesEditView"));
                    nei.XMQZ = value;
                    if (nei_parent != null) {
                        nei_parent.childValueMap.put(key, value);
                    }
                    updateViewIsGoneValueMap(nei_parent, view);
                    updateNursingEvaluateItem(key, nei);
                }
            } else if (view instanceof CheckBoxGroupLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    CheckBoxGroupLayout cbgl = (CheckBoxGroupLayout) view;
                    Pair<String, String> keyValuePair = cbgl.getCheckedCheckBoxKeyValue();
                    if (keyValuePair != null && keyValuePair.first != null) {
                        String value = keyValuePair.second;
                        if (!EmptyTool.isBlank(value)) {
                            pairList.add(keyValuePair);
                            nei.XMQZ = "√";
                            //  nei_parent.XMQZ = nei.XMXH;
                            if (nei_parent != null) {
                                nei_parent.childValueMap.put(key, "√");
                            }
                            updateOrAddNursingEvaluateItemOperate(nei);
                        } else {
                            nei.XMQZ = "";
                            //   nei_parent.XMQZ = "";
                            if (nei_parent != null) {
                                nei_parent.childValueMap.put(key, "");
                            }
                            //移除没有选择RadioButton的Detail记录
                            //需要操作的是选择的下级
                            removeNursingEvaluateItemOperate(nei);
                        }
                    } else {
                        nei.XMQZ = "";
                        //    nei_parent.XMQZ = "";
                        if (nei_parent != null) {
                            nei_parent.childValueMap.put(key, "");
                        }
                        //移除没有选择RadioButton的Detail记录
                        //需要操作的是选择的下级
                        removeNursingEvaluateItemOperate(nei);
                    }
                    updateViewIsGoneValueMap(nei_parent, view);
                }
            } else if (view instanceof AppCompatEditText) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    AppCompatEditText et = (AppCompatEditText) view;
                    String value = et.getText().toString();
                    pairList.add(Pair.create(key, value + "EditText"));
                    nei.XMQZ = value;
                    if (nei_parent != null) {
                        nei_parent.childValueMap.put(key, value);
                    }
                    updateViewIsGoneValueMap(nei_parent, view);
                    updateNursingEvaluateItem(key, nei);
                }
            } else if (view instanceof RadioGroupLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    RadioGroupLayout radioLayout = (RadioGroupLayout) view;
                    RadioButton rb = (RadioButton) radioLayout.findViewById(radioLayout.getCheckedRadioButtonId());
                    if (rb != null && rb.isChecked()) {
                        String value = rb.getText().toString();
                        pairList.add(Pair.create(key, value + "RadioGroup"));
                        nei.XMQZ = "√";
                        // nei_parent.XMQZ = nei.XMXH;
                        if (nei_parent != null) {
                            nei_parent.childValueMap.put(key, "√");
                        }
                        updateOrAddNursingEvaluateItemOperate(nei);
                    } else {
                        nei.XMQZ = "";
                        // nei_parent.XMQZ = "";
                        if (nei_parent != null) {
                            nei_parent.childValueMap.put(key, "");
                        }
                        //移除没有选择RadioButton的Detail记录
                        removeNursingEvaluateItemOperate(nei);
                    }
                    updateViewIsGoneValueMap(nei_parent, view);
                }
            }/* else if (view instanceof CheckBox) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String key = nei.XMXH;
                    CheckBox cb = (CheckBox) view;
                    if (cb.isChecked()) {
                        String value = cb.getText().toString();
                        pairList.add(Pair.create(key, value + "CheckBox"));
                        nei.XMQZ = "√";
                        updateOrAddNursingEvaluateItem(key, nei);
                    }else{
                        //移除没有选择的CheckBox的Detail记录
                        removeNursingEvaluateItem(nei.XMXH);
                    }
                }
            }*/ else if (view instanceof DateSelectView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    DateSelectView dsv = (DateSelectView) view;
                    String value = dsv.getNowData();
                    pairList.add(Pair.create(key, value + "DateSelectView"));
                    nei.XMQZ = value;
                    if (nei_parent != null) {
                        nei_parent.childValueMap.put(key, value);
                    }
                    updateViewIsGoneValueMap(nei_parent, view);
                    updateNursingEvaluateItem(key, nei);
                }
            } else if (view instanceof TextView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    NursingEvaluateItem nei_parent = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei_parent);
                    String key = nei.XMXH;
                    TextView tv = (TextView) view;
                    String value = tv.getText().toString();
                    pairList.add(Pair.create(key, value + "TextView"));
                    nei.XMQZ = value;
                    if (nei_parent != null) {
                        nei_parent.childValueMap.put(key, value);
                    }
                    //###不需要updateNursingEvaluateItem(key, nei);
                }
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                getAllValueInner(viewGroup);
            }
        }

    }

    private void updateViewIsGoneValueMap(NursingEvaluateItem nei_parent, View view) {
        if (view == null) {
            return;
        }
        if (nei_parent != null && view.getTag(R.id.id_holder_view_visibility) != null) {
            nei_parent.childViewIsGone = view.getTag(R.id.id_holder_view_visibility).equals("GONE");
        }

    }

    private boolean checkHasValue(NursingEvaluateItem nei) {
        if (nei == null) {
            return false;
        }
        if (!EmptyTool.isBlank(nei.XMQZ)) {
            return true;
        }
        for (NursingEvaluateItem childItem : nei.childItems) {
            //
            if (!EmptyTool.isBlank(nei.XMQZ)) {
                return true;
            }
            //返回子
            checkHasValue(childItem);
        }
        return false;
    }

    public void setAllValueSimple(ViewGroup vp, NursingEvaluateRecord ned, Map<String, String> baseInfo, boolean isCreate) {
        nursingEvaluateRecord = ned;///!!!!
        baseInfoMap = baseInfo;///!!!!
        mIsCreate = isCreate;
        setAllValueSimpleInner(vp, nursingEvaluateRecord.detailList);
    }

    private void removeNursingEvaluateItem(String XMXH) {
        if (nursingEvaluateRecord.detailList == null || nursingEvaluateRecord.detailList.isEmpty() || EmptyTool.isBlank(XMXH)) {
            return;
        }
        for (NursingEvaluateRecordDetail nursingEvaluateRecordDetail : nursingEvaluateRecord.detailList) {
            if (XMXH.equals(nursingEvaluateRecordDetail.XMXH)) {
                nursingEvaluateRecord.detailList.remove(nursingEvaluateRecordDetail);
                break;
            }
        }
    }

    private void updateOrAddNursingEvaluateItemChild(List<NursingEvaluateItem> neiList) {
        if (neiList.isEmpty()) {
            return;
        }
        for (NursingEvaluateItem childItem : neiList) {
            updateOrAddNursingEvaluateItem(childItem.XMXH, childItem);
            //迭代子项
            updateOrAddNursingEvaluateItemChild(childItem.childItems);
        }
    }

    private void removeNursingEvaluateItemChild(List<NursingEvaluateItem> neiList) {
        if (neiList.isEmpty()) {
            return;
        }
        for (NursingEvaluateItem childItem : neiList) {
            removeNursingEvaluateItem(childItem.XMXH);
            //迭代子项
            removeNursingEvaluateItemChild(childItem.childItems);
        }
    }

    private void updateNursingEvaluateItem(String XMXH, NursingEvaluateItem nei) {
        if (nursingEvaluateRecord.detailList == null || nursingEvaluateRecord.detailList.isEmpty() || EmptyTool.isBlank(XMXH)) {
            return;
        }
        for (NursingEvaluateRecordDetail nursingEvaluateRecordDetail : nursingEvaluateRecord.detailList) {
            if (XMXH.equals(nursingEvaluateRecordDetail.XMXH)) {
                nursingEvaluateRecordDetail.XMNR = nei.XMQZ;
                break;
            }
        }
    }

    private void updateOrAddNursingEvaluateItem(String XMXH, NursingEvaluateItem nei) {
        if (nursingEvaluateRecord.detailList == null || nursingEvaluateRecord.detailList.isEmpty() || EmptyTool.isBlank(XMXH)) {
            return;
        }
        boolean needAdd = true;
        for (NursingEvaluateRecordDetail nursingEvaluateRecordDetail : nursingEvaluateRecord.detailList) {
            if (XMXH.equals(nursingEvaluateRecordDetail.XMXH)) {
                nursingEvaluateRecordDetail.XMNR = nei.XMQZ;
                needAdd = false;
                break;
            }
        }
        if (needAdd) {
            NursingEvaluateRecordDetail detail = parseDataToNursingEvaluateRecordDetail(nei);
            nursingEvaluateRecord.detailList.add(detail);
        }
    }

    private String findBaseInfoByXMKZ(String XMKZ) {
        if (baseInfoMap == null || EmptyTool.isBlank(XMKZ)) {
            return null;
        }
        return baseInfoMap.get(XMKZ);
    }

    private String findXMNRByXMXH(String XMXH) {
        if (nursingEvaluateRecord == null || nursingEvaluateRecord.detailList == null || nursingEvaluateRecord.detailList.isEmpty() || EmptyTool.isBlank(XMXH)) {
            return null;
        }
        for (NursingEvaluateRecordDetail nursingEvaluateRecordDetail : nursingEvaluateRecord.detailList) {
            if (XMXH.equals(nursingEvaluateRecordDetail.XMXH)) {
                return nursingEvaluateRecordDetail.XMNR;
            }
        }
        return null;
    }

    private void setAllValueSimpleInner(ViewGroup vp, List<NursingEvaluateRecordDetail> recordDetailList) {
        if (vp.getChildCount() <= 0 || recordDetailList == null) {
            return;
        }
        for (int i = 0; i < vp.getChildCount(); i++) {
            View view = vp.getChildAt(i);

            /////
            if (view instanceof ClassicFormInputLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        ClassicFormInputLayout cfil = (ClassicFormInputLayout) view;
                        ClassicInputLayout cil = cfil.getInputLayoutInCenterLayout();
                        if ("1".equals(view.getTag(R.id.id_holder_is_downselect))) {
                            cfil.setTextByKey(nei.XMQZ);
                        } else {
                            if (cil != null && cil.getInput() != null) {
                                cil.getInput().setText(nei.XMQZ);
                            }
                        }
                    }
                }
            } else if (view instanceof ClassicLinesEditView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        ClassicLinesEditView clev = (ClassicLinesEditView) view;
                        clev.setContentText(nei.XMQZ);
                    }
                }
            } else if (view instanceof CheckBoxGroupLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        CheckBoxGroupLayout cbgl = (CheckBoxGroupLayout) view;
                        for (int j = 0; j < cbgl.getChildCount(); j++) {
                            CheckBox checkBox = (CheckBox) cbgl.getChildAt(j);
                            //只有一个checkBox
                            checkBox.setChecked(true);
                          /*  if (childLayout!=null){
                                childLayout.setVisibility(View.VISIBLE);
                            }*/
                            break;
                        }
                    }
                }
            } else if (view instanceof EditText) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        EditText et = (EditText) view;
                        et.setText(nei.XMQZ);
                    }
                }
            } else if (view instanceof RadioGroup) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        RadioGroup rg = (RadioGroup) view;
                        for (int j = 0; j < rg.getChildCount(); j++) {
                            RadioButton radioButton = (RadioButton) rg.getChildAt(j);
                            //只有一个radioButton
                            //String key = (String) radioButton.getTag(R.id.id_holder_view_radioButton_key);
                            rg.check(radioButton.getId());
                            LinearLayout childLayout = (LinearLayout) rg.getTag(R.id.id_holder_view_childLayout);
                            if (childLayout != null) {
                                showViewGroupAndSetTagAllChildView(childLayout);
                            }
                            break;
                        }
                    }
                }
            } else if (view instanceof CheckBox) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        CheckBox cb = (CheckBox) view;
                        cb.setChecked(true);
                    }
                }
            } else if (view instanceof DateSelectView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //新增的时候 处理基本信息数据
                    XMNR = findCreateBaseData(nei, XMNR);
                    initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        DateSelectView dsv = (DateSelectView) view;
                        dsv.setupDateText(nei.XMQZ);
                    }
                }
            } else if (view instanceof TextView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = findXMNRByXMXH(nei.XMXH);
                    //###不需要 initDetailData(nei);
                    if (!EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        TextView tv = (TextView) view;
                        tv.setText(nei.XMQZ);
                    }
                }
            } else if (view instanceof ViewGroup) {
                ///
                ViewGroup viewGroup = (ViewGroup) view;
                setAllValueSimpleInner(viewGroup, recordDetailList);
            }
            ////
        }
    }

    public void setAllEmptyValue(ViewGroup vp, String fixStr) {
        if (vp.getChildCount() <= 0 || EmptyTool.isBlank(fixStr)) {
            return;
        }
        for (int i = 0; i < vp.getChildCount(); i++) {
            View view = vp.getChildAt(i);

            /////
            if (view instanceof ClassicFormInputLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = nei.XMQZ;
                    if (EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = fixStr;
                        ClassicFormInputLayout cfil = (ClassicFormInputLayout) view;
                        ClassicInputLayout cil = cfil.getInputLayoutInCenterLayout();
                        if ("1".equals(view.getTag(R.id.id_holder_is_downselect))) {
                            //####cfil.setTextByKey(nei.XMQZ);
                        } else {
                            if (cil != null && cil.getInput() != null) {
                                cil.getInput().setText(nei.XMQZ);
                            }
                        }
                    }
                }
            } else if (view instanceof ClassicLinesEditView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = nei.XMQZ;
                    if (EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = fixStr;
                        ClassicLinesEditView clev = (ClassicLinesEditView) view;
                        clev.setContentText(nei.XMQZ);
                    }
                }
            } else if (view instanceof EditText) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    String XMNR = nei.XMQZ;
                    if (EmptyTool.isBlank(XMNR)) {
                        nei.XMQZ = XMNR;
                        EditText et = (EditText) view;
                        et.setText(nei.XMQZ);
                    }
                }
            } else if (view instanceof ViewGroup) {
                ///
                ViewGroup viewGroup = (ViewGroup) view;
                setAllEmptyValue(viewGroup, fixStr);
            }
            ////
        }
    }


    private String findCreateBaseData(NursingEvaluateItem nei, String XMNR) {
        if (mIsCreate && "3".equals(nei.XMLB) && !EmptyTool.isBlank(nei.XMKZ)) {
            String baseinfo = findBaseInfoByXMKZ(nei.XMKZ);
            if (!EmptyTool.isBlank(baseinfo)) {
                XMNR = baseinfo;
            }
        }
        return XMNR;
    }

    private void initDetailData(NursingEvaluateItem nei) {
        if (!mIsCreate) {
            return;
        }
        if (nei != null && nursingEvaluateRecord != null && nursingEvaluateRecord.detailList != null) {
            NursingEvaluateRecordDetail nursingEvaluateRecordDetail = parseDataToNursingEvaluateRecordDetail(nei);
            nursingEvaluateRecord.detailList.add(nursingEvaluateRecordDetail);
        }
    }

    private NursingEvaluateRecordDetail parseDataToNursingEvaluateRecordDetail(NursingEvaluateItem nei) {
        NursingEvaluateRecordDetail nursingEvaluateRecordDetail = new NursingEvaluateRecordDetail();
        // nursingEvaluateRecordDetail.MXXH = nei.;
        // nursingEvaluateRecordDetail.JLXH;=nei.j
        nursingEvaluateRecordDetail.XMXH = nei.XMXH;
        nursingEvaluateRecordDetail.SJXM = nei.SJXM;
        nursingEvaluateRecordDetail.SJXMMC = nei.SJXMMC;
        nursingEvaluateRecordDetail.XMNR = nei.XMQZ;
       /* nursingEvaluateRecordDetail.DZLX=nei.
        nursingEvaluateRecordDetail.DZBDJL=nei.jlx*/
        String KJLX_Web = parseDataToKJLXWeb(nei);
        nursingEvaluateRecordDetail.KJLX_Web = KJLX_Web;
        return nursingEvaluateRecordDetail;
    }

    private String parseDataToKJLXWeb(NursingEvaluateItem nei) {
        String KJLX_Web = null;
        if (!EmptyTool.isBlank(nei.KJLX)) {
            switch (nei.KJLX) {
                case "1"://单行输入
                    KJLX_Web = "1";//1:text
                    /**
                     * SJLX
                     * 1.数字
                     * 2.字符
                     * 3.日期
                     */
                    if (EmptyTool.isBlank(nei.SJLX)) {
                        if (nei.SJLX.equals("1")) {
                            KJLX_Web = "2";//2:numberbox
                        } else if (nei.SJLX.equals("3")) {
                            KJLX_Web = "3";//3:datebox
                            if (!EmptyTool.isBlank(nei.SJGS) && nei.SJGS.contains(":")) {
                                KJLX_Web = "4";//4:datetimebox
                            }

                        }
                    }
                    break;
                case "2"://多行输入
                    KJLX_Web = "1";//1:text
                    break;
                case "3"://单项选择
                    KJLX_Web = "5";//5:checkbox
                    break;
                case "4"://多项选择
                    KJLX_Web = "5";//5:checkbox
                    break;
                case "5"://下拉列表
                    KJLX_Web = "6";//6:combobox
                    break;
            }
        }
        return KJLX_Web;
    }

    /**
     * 通过相关ID设置值setup 1
     *
     * @param vp
     * @param nei_In
     */
    @Deprecated
    public void setOneValue(ViewGroup vp, NursingEvaluateItem nei_In) {
        if (nei_In == null) {
            return;
        }
        setAllValueInner(vp, nei_In);

    }


    /**
     * 通过相关ID设置值setup 1
     * 外层迭代数据 List
     *
     * @param vp
     * @param neiList_In
     */
    @Deprecated
    public void setAllValue(ViewGroup vp, List<NursingEvaluateItem> neiList_In) {
        if (neiList_In == null || neiList_In.isEmpty()) {
            return;
        }
        for (NursingEvaluateItem nei_in : neiList_In) {
            //当前设置取值
            setAllValueInner(vp, nei_in);

            //继续迭代List<NursingEvaluateItem>
            setAllValue(vp, nei_in.childItems);
        }


    }

    /**
     * 通过相关ID设置值setup 2
     * 内层迭代 ViewGroup
     * 配合setAllChildValue
     *
     * @param vp
     * @param nei_in
     */
    @Deprecated
    private void setAllValueInner(ViewGroup vp, NursingEvaluateItem nei_in) {
        if (vp.getChildCount() <= 0 || nei_in == null) {
            return;
        }
        for (int i = 0; i < vp.getChildCount(); i++) {
            View view = vp.getChildAt(i);

            String XMXH_In = nei_in.XMXH;
            String XMQZ_In = nei_in.XMQZ;
            /////
            if (view instanceof ClassicFormInputLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        ClassicFormInputLayout cfil = (ClassicFormInputLayout) view;
                        ClassicInputLayout cil = cfil.getInputLayoutInCenterLayout();
                        if (cil != null && cil.getInput() != null) {
                            cil.getInput().setText(nei.XMQZ);
                        }
                    }
                }
            } else if (view instanceof ClassicLinesEditView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        ClassicLinesEditView clev = (ClassicLinesEditView) view;
                        clev.setContentText(nei.XMQZ);
                    }
                }
            } else if (view instanceof CheckBoxGroupLayout) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        CheckBoxGroupLayout cbgl = (CheckBoxGroupLayout) view;
                        cbgl.setCheckedCheckBoxKey(nei.XMQZ);
                    }
                }
            } else if (view instanceof EditText) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        EditText et = (EditText) view;
                        et.setText(nei.XMQZ);
                    }
                }
            } else if (view instanceof RadioGroup) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        RadioGroup rg = (RadioGroup) view;
                        for (int j = 0; j < rg.getChildCount(); j++) {
                            RadioButton radioButton = (RadioButton) rg.getChildAt(j);
                            String key = (String) radioButton.getTag(R.id.id_holder_view_radioButton_key);
                            if (key.equals(nei.XMQZ)) {
                                radioButton.setChecked(true);
                                break;
                            }
                        }
                    }
                }
            } else if (view instanceof CheckBox) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        CheckBox cb = (CheckBox) view;
                        cb.setChecked(true);
                    }
                }
            } else if (view instanceof DateSelectView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        DateSelectView dsv = (DateSelectView) view;
                        dsv.setupDateText(nei.XMQZ);
                    }
                }
            } else if (view instanceof TextView) {
                if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                    NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                    if (XMXH_In != null && XMXH_In.equals(nei.XMXH)) {
                        nei.XMQZ = XMQZ_In;
                        TextView tv = (TextView) view;
                        tv.setText(nei.XMQZ);
                    }
                }
            } else if (view instanceof ViewGroup) {
                ///
                ViewGroup viewGroup = (ViewGroup) view;
                setAllValueInner(viewGroup, nei_in);
            }
            ////
        }
    }

    /**
     * 通过相关TAG设置值
     *
     * @param recordDetailList
     */
    public void setAllValueByTag(ViewGroup vp, List<NursingEvaluateRecordDetail> recordDetailList) {
        if (vp.getChildCount() <= 0 || recordDetailList == null) {
            return;
        }
        for (NursingEvaluateRecordDetail detail : recordDetailList) {
            View view = vp.findViewWithTag(detail.XMXH);
            if (view != null) {
                /////
                if (view instanceof ClassicFormInputLayout) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        ClassicFormInputLayout cfil = (ClassicFormInputLayout) view;
                        ClassicInputLayout cil = cfil.getInputLayoutInCenterLayout();
                        if ("1".equals(view.getTag(R.id.id_holder_is_downselect))) {
                            cfil.setTextByKey(nei.XMQZ);
                        } else {
                            if (cil != null && cil.getInput() != null) {
                                cil.getInput().setText(nei.XMQZ);
                            }
                        }

                    }
                } else if (view instanceof ClassicLinesEditView) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        ClassicLinesEditView clev = (ClassicLinesEditView) view;
                        clev.setContentText(nei.XMQZ);
                    }
                } else if (view instanceof CheckBoxGroupLayout) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        CheckBoxGroupLayout cbgl = (CheckBoxGroupLayout) view;
                        for (int j = 0; j < cbgl.getChildCount(); j++) {
                            CheckBox checkBox = (CheckBox) cbgl.getChildAt(j);
                            //只有一个checkBox
                            checkBox.setChecked(true);
                          /*  if (childLayout!=null){
                                childLayout.setVisibility(View.VISIBLE);
                            }*/
                            break;
                        }
                    }
                } else if (view instanceof EditText) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        EditText et = (EditText) view;
                        et.setText(nei.XMQZ);
                    }
                } else if (view instanceof RadioGroup) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        RadioGroup rg = (RadioGroup) view;
                        for (int j = 0; j < rg.getChildCount(); j++) {
                            RadioButton radioButton = (RadioButton) rg.getChildAt(j);
                            //只有一个radioButton
                            //String key = (String) radioButton.getTag(R.id.id_holder_view_radioButton_key);
                            rg.check(radioButton.getId());
                            LinearLayout childLayout = (LinearLayout) rg.getTag(R.id.id_holder_view_childLayout);
                            if (childLayout != null) {
                                showViewGroupAndSetTagAllChildView(childLayout);
                            }
                            break;
                        }
                    }
                } else if (view instanceof CheckBox) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;

                        CheckBox cb = (CheckBox) view;
                        cb.setChecked(true);
                    }
                } else if (view instanceof DateSelectView) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        DateSelectView dsv = (DateSelectView) view;
                        dsv.setupDateText(nei.XMQZ);
                    }
                } else if (view instanceof TextView) {
                    if (view.getTag(R.id.id_holder_view_data_nei) != null) {
                        NursingEvaluateItem nei = (NursingEvaluateItem) view.getTag(R.id.id_holder_view_data_nei);
                        String XMNR = detail.XMNR;
                        nei.XMQZ = XMNR;
                        TextView tv = (TextView) view;
                        tv.setText(nei.XMQZ);
                    }
                }

                ////
            }

        }
    }
}
