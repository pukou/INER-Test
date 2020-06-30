package com.bsoft.mob.ienr.dynamicui.evaluate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.HealthGuidActivity;
import com.bsoft.mob.ienr.activity.user.LifeSymptomActivity;
import com.bsoft.mob.ienr.activity.user.NurseEvaluateActivity;
import com.bsoft.mob.ienr.activity.user.RiskEvaluateActivity;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PlugIn;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PouponEditViewForMultiselect;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PouponItem;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.evaluate.CheckCls;
import com.bsoft.mob.ienr.model.evaluate.CheckForm;
import com.bsoft.mob.ienr.model.evaluate.CheckItem;
import com.bsoft.mob.ienr.model.evaluate.ItemChild;
import com.bsoft.mob.ienr.util.DynamicUiUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.SizeTool;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.vectortextview.ClassicVectorTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluateViewFactory {

    /**
     * 以2开头，避免重复
     */
    public static final int REQ_RISK = 22;
    public static final int REQ_GUID = 23;
    public static final int REQ_LIFE = 25;

    /**
     * 监听点击时间按钮事件
     *
     * @author hy
     */
    public static interface DateTimePickerListener {
        public void onDateTimeClick(View view, String SJGS);
    }

    /**
     * 监听点击签名按钮事件
     *
     * @author hy
     */
    public static interface SignClickListener {
        public void onSign(View view);

        public void onAutoSign(Classification classification, boolean cancelSign);
    }

    private Context mContext;

    private NurseEvaluateActivity mFragment;

    private DateTimePickerListener listener;

    private SignClickListener signListener;

    private float totalScore;

    private Form wholeForm;

    private CheckForm mCheckForm;

    public String Txsj;

    public String Xsfllx = "";

    //上次展开的内容
    public LinearLayout last_itemNodeLayout = null;
    public TextView last_classTextView = null;

    public static class VEntity {

        public int XXID;
        /*
        升级编号【56010048】============================================= start
        PDA端输入部分值（修改时选项变成新增），PC端无法显示
        ================= Classichu 2017/10/17 8:55
        1 存放原有的1级项目的id
        */
        public int XXID_Raw;
        /* =============================================================== end */

        public String Value = "";

        public String Score;

        public String CtrlType;

        public int XMID;

        public int JFGZ;

        public int XXDJ;

        // add by louis
        public String dzlx;
        public String dzbdjl;

        //===
        @Override
        public boolean equals(Object o) {

            if (o == null) {
                return false;
            }

            if (!(o instanceof VEntity)) {
                return false;
            }
            return XXID == ((VEntity) o).XXID;
        }
    }

    public static class RelativeEntity {
        ViewGroup Root;
        int Id;
        // 控件类型 1:TextView;2:EditText;3:CheckBox;4:RadioBox;
        int Type;

        public RelativeEntity(ViewGroup root, int id, int type) {
            this.Id = id;
            this.Root = root;
            this.Type = type;
        }
    }

    //保存时用
    public SparseArray<ArrayList<VEntity>> map = new SparseArray<ArrayList<VEntity>>();
    public SparseArray<ArrayList<VEntity>> map4Sgin = new SparseArray<ArrayList<VEntity>>();
    public SparseArray<android.util.Pair<Integer, Boolean>> parentPair = new SparseArray<>();

    private List<RelativeEntity> mRisks = new ArrayList<EvaluateViewFactory.RelativeEntity>();
    private List<RelativeEntity> mGuid = new ArrayList<EvaluateViewFactory.RelativeEntity>();
    private List<RelativeEntity> mLife = new ArrayList<EvaluateViewFactory.RelativeEntity>();

    public EvaluateViewFactory(Context mContext, NurseEvaluateActivity fragment,
                               DateTimePickerListener listener, SignClickListener signListener,
                               CheckForm checkForm) {
        this.mContext = mContext;
        this.mFragment = fragment;
        this.listener = listener;
        this.signListener = signListener;
        this.mCheckForm = checkForm;
    }

    /**
     * 动态生成UI
     *
     * @return
     */
    public LinearLayout builderUi(Form form) {

        if (form == null || mContext == null) {
            return null;
        }

        final LinearLayout root = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);

        // 生成form text
        wholeForm = form;
        root.addView(addFormTitle(form.NText, form.SYZT));
        if (StringUtils.isNotEmpty(form.Score)) {
            totalScore = Float.parseFloat(form.Score);
        } else {
            totalScore = 0;
        }
        if (form.clazzs == null) {
            return null;
        }

        // 遍历 Classification
        for (Classification cf : form.clazzs) {
            // 生成 Classification 标题容器
            LinearLayout containerLayout = LayoutParamsHelper.buildLinearMatchWrap_H(mContext);
//            Pair<LinearLayout, TextView> classTextViewLayoutPair = ViewBuildHelper.buildClassTextViewLayout(mContext, cf.NText);
            LinearLayout classLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_list_group_hlpg, containerLayout, false);
            final TextView classTextView = classLayout.findViewById(R.id.id_tv);
            //没有容器 不占全屏
            LinearLayout id_rg_contar = classLayout.findViewById(R.id.id_rg_contar);
            id_rg_contar.setGravity(Gravity.RIGHT);
            classTextView.setText(cf.NText);
//            containerLayout.setTag(cf.ID);
            classTextView.setTag("classTextView_" + cf.ID);

            if (cf.XSFLLX.equals("1")) {
                Xsfllx = "1";
                RadioGroup rg = addClassChoice(root, cf, form);
                if (rg.getChildCount() > 0) {
                    id_rg_contar.addView(rg);
                    classLayout.setTag(rg);//tag RadioGroup
                }
            }
            containerLayout.addView(classLayout);
            //// TODO: 2018/2/11   以上的MATCH_PARENT都无效 待确认
//            containerLayout.addView(classLayout, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            root.addView(containerLayout);

            LinearLayout itemNodeLayout = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);
            itemNodeLayout.setVisibility(View.GONE);
            itemNodeLayout.setTag("itemNodeLayout_" + cf.ID);

            if (cf.itemNodes != null) {
                // 遍历ItemNode
                for (ItemNode node : cf.itemNodes) {
                    // 生成ItemNode 标题
                    itemNodeLayout.addView(addTypeTextView(node));
                    initItemNode(node, itemNodeLayout);
                }
                //福建协和客户化：签名之后还允许修改
//                if (!EmptyTool.isBlank(cf.HSQM1)) {
//                    disableSubControls(itemNodesView);
//                }
                //审阅过后，整个护理评估单不允许再修改
                if ("1".equals(form.SYZT)) {
                    disableSubControls(itemNodeLayout);
                } else if (!EmptyTool.isBlank(cf.HSQM1)) {
                    //没有审阅时，只允许修改自己录入的内容
                    AppApplication app = AppApplication.getInstance();
                    if (!cf.HSQM1.equals(app.user.YHID)) {
                        disableSubControls(itemNodeLayout);
                    }
                }
                root.addView(itemNodeLayout);
            }

            //            textView.setTag(cf.ID);
            classLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //标题下的内容布局
                    if (itemNodeLayout.getVisibility() == View.GONE) {
                        if (Xsfllx.equals("1")) {
                            //左边radio没有点击或者选中为无时不能展开
                            RadioGroup radioGroup = (RadioGroup) classLayout.getTag();
                            int i = radioGroup.getCheckedRadioButtonId();
                            if (i <= 0) {
                                Toast.makeText(mContext, "当选中“有”或“其他”时才能展开", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (last_itemNodeLayout != null) {
                            last_itemNodeLayout.setVisibility(View.GONE);
                        }
                        if (last_classTextView != null) {
                            last_classTextView.setSelected(false);
                        }
                        itemNodeLayout.setVisibility(View.VISIBLE);
                        classTextView.setSelected(true);
                        //记录
                        last_itemNodeLayout = itemNodeLayout;
                        last_classTextView = classTextView;
                        //定位到指定组件
                        scrollToView(v);
                    } else {
                        itemNodeLayout.setVisibility(View.GONE);
                        classTextView.setSelected(false);
                    }
                }
            });

            // 独立签名
            if (!"1".equals(cf.DLBZ)) {
                form.globalSign = true;
            } else {
                View bottomView = initBottomView(cf);
                if (bottomView != null) {
                    root.addView(bottomView);
                }
            }

        }
        root.setTag(form);
        return root;
    }

    /**
     * 遍历布局，并禁用所有子控件
     *
     * @param viewGroup 布局对象
     */
    public static void disableSubControls(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                if (v instanceof android.widget.Spinner) {
                    android.widget.Spinner spinner = (android.widget.Spinner) v;
                    spinner.setClickable(false);
                    spinner.setEnabled(false);
                } else if (v instanceof ListView) {
                    ((ListView) v).setClickable(false);
                    ((ListView) v).setEnabled(false);
                } else {
                    disableSubControls((ViewGroup) v);
                }
            } else if (v instanceof EditText) {
                ((EditText) v).setEnabled(false);
                ((EditText) v).setClickable(false);
            } else if (v instanceof Button) {
                ((Button) v).setEnabled(false);
            } else if (v instanceof android.widget.CheckBox) {
                ((android.widget.CheckBox) v).setClickable(false);
            } else if (v instanceof android.widget.RadioButton) {
                ((android.widget.RadioButton) v).setClickable(false);
            }
        }
    }

    //展现每个大类别后面的三个选项
    private RadioGroup addClassChoice(final LinearLayout root, final Classification cf, final Form form) {
        RadioGroup rg = new RadioGroup(mContext);
        rg.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rg.setLayoutParams(lllp);
//        rg.setPadding(0, 10, 0, 10);
        String[] sss = {"无", "有", "其他"};
        for (int i = 0; i < sss.length; i++) {
            RadioButton rButton = new AppCompatRadioButton(mContext);
            rButton.setText(sss[i]);
            rButton.setId(i);
            rButton.setChecked(cf.FLLX.equals(String.valueOf(i + 1)));  //设置是否选中
//            rButton.setTag(R.id.id_holder_view_eva_rb,);
            rButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
//                        String cf_ID = (String) buttonView.getTag();
                        int id = buttonView.getId();
                        LinearLayout itemNodeLayout = root.findViewWithTag("itemNodeLayout_" + cf.ID);
                        TextView classTextView = root.findViewWithTag("classTextView_" + cf.ID);
                        //选择分类
                        cf.FLLX = String.valueOf(id + 1);
                        cf.modFlag = true;
                        form.modFlag = true;
                        if (id == 1 || id == 2) {
                            //有、其他
                            if (last_itemNodeLayout != null) {
                                last_itemNodeLayout.setVisibility(View.GONE);
                            }
                            if (last_classTextView != null) {
                                last_classTextView.setSelected(false);
                            }
                            itemNodeLayout.setVisibility(View.VISIBLE);
                            classTextView.setSelected(true);
                            //记录
                            last_itemNodeLayout = itemNodeLayout;
                            last_classTextView = classTextView;
                            //定位到指定组件
                            scrollToView(buttonView);
                        } else if (id == 0) {
                            //
                            itemNodeLayout.setVisibility(View.GONE);
                            classTextView.setSelected(false);
                        }
                    }
                }
            });
            rg.addView(rButton);
        }
        return rg;
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
                    if (curView == null) {
                        break;
                    }
                    if (curView instanceof NestedScrollView) {
                        break;
                    }
                    vToScY += curView.getTop();
                }

                if (curView != null) {
                    ((NestedScrollView) curView).scrollTo(0, vToScY);
                }
            }
        }, 10);
    }

    public void resetRisk(List<ValueEntity> values) {
        for (RelativeEntity entity : mRisks) {
            for (ValueEntity value : values) {
                int id = value.ID;
                id = id == 0 ? value.GroupId : id;
                if (id == entity.Id) {
                    Integer czid = value.ID == 0 ? Integer.parseInt("100" + entity.Id) : entity.Id;
                    switch (entity.Type) {
                        case 1:
                            ((TextView) entity.Root.findViewById(entity.Id))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            break;
                        case 2:
                            if (entity.Root.findViewById(czid.intValue()) != null) {
                                entity.Root.findViewById(czid.intValue()).setTag(R.id.id_hold_view_dzbdjl, value.dzbdjl_my);
                            }
                            if (entity.Root.findViewById(czid.intValue()) instanceof EditText) {
                                ((EditText) entity.Root.findViewById(czid.intValue()))
                                        .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                                : value.VALUE);
                            } else {
                                Log.e(Constant.TAG_COMM, "resetRisk: entity.Id:" + entity.Id);
                            }
                            break;
                        case 3:
                            ((android.widget.CheckBox) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            break;
                        case 4:
                            ((android.widget.RadioButton) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            break;
                    }
                    continue;
                }
            }
        }
    }

    public void resetGuid(List<ValueEntity> values) {
        for (RelativeEntity entity : mGuid) {
            for (ValueEntity value : values) {
                int id = value.ID;
                id = id == 0 ? value.GroupId : id;
                if (id == entity.Id) {
                    Integer czid = value.ID == 0 ? Integer.parseInt("100" + entity.Id) : entity.Id;
                    switch (entity.Type) {
                        case 1:
                            ((TextView) entity.Root.findViewById(entity.Id))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            break;
                        case 2:
                            ((EditText) entity.Root.findViewById(czid.intValue()))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            break;
                        case 3:
                            ((android.widget.CheckBox) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            break;
                        case 4:
                            ((android.widget.RadioButton) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            break;
                    }
                    continue;
                }
            }
        }
    }

    public void resetLife(List<ValueEntity> values) {
        for (RelativeEntity entity : mLife) {
            for (ValueEntity value : values) {
                int id = value.ID;
                id = id == 0 ? value.GroupId : id;
                if (id == entity.Id) {
                    Integer czid = value.ID == 0 ? Integer.parseInt("100" + entity.Id) : entity.Id;
                    switch (entity.Type) {
                        case 1:
                            ((TextView) entity.Root.findViewById(entity.Id))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            break;
                        case 2:
                            if (entity.Root.findViewById(czid.intValue()) != null) {
                                entity.Root.findViewById(czid.intValue()).setTag(R.id.id_hold_view_dzbdjl, value.dzbdjl_my);
                            }
                            ((EditText) entity.Root.findViewById(czid.intValue()))
                                    .setText(EmptyTool.isBlank(value.VALUE) ? ""
                                            : value.VALUE);
                            break;
                        case 3:
                            ((android.widget.CheckBox) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            break;
                        case 4:
                            ((android.widget.RadioButton) entity.Root
                                    .findViewById(entity.Id)).setChecked(!TextUtils
                                    .isEmpty(value.VALUE));
                            break;
                    }
                    continue;
                }
            }
        }
    }

    private SparseArray<View> signViewSparseArray = new SparseArray<>();

    /**
     * @param cf
     * @return
     */
    private View initBottomView(Classification cf) {

        if (cf == null) {
            return null;
        }
        View bottomView = LayoutInflater.from(mContext).inflate(R.layout.layout_sign_save,
                null);

        TextView signView = (ClassicVectorTextView) bottomView
                .findViewById(R.id.evalute_sign_ibtn);
        signView.setTag(cf);
        signView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (signListener != null) {
                    signListener.onSign(v);
                }
            }
        });

        if (!EmptyTool.isBlank(cf.HSQM1) || !EmptyTool.isBlank(cf.HSQM2)) {
            Drawable cancelDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.btn_cancel_sign);
            signView.setCompoundDrawablesWithIntrinsicBounds(cancelDrawable, null, null, null);
        }

        if (!"1".equals(cf.DLBZ)) {
            signView.setVisibility(View.GONE);
        }
        signViewSparseArray.put(cf.ID, signView);
        return bottomView;

    }

    public void updateSginView(int cf_id, boolean cancelSgin) {
        if (signViewSparseArray != null) {
            TextView textView = (TextView) signViewSparseArray.get(cf_id);
            if (textView != null) {
                Drawable cancelDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.btn_cancel_sign);
                Drawable siginDraw = ContextCompatHelper.getDrawable(mContext, R.drawable.ic_mode_edit_black_24dp);
                //要取消签名了 就要显示签名图标了
                textView.setCompoundDrawablesWithIntrinsicBounds(!cancelSgin ? cancelDrawable : siginDraw, null, null, null);
            }
        }
    }

    private void initItemNode(ItemNode node, LinearLayout root) {

        if (node == null || root == null) {
            return;
        }
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        llp.setMargins(SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary(),
                SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary());
        root.setLayoutParams(llp);

        if (node.childViewModelLists != null && node.childViewModelLists.size() > 0) {
            LinearLayout checkBoxGroup = new LinearLayout(mContext);
            checkBoxGroup.setOrientation(LinearLayout.VERTICAL);
            checkBoxGroup.setLayoutParams(new ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            checkBoxGroup.setBackgroundResource(R.drawable.shape_classic_bg_view);
            //
            RadioGroup radioGroup = new RadioGroup(mContext);
            radioGroup.setOrientation(LinearLayout.VERTICAL);
            radioGroup.setLayoutParams(new ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            radioGroup.setBackgroundResource(R.drawable.shape_classic_bg_view);

            for (ChildViewModel childViewModel : node.childViewModelLists) {
                switch (childViewModel.getChildViewType()) {
                    case ChildViewModel.CheckBox:
                        try {
                            //内部的List 解析不出来
                            CheckBox ccbb = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), CheckBox.class);
                            //找寻元数据的list赋值
                            for (CheckBox checkBoxRaw :
                                    node.cbs) {
                                if (checkBoxRaw.ID == ccbb.ID) {
                                    ccbb.labels = checkBoxRaw.labels;
                                    ccbb.cbs = checkBoxRaw.cbs;
                                    ccbb.rbs = checkBoxRaw.rbs;
                                    ccbb.numbers = checkBoxRaw.numbers;
                                    ccbb.inputs = checkBoxRaw.inputs;
                                    ccbb.datetimes = checkBoxRaw.datetimes;
                                    ccbb.childViewModelLists = checkBoxRaw.childViewModelLists;
                                    break;
                                }
                            }
                            initCheckBox(ccbb, checkBoxGroup, true, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.RadioBox:
                        try {
                            //内部的List 解析不出来
                            RadioBox rrbb = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), RadioBox.class);
                            //找寻元数据的list赋值
                            for (RadioBox radioBoxRaw :
                                    node.rbs) {
                                if (radioBoxRaw.ID == rrbb.ID) {
                                    rrbb.labels = radioBoxRaw.labels;
                                    rrbb.cbs = radioBoxRaw.cbs;
                                    rrbb.rbs = radioBoxRaw.rbs;
                                    rrbb.numbers = radioBoxRaw.numbers;
                                    rrbb.inputs = radioBoxRaw.inputs;
                                    rrbb.datetimes = radioBoxRaw.datetimes;
                                    rrbb.childViewModelLists = radioBoxRaw.childViewModelLists;
                                    break;
                                }
                            }
                            initRadioBox(rrbb, radioGroup, true, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Input:
                        break;
                    case ChildViewModel.DateTime:
                        break;
                    case ChildViewModel.Numeric:
                        break;
                    case ChildViewModel.Spinner:
                        break;
                    default:
                }

            }
            if (checkBoxGroup.getChildCount() > 0) {
                root.addView(checkBoxGroup);
            }

            if (radioGroup.getChildCount() > 0) {
                root.addView(radioGroup);
            }

            for (ChildViewModel childViewModel : node.childViewModelLists) {
                switch (childViewModel.getChildViewType()) {
                    case ChildViewModel.CheckBox:
                        break;
                    case ChildViewModel.RadioBox:
                        break;
                    case ChildViewModel.Input:
                        try {
                            Input iinn = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), Input.class);
                            initInput(iinn, node.labels, root, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.DateTime:
                        try {
                            DateTime ddtt = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), DateTime.class);
                            initDateTime(ddtt, root, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Numeric:
                        try {
                            Numeric nnuu = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), Numeric.class);
                            initNumberic(nnuu, node.labels, root, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Spinner:
                        try {
                            //内部的List 解析不出来
                            SpinnerDataInfo sspp = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), SpinnerDataInfo.class);
                            //找寻元数据的list赋值
                            for (SpinnerDataInfo spinnerDataInfoRaw :
                                    node.spinners) {
                                if (spinnerDataInfoRaw.ID == sspp.ID) {
                                    sspp.datas = spinnerDataInfoRaw.datas;
                                    break;
                                }
                            }
                            initSpinner(sspp, root, node.ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.SpecSpinner:
                        try {
                            //内部的List 解析不出来
                            SpecSpinnerDataInfo sspp = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), SpecSpinnerDataInfo.class);
                            for (SpecSpinnerDataInfo spinnerDataInfoRaw :
                                    node.specSpinners) {
                                if (spinnerDataInfoRaw.ID == sspp.ID) {
                                    sspp.datas = spinnerDataInfoRaw.datas;
                                    break;
                                }
                            }

                            if (sspp != null) {
                                PlugIn plugIn = new PlugIn();
                                plugIn.FZYT = "1";
                                plugIn.SJLX = 1;
                                plugIn.DXFG = "";
                                plugIn.XSMC = sspp.Text;
                                plugIn.KJNR = sspp.Value;
                                plugIn.KJH = sspp.ID;

                                plugIn.DropdownItem = new ArrayList<>();
                                if (sspp.datas != null && sspp.datas.size() > 0) {
                                    for (DropData item : sspp.datas) {
                                        PouponItem pouponItem = new PouponItem();
                                        //todo 如何处理（初始化）选择情况 考虑截取字符串截取
                                        pouponItem.ISCHECK = false;
                                        pouponItem.QZWB = item.QZWB;
                                        pouponItem.VALUE = item.XMID;
                                        pouponItem.XZH = item.XMID;
                                        pouponItem.XZNR = item.XMMC;
                                        plugIn.DropdownItem.add(pouponItem);
                                    }
                                }
                                View specView = initComboxForMultiselect(plugIn, true, node.ID);
                                root.addView(specView);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                }

            }
        }
    }

    /**
     * 支持手动输入和导入
     *
     * @param plugIn
     * @return
     */
    private View initComboxForMultiselect(final PlugIn plugIn, Boolean onlyShowQzwb, int node_ID) {

        if (null == plugIn.DropdownItem) {
            return null;
        }

        final PouponEditViewForMultiselect pullEditViewForMultiselect = new PouponEditViewForMultiselect(mContext, plugIn.DXFG, onlyShowQzwb);
        pullEditViewForMultiselect.setDataList(plugIn.DropdownItem);
        pullEditViewForMultiselect.setTitle(plugIn.XSMC);
        //福建协和客户化：处理文本内容
        String s = plugIn.KJNR;
        if (!TextUtils.isEmpty(s)) {
            //初始化 todo 2018-6-7 17:22:44
          /*  VEntity entity = new VEntity();
            entity.XXID = plugIn.KJH;
            entity.XMID =node_ID ;
            addMapDataOnInit(node_ID,entity);*/
        }
        pullEditViewForMultiselect.setEditText(s);

        // 用于回收输入数据
        pullEditViewForMultiselect.edit.setTag(R.id.tag_KJ, plugIn);

        pullEditViewForMultiselect.setOnSelectListener(new PouponEditViewForMultiselect.OnSelectListener() {
            @Override
            public void doSelect(ArrayList<String> listPar) {
                //执行回调
                VEntity entity = new VEntity();
                entity.CtrlType = "SpecSpinnerDataInfo";
                entity.XMID = plugIn.KJH;
                entity.Value = pullEditViewForMultiselect.getEditTextText();
                entity.JFGZ = 0;
                entity.XXDJ = 0;
                entity.Score = "0";
                ItemCheck(entity, false);

                //xx
                dealAutoSign4Add(entity);
                //XX
                if (map.indexOfKey(entity.XMID) < 0) {
                    ArrayList<VEntity> list = new ArrayList<VEntity>();
                    list.add(entity);
                    map.put(entity.XMID, list);
                    return;
                }

                if (map.indexOfKey(entity.XMID) >= 0) {

                    ArrayList<VEntity> list = map.get(entity.XMID);
                    if (list != null) {
                        int index = list.indexOf(entity);
                        if (index != -1) {
                            list.set(index, entity);
                        } else {
                            list.add(entity);
                        }
                    }
                }
            }
        });

        if (plugIn.SJLX == 2) {// 数字
            pullEditViewForMultiselect.edit.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (plugIn.SJLX == 3) {// 时间和日期
            pullEditViewForMultiselect.edit.setInputType(InputType.TYPE_CLASS_DATETIME
                    | InputType.TYPE_DATETIME_VARIATION_NORMAL);
        }
  /*
        升级编号【56010022】============================================= start
        护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
        ================= Classichu 2017/10/18 10:41
        */
        LinearLayout linearLayout = null;
        if ("1".equals(plugIn.FZYT)//等于1不显示
                ) {/*no op*/} else {
            linearLayout = new LinearLayout(mContext);
            LinearLayout.LayoutParams pullEditView_vlp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            pullEditView_vlp.weight = 1;
            pullEditViewForMultiselect.setLayoutParams(pullEditView_vlp);
            linearLayout.addView(pullEditViewForMultiselect);

        }
        return linearLayout == null ? pullEditViewForMultiselect : linearLayout;
        /* =============================================================== end */
    }


      /*  // 生成多选项
        if (node.cbs != null) {
            LinearLayout content = new LinearLayout(mContext);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (CheckBox cb : node.cbs) {
                initCheckBox(cb, content, true, node);
            }
            if (content.getChildCount() > 0) {
                root.addView(content);
            }
        }

        // 生成单选项
        if (node.rbs != null) {

            RadioGroup rg = new RadioGroup(mContext);
            rg.setOrientation(LinearLayout.VERTICAL);
            rg.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (RadioBox rb : node.rbs) {
                initRadioBox(rb, rg, true, node);
            }
            if (rg.getChildCount() > 0) {
                root.addView(rg);
            }
        }

        // 生成输入框
        if (node.inputs != null) {
            for (Input in : node.inputs) {
                initInput(in, node.labels, root, node);
            }
        }

        // 生成 时间选择框
        if (node.datetimes != null) {
            for (DateTime date : node.datetimes) {
                initDateTime(date, root, node);
            }
        }

        // 生成 数字输入框
        if (node.numbers != null) {
            for (Numeric number : node.numbers) {
                initNumberic(number, node.labels, root, node);
            }
        }

        // 生成下拉框
        if (node.spinners != null) {
            for (SpinnerDataInfo spinner : node.spinners) {
                initSpinner(spinner, root, node.ID);
            }
        }*/
//获取 xmid 在 list 中的位置
    private int getPosInList(List<DropData> list, String xmid) {
        if (TextUtils.isEmpty(xmid)) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            DropData data = list.get(i);
            if (xmid.equals(data.XMID)) {
                return i;
            }
        }

        return -1;
    }

    private void initSpinner(final SpinnerDataInfo spinnerDataInfo, LinearLayout root,
                             final int nodeId) {
        SpinnerLayout spinnerLayout = new SpinnerLayout(mContext);
        spinnerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        android.widget.Spinner sp = spinnerLayout.getSpinner();
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(spinnerDataInfo.datas);
        sp.setAdapter(spinnerAdapter);

        sp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
               /*  //change by louis todo ：优化 2017-5-22 17:32:53
               if (arg2 == 0)
                    return;*/
                VEntity entity = new VEntity();
                entity.CtrlType = spinnerDataInfo.CtrlType;
                entity.Score = spinnerDataInfo.Score;
                entity.XMID = nodeId;
                entity.Value = spinnerDataInfo.datas.get(arg2).XMID;

                if (StringUtils.isNotEmpty(spinnerDataInfo.Jfgz)) {
                    entity.JFGZ = Integer.parseInt(spinnerDataInfo.Jfgz);
                } else {
                    entity.JFGZ = 0;
                }
                if (StringUtils.isNotEmpty(spinnerDataInfo.Xxdj)) {
                    entity.XXDJ = Integer.parseInt(spinnerDataInfo.Xxdj);
                } else {
                    entity.XXDJ = 0;
                }
                entity.Score = computeItemScore(entity.JFGZ, entity.XXDJ, "0",
                        "0", "btn", false);
                ItemCheck(entity, false);
                //xx
                dealAutoSign4Add(entity);
                //XX
                if (map.indexOfKey(nodeId) < 0) {
                    ArrayList<VEntity> list = new ArrayList<VEntity>();
                    list.add(entity);
                    map.put(nodeId, list);
                    return;
                }

                if (map.indexOfKey(nodeId) >= 0) {

                    ArrayList<VEntity> list = map.get(nodeId);
                    if (list != null) {
                        int index = list.indexOf(entity);
                        if (index != -1) {
                            list.set(index, entity);
                        } else {
                            list.add(entity);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //change af setOnItemSelectedListener
        if (!TextUtils.isEmpty(spinnerDataInfo.Value)) {
            //初始化
            VEntity entity = new VEntity();
            entity.XXID = spinnerDataInfo.ID;
            entity.XMID = nodeId;
            addMapDataOnInit(nodeId, entity);
        }
        if (!EmptyTool.isBlank(spinnerDataInfo.Value)) {
            //if (TextUtils.isDigitsOnly(spinnerDataInfo.Value)) {
            if (TextUtils.isDigitsOnly(spinnerDataInfo.Value) && Integer.parseInt(spinnerDataInfo.Value) < spinnerDataInfo.datas.size()) {
                //sp.setSelection(Integer.parseInt(spinnerDataInfo.Value));
                int pos = getPosInList(spinnerDataInfo.datas, spinnerDataInfo.Value);
                if (pos > -1) {
                    sp.setSelection(pos);
                }
            } else {
                //根据值, 设置spinner默认选中:
                int k = spinnerAdapter.getCount();
                for (int i = 0; i < k; i++) {
                    if (spinnerDataInfo.Value.equals(spinnerAdapter.getItem(i).XMID.toString())) {
                        sp.setSelection(i, true);// 默认选中项
                        break;
                    }
                }
            }
        } else {
            //add by louis todo ：优化 2017-5-22 17:32:53
            if (spinnerDataInfo.datas != null && spinnerDataInfo.datas.size() > 0) {
                //设置默认选中
                sp.setSelection(0, true);
            }
        }
        //
        root.addView(spinnerLayout);
    }

    /**
     * 生成 数字输入框
     *
     * @param number
     * @param root
     * @param node
     */
    private void initNumberic(final Numeric number, List<Label> labels,
                              LinearLayout root, final ItemNode node) {

        if (number == null || root == null) {
            return;
        }

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.HORIZONTAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        String beforeId = number.FrontId;

        // 加入前置文本
        if (!EmptyTool.isBlank(beforeId)) {

            if (labels != null) {
                for (Label label : labels) {
                    if (beforeId.equals(String.valueOf(label.ID))) {
                        initText(label, child);
                        break;
                    }
                }
            }

            //
            View view=child.getChildAt(0);
            if (!EmptyTool.isBlank(number.Dzlx) && !number.Dzlx.equals("-1") && (view != null && view instanceof TextView)) {

                TextView txtView = (TextView) view;
                if (number.Dzlx.equals("2")) {
                    // 风险
                    txtView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                } else if (number.Dzlx.equals("3")) {
                    // 宣教
                    txtView.setTextColor(ContextCompat.getColor(mContext,  R.color.blue));

                } else if (number.Dzlx.equals("5")) {
                    // 体征
                    txtView.setTextColor(ContextCompat.getColor(mContext, R.color.pink));
                }
                //
                txtView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if (number.Dzlx.equals("2")) {
                            // 风险
                            intent = new Intent(mContext, RiskEvaluateActivity.class);
                  /* 2017年4月27日08:46:43  remove
                    intent.putExtra("PGDH", node.Dzbd);
                    intent.putExtra("PGLX", node.Dzbdlx);*/
                            intent.putExtra("PGDH", "");
                            //intent.putExtra("PGLX", in.Dzbd);//(风险评估：一种类型对应多个表单): DZBD对应IENR_PGJL的PGLX 值就是PGZF
                            intent.putExtra("PGLX", number.Dzbdlx);
                            intent.putExtra("FROMOUT", true);
                            intent.putExtra("TXSJ", Txsj);
                            mFragment.startActivityForResult(intent, REQ_RISK);
                        } else if (number.Dzlx.equals("3")) {
                            intent = new Intent(mContext, HealthGuidActivity.class);
                            intent.putExtra("lxbh", number.Dzbd);
                            intent.putExtra("type", number.Dzbdlx);
                            intent.putExtra("operType", "9");
                            intent.putExtra("TXSJ", Txsj);
                            mFragment.startActivityForResult(intent, REQ_GUID);
                        } else if (number.Dzlx.equals("5")) {
                            // 体征
                            intent = new Intent(mContext, LifeSymptomActivity.class);
                            intent.putExtra("TXSJ", Txsj);
                            mFragment.startActivityForResult(intent, REQ_LIFE);
                        }
                    }
                });
            }
        }

        final String value = number.Value;
        if (!TextUtils.isEmpty(value)) {
            //初始化
            VEntity entity = new VEntity();
            entity.XXID = number.ID;
            entity.XMID = node.ID;
            addMapDataOnInit(node.ID, entity);
        }
        final EditText edit = addNEditText(value, number.UpLimit,
                number.DownLimit, null, null);
        int bxID = Integer.parseInt("100" + node.ID);
        edit.setId(number.ID == 0 ? bxID : number.ID);
        //if (!EmptyTool.isBlank(node.Dzlx) && !node.Dzlx.equals("-1")) {
//            if (!EmptyTool.isBlank(number.Dzlx)) {
        if (!EmptyTool.isBlank(node.Dzlx) && !node.Dzlx.equals("-1") || !EmptyTool.isBlank(number.Dzlx) && !"-1".equals(number.Dzlx)) {
            //if (node.Dzlx.equals("2")) {
            if ("2".equals(node.Dzlx) || "2".equals(number.Dzlx)) {
                // 风险
                edit.setTextColor(mContext.getResources().getColor(R.color.green));
                edit.setEnabled(false);
                mRisks.add(new RelativeEntity(root, number.ID == 0 ? node.ID : number.ID, 2));
            //} else if (node.Dzlx.equals("3")) {
            } else if ("3".equals(node.Dzlx) || "3".equals(number.Dzlx)) {
                // 宣教
                edit.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                edit.setEnabled(false);
                mGuid.add(new RelativeEntity(root, number.ID == 0 ? node.ID : number.ID, 2));
            //} else if (node.Dzlx.equals("5")) {
            } else if ("5".equals(node.Dzlx) || "5".equals(number.Dzlx)) {
                // 体征
                edit.setTextColor(mContext.getResources().getColor(R.color.pink));
                edit.setEnabled(false); //关联体征的项目不允许手工录入
                mLife.add(new RelativeEntity(root, number.ID == 0 ? node.ID : number.ID, 2));
            }
        }
        if (edit != null) {
            edit.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edit.setTag(number);

            edit.addTextChangedListener(new TextWatcher() {
                private String oldValue = "0";

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    if (StringUtils.isNotEmpty(edit.getText().toString())) {
                        oldValue = edit.getText().toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals(value)) {
//                    if (!EmptyTool.isBlank(s) && !s.toString().equals(value)) {

                        VEntity entity = new VEntity();
                        entity.CtrlType = number.CtrlType;
                        entity.Value = s.toString();
                        //change by louis 2017-7-5 00:21:16 单文档 护理评估表单修改XXNR不起作用
                        // entity.XXID = number.ID;
                        entity.XXID = edit.getId();
                           /*
        升级编号【56010048】============================================= start
        PDA端输入部分值（修改时选项变成新增），PC端无法显示
        ================= Classichu 2017/10/17 8:55
        2 存放原有的1级项目的id
        */
                        entity.XXID_Raw = number.ID;
                        /* =============================================================== end */
                        entity.XMID = node.ID;

                        //add by louis 2017年6月6日09:50:15
                        entity.dzlx = node.Dzlx; //对照类型不需要变
                        String dzbdjl_my = number.dzbdjl_my;
                        //更新操作不会覆盖number数据源，在reset View的时候 赋给 R.id.id_hold_view_dzbdjl
                        if (edit != null && edit.getTag(R.id.id_hold_view_dzbdjl) != null) {
                            dzbdjl_my = (String) edit.getTag(R.id.id_hold_view_dzbdjl);
                        }
                        entity.dzbdjl = dzbdjl_my;
                        //===
                        if (StringUtils.isNotEmpty(number.Jfgz)) {
                            entity.JFGZ = Integer.parseInt(number.Jfgz);
                        } else {
                            entity.JFGZ = 0;
                        }
                        if (StringUtils.isNotEmpty(number.Xxdj)) {
                            entity.XXDJ = Integer.parseInt(number.Xxdj);
                        } else {
                            entity.XXDJ = 0;
                        }

                        entity.Score = computeItemScore(entity.JFGZ,
                                entity.XXDJ, edit.getText().toString(),
                                oldValue, "txt", false);
                        ItemCheck(entity, false);
                        //xx
                        if (EmptyTool.isBlank(s)) {
                            dealAutoSign4Remove(node.ID);
                        } else {
                            dealAutoSign4Add(entity);
                        }
                        //XX
                        if (map.indexOfKey(node.ID) < 0) {
                            ArrayList<VEntity> list = new ArrayList<VEntity>();
                            list.add(entity);
                            map.put(node.ID, list);
                            return;
                        }

                        if (map.indexOfKey(node.ID) >= 0) {

                            ArrayList<VEntity> list = map.get(node.ID);
                            if (list != null) {
                                int index = list.indexOf(entity);
                                if (index != -1) {
                                    list.set(index, entity);
                                } else {
                                    list.add(entity);
                                }
                            }

                        }
                    }
                }
            });

            child.addView(edit);
        }

        String afterId = number.PostpositionId;
        // 加入后置文本
        if (!EmptyTool.isBlank(afterId)) {

            if (labels != null) {
                for (Label label : labels) {
                    if (afterId.equals(String.valueOf(label.ID))) {
                        initText(label, child);
                        break;
                    }
                }
            }
        }

        root.addView(child);
    }

    /**
     * 增加数字输入框
     *
     * @param value
     * @param upLimit
     * @param downLimit
     * @return
     */
    private EditText addNEditText(String value, String upLimit,
                                  String downLimit, String min, String max) {

        final EditText edit = ViewBuildHelper.buildEditTextAutoWrap(mContext, value);
        //
        edit.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        DynamicUiUtil.setMaxMinValue(downLimit, upLimit, edit, min, max);
        return edit;
    }

    /**
     * 生成 时间选择框
     *
     * @param date
     * @param root
     * @param node
     */
    private void initDateTime(final DateTime date, LinearLayout root,
                              final ItemNode node) {

        if (date == null || root == null) {
            return;
        }
        final String value = date.Value;

        final TextView txt = ViewBuildHelper.buildTimeTextView(mContext, value);
        // final String formatedStr = DateUtil.get8To7Sstr(value);
        if (!EmptyTool.isBlank(value)) {
            txt.setText(value);
            //初始化
            VEntity entity = new VEntity();
            entity.XXID = date.ID;
            entity.XMID = node.ID;
            addMapDataOnInit(node.ID, entity);
        }
        // txt.setId(date.ID);
        txt.setId(node.ID);
        if (!EmptyTool.isBlank(node.Dzlx) && !node.Dzlx.equals("-1")) {
            if (node.Dzlx.equals("2")) {
                // 风险
                txt.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                mRisks.add(new RelativeEntity(root, node.ID, 1));
            } else if (node.Dzlx.equals("3")) {
                // 宣教
                txt.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                mGuid.add(new RelativeEntity(root, node.ID, 1));
            } else if (node.Dzlx.equals("5")) {
                // 体征
                txt.setTextColor(mContext.getResources().getColor(R.color.pink));
                mLife.add(new RelativeEntity(root, node.ID, 1));
            }
        }
        // 设置点击监听事件
        txt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDateTimeClick(v, date.SJGS);
                }
            }
        });

        txt.setTag(date);

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

                if (!s.toString().equals(value)) {
//                if (!EmptyTool.isBlank(s) && !s.toString().equals(value)) {

                    VEntity entity = new VEntity();
                    entity.CtrlType = date.CtrlType;
                    entity.Value = s.toString();
                    entity.XXID = date.ID;
                    entity.XMID = node.ID;

                    ItemCheck(entity, false);
                    //xx
                    if (EmptyTool.isBlank(s)) {
                        dealAutoSign4Remove(node.ID);
                    } else {
                        dealAutoSign4Add(entity);
                    }
                    //XX
                    if (map.indexOfKey(node.ID) < 0) {
                        ArrayList<VEntity> list = new ArrayList<VEntity>();
                        list.add(entity);
                        map.put(node.ID, list);
                        return;
                    }

                    if (map.indexOfKey(node.ID) >= 0) {

                        ArrayList<VEntity> list = map.get(node.ID);
                        if (list != null) {
                            int index = list.indexOf(entity);
                            if (index != -1) {
                                list.set(index, entity);
                            } else {
                                list.add(entity);
                            }
                        }

                    }
                }
            }
        });

        root.addView(txt);

    }

    /**
     * 生成 文字
     *
     * @param txt
     * @param root
     */
    private void initText(Label txt, LinearLayout root) {

        if (txt == null || root == null) {
            return;
        }
        TextView txtView = ViewBuildHelper.buildTextView(mContext, txt.Text);
        if (txtView == null) {
            return;
        }
        txtView.setTag(txtView);
        TextViewCompat.setTextAppearance(txtView, R.style.ClassicTextAppearancePrimary);
        root.addView(txtView);


    }

    /**
     * 生成输入框
     *
     * @param in
     * @param labels
     * @param root
     * @param node
     */
    private void initInput(final Input in, List<Label> labels,
                           LinearLayout root, final ItemNode node) {

        if (in == null || root == null) {
            return;
        }

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.HORIZONTAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        String beforeId = in.FrontId;

        // 加入前置文本
        if (!EmptyTool.isBlank(beforeId)) {

            if (labels != null) {
                for (Label label : labels) {
                    if (beforeId.equals(String.valueOf(label.ID))) {
                        initText(label, child);
                        break;
                    }
                }
            }
            //
            View view=child.getChildAt(0);
            if (!EmptyTool.isBlank(in.Dzlx) && !in.Dzlx.equals("-1") && (view != null && view instanceof TextView)) {

                TextView txtView = (TextView) view;
                if (in.Dzlx.equals("2")) {
                    // 风险
                    txtView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                } else if (in.Dzlx.equals("3")) {
                    // 宣教
                    txtView.setTextColor(ContextCompat.getColor(mContext,  R.color.blue));

                } else if (in.Dzlx.equals("5")) {
                    // 体征
                    txtView.setTextColor(ContextCompat.getColor(mContext, R.color.pink));
                }
                //
                txtView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if (in.Dzlx.equals("2")) {
                            // 风险
                            intent = new Intent(mContext, RiskEvaluateActivity.class);
                  /* 2017年4月27日08:46:43  remove
                    intent.putExtra("PGDH", node.Dzbd);
                    intent.putExtra("PGLX", node.Dzbdlx);*/
                            intent.putExtra("PGDH", "");
                            //intent.putExtra("PGLX", in.Dzbd);//(风险评估：一种类型对应多个表单): DZBD对应IENR_PGJL的PGLX 值就是PGZF
                            intent.putExtra("PGLX", in.Dzbdlx);
                            intent.putExtra("FROMOUT", true);
                            intent.putExtra("TXSJ", Txsj);
                            mFragment.startActivityForResult(intent, REQ_RISK);
                        } else if (in.Dzlx.equals("3")) {
                            intent = new Intent(mContext, HealthGuidActivity.class);
                            intent.putExtra("lxbh", in.Dzbd);
                            intent.putExtra("type", in.Dzbdlx);
                            intent.putExtra("operType", "9");
                            intent.putExtra("TXSJ", Txsj);
                            mFragment.startActivityForResult(intent, REQ_GUID);
                        } else if (in.Dzlx.equals("5")) {
                            // 体征
                            intent = new Intent(mContext, LifeSymptomActivity.class);
                            intent.putExtra("TXSJ", Txsj);
                            mFragment.startActivityForResult(intent, REQ_LIFE);
                        }
                    }
                });
            }


        }

        final String value = in.Value;
        if (!TextUtils.isEmpty(value)) {
            //初始化
            VEntity entity = new VEntity();
            entity.XXID = in.ID;
            entity.XMID = node.ID;
            addMapDataOnInit(node.ID, entity);
        }
        final EditText edit = ViewBuildHelper.buildEditTextAutoWrap(mContext, value);
        int bxID = Integer.parseInt("100" + node.ID);
        edit.setId(in.ID == 0 ? bxID : in.ID);
        if (!EmptyTool.isBlank(node.Dzlx) && !node.Dzlx.equals("-1") || !EmptyTool.isBlank(in.Dzlx) && !"-1".equals(in.Dzlx)) {
        //if (!EmptyTool.isBlank(in.Dzlx) && !in.Dzlx.equals("-1")) {
            //if (node.Dzlx.equals("2")) {
            //if (in.Dzlx.equals("2")) {
            if ("2".equals(node.Dzlx) || "2".equals(in.Dzlx)) {
                // 风险
                edit.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                edit.setEnabled(false);
                mRisks.add(new RelativeEntity(root, node.ID, 2));
             //} else if (node.Dzlx.equals("3")) {
             //} else if (in.Dzlx.equals("3")) {
            }else if ("3".equals(node.Dzlx) || "3".equals(in.Dzlx)) {
                // 宣教
                edit.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
                mGuid.add(new RelativeEntity(root, node.ID, 2));
             //} else if (node.Dzlx.equals("5")) {
             //} else if (in.Dzlx.equals("5")) {
            }else if ("5".equals(node.Dzlx) || "5".equals(in.Dzlx)) {
                // 体征
                edit.setTextColor(ContextCompat.getColor(mContext, R.color.pink));
                edit.setEnabled(false);     //有体征关联的不允许手工录入
                //mLife.add(new RelativeEntity(root, node.ID, 2));
                mLife.add(new RelativeEntity(root, in.ID, 2));
            }
            edit.setEnabled(false);
        }
        if (edit != null) {
            edit.setTag(in);
            child.addView(edit);

            edit.addTextChangedListener(new TextWatcher() {
                private String oldValue = "0";

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    if (StringUtils.isNotEmpty(edit.getText().toString())) {
                        oldValue = edit.getText().toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

//                    if (!EmptyTool.isBlank(s) && !s.toString().equals(value)) {
                    if (!s.toString().equals(value)) {

                        VEntity entity = new VEntity();
                        entity.CtrlType = in.CtrlType;
                        entity.Value = s.toString();
                        //change by louis 2017-7-5 00:21:16 单文档 护理评估表单修改XXNR不起作用
                        //entity.XXID = in.ID;
                        entity.XXID = edit.getId();
                                           /*
        升级编号【56010048】============================================= start
        PDA端输入部分值（修改时选项变成新增），PC端无法显示
        ================= Classichu 2017/10/17 8:55
        3 存放原有的1级项目的id
        */
                        entity.XXID_Raw = in.ID;
                        /* =============================================================== end */
                        entity.XMID = node.ID;

                        //add by louis 2017年6月6日09:50:15
                        entity.dzlx = node.Dzlx; //对照类型不需要变
                        String dzbdjl_my = in.dzbdjl_my;
                        // 更新操作不会覆盖in数据源，在reset View的时候 赋给 R.id.id_hold_view_dzbdjl
                        if (edit != null && edit.getTag(R.id.id_hold_view_dzbdjl) != null) {
                            dzbdjl_my = (String) edit.getTag(R.id.id_hold_view_dzbdjl);
                        }
                        entity.dzbdjl = dzbdjl_my;
                        //===
                        if (StringUtils.isNotEmpty(in.Jfgz)) {
                            entity.JFGZ = Integer.parseInt(in.Jfgz);
                        } else {
                            entity.JFGZ = 0;
                        }
                        if (StringUtils.isNotEmpty(in.Xxdj)) {
                            entity.XXDJ = Integer.parseInt(in.Xxdj);
                        } else {
                            entity.XXDJ = 0;
                        }
                        entity.Score = computeItemScore(entity.JFGZ,
                                entity.XXDJ, edit.getText().toString(),
                                oldValue, "txt", false);
                        ItemCheck(entity, false);
                        //xx
                        if (EmptyTool.isBlank(s)) {
                            dealAutoSign4Remove(node.ID);
                        } else {
                            dealAutoSign4Add(entity);
                        }
                        //XX
                        if (map.indexOfKey(node.ID) < 0) {
                            ArrayList<VEntity> list = new ArrayList<VEntity>();
                            list.add(entity);
                            map.put(node.ID, list);
                            return;
                        }

                        if (map.indexOfKey(node.ID) >= 0) {

                            ArrayList<VEntity> list = map.get(node.ID);
                            if (list != null) {
                                int index = list.indexOf(entity);
                                if (index != -1) {
                                    list.set(index, entity);
                                } else {
                                    list.add(entity);
                                }
                            }

                        }
                    }
                }
            });

        }

        String afterId = in.PostpositionId;
        // 加入后置文本
        if (!EmptyTool.isBlank(afterId)) {

            if (labels != null) {
                for (Label label : labels) {
                    if (afterId.equals(String.valueOf(label.ID))) {
                        initText(label, child);
                        break;
                    }
                }
            }
        }

        root.addView(child);
    }


    /**
     * 初始化单选项
     *
     * @param rb
     * @param root
     * @param node
     */
    private void initRadioBox(final RadioBox rb, RadioGroup root,
                              boolean isFristClass, final ItemNode node) {
        if (rb == null || root == null) {
            return;
        }

        final boolean isSelected = rb.IsSelected == 0 ? false : true;
        RadioButton rButton = new AppCompatRadioButton(mContext);
        rButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        rButton.setText(rb.Value);
        rButton.setId(rb.ID);
        rButton.setTag(rb);
        if (!EmptyTool.isBlank(rb.Dzlx)) {
            if (rb.Dzlx.equals("2")) {
                // 风险
                rButton.setTextColor(mContext.getResources().getColor(
                        R.color.green));
                mRisks.add(new RelativeEntity(root, rb.ID, 4));
            } else if (rb.Dzlx.equals("3")) {
                // 宣教
                rButton.setTextColor(ContextCompat.getColor(mContext,
                        R.color.blue));
                mGuid.add(new RelativeEntity(root, rb.ID, 4));
            } else if (rb.Dzlx.equals("5")) {
                // 体征
                rButton.setTextColor(mContext.getResources().getColor(
                        R.color.pink));
                mLife.add(new RelativeEntity(root, rb.ID, 4));
            }
            //rButton.setEnabled(false);
        }
        root.addView(rButton);

        if (isSelected) {
            int id = rButton.getId();
            root.check(id);
            //初始化选中状态
            VEntity entity = new VEntity();
            entity.XXID = rb.ID;
            entity.XMID = node.ID;
            addMapDataOnInit(node.ID, entity);
            //
            //TODO Radio,Check只要是选中状态强制将TXBZ置为1，解决修改数据时提醒未填写的bug
            boolean isBreak = false;
            for (CheckCls cls : mCheckForm.CLS) {
                if (isBreak) {
                    break;
                }
                if (cls.ITEMS == null) {
                    continue;
                }
                for (CheckItem item : cls.ITEMS) {
                    if (node.ID == item.XMID) {
                        if (rb.CtrlType.equals("Radio") ||
                                rb.CtrlType.equals("RadioBox")
                                || rb.CtrlType.equals("Check") || rb.CtrlType.equals("CheckBox")) {
                            item.TXBZ = "1";
                        }
                        isBreak = !isBreak;
                        break;
                    }
                }
            }
        }

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        child.setPadding(SizeTool.dp2px(30), 0, 0, 0);
        child.setTag(R.id.tag_KJ, rb.ID);


        if (rb.childViewModelLists != null && rb.childViewModelLists.size() > 0) {
            LinearLayout checkBoxGroup = new LinearLayout(mContext);
            checkBoxGroup.setOrientation(LinearLayout.VERTICAL);
            checkBoxGroup.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            RadioGroup radioGroup = new RadioGroup(mContext);
            radioGroup.setOrientation(LinearLayout.VERTICAL);
            radioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (ChildViewModel childViewModel : rb.childViewModelLists) {
                switch (childViewModel.getChildViewType()) {
                    case ChildViewModel.CheckBox:
                        try {
                            //内部的List 解析不出来
                            CheckBox ccbb = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), CheckBox.class);
                            //找寻元数据的list赋值
                            for (CheckBox checkBoxRaw :
                                    rb.cbs) {//!!!
                                if (checkBoxRaw.ID == ccbb.ID) {
                                    ccbb.labels = checkBoxRaw.labels;
                                    ccbb.cbs = checkBoxRaw.cbs;
                                    ccbb.rbs = checkBoxRaw.rbs;
                                    ccbb.numbers = checkBoxRaw.numbers;
                                    ccbb.inputs = checkBoxRaw.inputs;
                                    ccbb.datetimes = checkBoxRaw.datetimes;
                                    ccbb.childViewModelLists = checkBoxRaw.childViewModelLists;
                                    break;
                                }
                            }
                            initCheckBox(ccbb, checkBoxGroup, false, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.RadioBox:
                        try {
                            //内部的List 解析不出来
                            RadioBox rrbb = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), RadioBox.class);
                            //找寻元数据的list赋值
                            for (RadioBox radioBoxRaw :
                                    rb.rbs) {//!!!
                                if (radioBoxRaw.ID == rrbb.ID) {
                                    rrbb.labels = radioBoxRaw.labels;
                                    rrbb.cbs = radioBoxRaw.cbs;
                                    rrbb.rbs = radioBoxRaw.rbs;
                                    rrbb.numbers = radioBoxRaw.numbers;
                                    rrbb.inputs = radioBoxRaw.inputs;
                                    rrbb.datetimes = radioBoxRaw.datetimes;
                                    rrbb.childViewModelLists = radioBoxRaw.childViewModelLists;
                                    break;
                                }
                            }
                            initRadioBox(rrbb, radioGroup, false, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Input:
                        try {
                            Input iinn = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), Input.class);
                            initInput(iinn, rb.labels, child, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.DateTime:
                        try {
                            DateTime ddtt = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), DateTime.class);
                            initDateTime(ddtt, child, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Numeric:
                        try {
                            Numeric nnuu = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), Numeric.class);
                            initNumberic(nnuu, rb.labels, child, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Spinner:
                        try {
                            //内部的List 解析不出来
                            SpinnerDataInfo sspp = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), SpinnerDataInfo.class);
                            //找寻元数据的list赋值
                            for (SpinnerDataInfo spinnerDataInfoRaw :
                                    node.spinners) {//!!!
                                if (spinnerDataInfoRaw.ID == sspp.ID) {
                                    sspp.datas = spinnerDataInfoRaw.datas;
                                    break;
                                }
                            }
                            initSpinner(sspp, child, node.ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                }

            }
            if (checkBoxGroup.getChildCount() > 0) {
                child.addView(checkBoxGroup);//!!!
            }

            if (radioGroup.getChildCount() > 0) {
                child.addView(radioGroup);//!!!
            }
        }

       /* // 生成多选项
        if (rb.cbs != null) {

            LinearLayout content = new LinearLayout(mContext);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (CheckBox cbx : rb.cbs) {
                initCheckBox(cbx, content, false, node);
            }
            if (content.getChildCount() > 0) {
                child.addView(content);
            }
        }

        // 生成单选项
        if (rb.rbs != null) {
            RadioGroup rg = new RadioGroup(mContext);
            rg.setOrientation(LinearLayout.VERTICAL);
            rg.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (RadioBox rBox : rb.rbs) {
                initRadioBox(rBox, rg, false, node);
            }
            if (rg.getChildCount() > 0) {
                child.addView(rg);
            }
        }

        // 生成输入框
        if (rb.inputs != null) {
            for (Input in : rb.inputs) {
                initInput(in, rb.labels, child, node);
            }
        }

        // 生成 时间选择框
        if (rb.datetimes != null) {
            for (DateTime date : rb.datetimes) {
                initDateTime(date, child, node);
            }
        }

        // 生成 数字输入框
        if (rb.numbers != null) {
            for (Numeric number : rb.numbers) {
                initNumberic(number, rb.labels, child, node);
            }
        }*/

        if (EmptyTool.isBlank(rb.Dzlx) || "0".equals(rb.Dzlx)) {
            // 当是一级控件时，隐藏二级项
            if (isFristClass && !isSelected) {
                child.setVisibility(View.GONE);
            }
            // 激活二级控件
            rButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                private boolean checked = false;

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    checked = isChecked;
                    ViewGroup parent = (ViewGroup) buttonView.getParent();
                    int childCount = parent.getChildCount();
                    for (int itemPos = 0; itemPos < childCount; itemPos++) {
                        View view = parent.getChildAt(itemPos);
                        if (view instanceof LinearLayout) {

                            int tag = (Integer) view.getTag(R.id.tag_KJ);
                            if (tag == buttonView.getId()) {
                                view.setVisibility(isChecked ? View.VISIBLE
                                        : View.GONE);
                                break;
                            }
                        }
                    }

                    VEntity entity = new VEntity();
                    entity.CtrlType = rb.CtrlType;
                    // entity.Value = rb.Value;
                    entity.XXID = rb.ID;
                    entity.XMID = node.ID;

                    if (StringUtils.isNotEmpty(rb.Jfgz)) {
                        entity.JFGZ = Integer.parseInt(rb.Jfgz);
                    } else {
                        entity.JFGZ = 0;
                    }
                    if (StringUtils.isNotEmpty(rb.Xxdj)) {
                        entity.XXDJ = Integer.parseInt(rb.Xxdj);
                    } else {
                        entity.XXDJ = 0;
                    }
                    entity.Score = computeItemScore(entity.JFGZ, entity.XXDJ,
                            "0", "0", "btn", checked);
                    ItemCheck(entity, isChecked);
                    //xx
                    if (!isChecked) {
                        dealAutoSign4Remove(node.ID);
                    } else {
                        dealAutoSign4Add(entity);
                    }
                    //XX
                    if (isSelected == isChecked) {
                        if (map.indexOfKey(node.ID) >= 0) {
                            ArrayList<VEntity> list = map.get(node.ID);
                            if (list != null) {
                                list.remove(entity);
                            }
                        }
                        return;
                    }

                    if (map.indexOfKey(node.ID) < 0) {
                        ArrayList<VEntity> list = new ArrayList<VEntity>();
                        map.put(node.ID, list);
                    }
                    ArrayList<VEntity> list = map.get(node.ID);
                    int index = list.indexOf(entity);
                    if (index == -1) {
                        list.add(entity);
                    }
                }
            });
        }
        root.addView(child);

    }

    private void addMapDataOnInit(int node_id, VEntity entity) {
        if (map4Sgin.indexOfKey(node_id) < 0) {
            ArrayList<VEntity> list = new ArrayList<>();
            list.add(entity);
            map4Sgin.put(node_id, list);
            return;
        }
        if (map4Sgin.indexOfKey(node_id) >= 0) {
            ArrayList<VEntity> list = map4Sgin.get(node_id);
            if (list != null) {
                boolean has = false;
                for (VEntity vEntity : list) {
                    if (vEntity.XMID == entity.XMID && vEntity.XXID == entity.XXID) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    list.add(entity);
                }
            }
        }
    }

    /**
     * 初始化多选项
     *
     * @param cb
     * @param root
     * @param node ItemNode
     */
    private void initCheckBox(final CheckBox cb, LinearLayout root,
                              boolean isFristClass, final ItemNode node) {

        if (cb == null || root == null) {
            return;
        }

        final boolean isSelected = cb.IsSelected == 0 ? false : true;
        android.widget.CheckBox checkBox = new AppCompatCheckBox(mContext);
        checkBox.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        checkBox.setText(cb.Value);
        checkBox.setId(cb.ID);
        checkBox.setChecked(isSelected);
        if (isSelected) {
            //初始化选中状态
            VEntity entity = new VEntity();
            entity.XXID = cb.ID;
            entity.XMID = node.ID;
            addMapDataOnInit(node.ID, entity);
        }
        checkBox.setTag(cb);
        if (!EmptyTool.isBlank(cb.Dzlx)) {
            if (cb.Dzlx.equals("2")) {
                // 风险
                checkBox.setTextColor(mContext.getResources().getColor(
                        R.color.green));
                mRisks.add(new RelativeEntity(root, cb.ID, 3));
            } else if (cb.Dzlx.equals("3")) {
                // 宣教
                checkBox.setTextColor(ContextCompat.getColor(mContext,
                        R.color.blue));
                mGuid.add(new RelativeEntity(root, cb.ID, 3));
            } else if (cb.Dzlx.equals("5")) {
                // 体征
                checkBox.setTextColor(mContext.getResources().getColor(R.color.pink));
                mLife.add(new RelativeEntity(root, cb.ID, 3));
            }
            checkBox.setEnabled(false);
        }
        root.addView(checkBox);

        LinearLayout child = new LinearLayout(mContext);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        child.setPadding(SizeTool.dp2px(30), 0, 0, 0);
        child.setTag(R.id.tag_KJ, cb.ID);

        if (cb.childViewModelLists != null && cb.childViewModelLists.size() > 0) {
            LinearLayout checkBoxGroup = new LinearLayout(mContext);
            checkBoxGroup.setOrientation(LinearLayout.VERTICAL);
            checkBoxGroup.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            RadioGroup radioGroup = new RadioGroup(mContext);
            radioGroup.setOrientation(LinearLayout.VERTICAL);
            radioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (ChildViewModel childViewModel : cb.childViewModelLists) {
                switch (childViewModel.getChildViewType()) {
                    case ChildViewModel.CheckBox:
                        try {
                            //内部的List 解析不出来
                            CheckBox ccbb = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), CheckBox.class);
                            //找寻元数据的list赋值
                            for (CheckBox checkBoxRaw :
                                    cb.cbs) {//!!!
                                if (checkBoxRaw.ID == ccbb.ID) {
                                    ccbb.labels = checkBoxRaw.labels;
                                    ccbb.cbs = checkBoxRaw.cbs;
                                    ccbb.rbs = checkBoxRaw.rbs;
                                    ccbb.numbers = checkBoxRaw.numbers;
                                    ccbb.inputs = checkBoxRaw.inputs;
                                    ccbb.datetimes = checkBoxRaw.datetimes;
                                    ccbb.childViewModelLists = checkBoxRaw.childViewModelLists;
                                    break;
                                }
                            }
                            initCheckBox(ccbb, checkBoxGroup, false, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.RadioBox:
                        try {
                            //内部的List 解析不出来
                            RadioBox rrbb = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), RadioBox.class);
                            //找寻元数据的list赋值
                            for (RadioBox radioBoxRaw :
                                    cb.rbs) {//!!!
                                if (radioBoxRaw.ID == rrbb.ID) {
                                    rrbb.labels = radioBoxRaw.labels;
                                    rrbb.cbs = radioBoxRaw.cbs;
                                    rrbb.rbs = radioBoxRaw.rbs;
                                    rrbb.numbers = radioBoxRaw.numbers;
                                    rrbb.inputs = radioBoxRaw.inputs;
                                    rrbb.datetimes = radioBoxRaw.datetimes;
                                    rrbb.childViewModelLists = radioBoxRaw.childViewModelLists;
                                    break;
                                }
                            }
                            initRadioBox(rrbb, radioGroup, false, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Input:
                        try {
                            Input iinn = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), Input.class);
                            initInput(iinn, cb.labels, child, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.DateTime:
                        try {
                            DateTime ddtt = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), DateTime.class);
                            initDateTime(ddtt, child, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Numeric:
                        try {
                            Numeric nnuu = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), Numeric.class);
                            initNumberic(nnuu, cb.labels, child, node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ChildViewModel.Spinner:
                        try {
                            //内部的List 解析不出来
                            SpinnerDataInfo sspp = ReflectHelper.map2Bean((Map) childViewModel.getChildViewObj(), SpinnerDataInfo.class);
                            //找寻元数据的list赋值
                            for (SpinnerDataInfo spinnerDataInfoRaw :
                                    node.spinners) {
                                if (spinnerDataInfoRaw.ID == sspp.ID) {
                                    sspp.datas = spinnerDataInfoRaw.datas;
                                    break;
                                }
                            }
                            initSpinner(sspp, child, node.ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                }

            }
            if (checkBoxGroup.getChildCount() > 0) {
                child.addView(checkBoxGroup);//!!!
            }

            if (radioGroup.getChildCount() > 0) {
                child.addView(radioGroup);//!!!
            }
        }

        // 生成多选项
   /*     if (cb.cbs != null) {

            LinearLayout content = new LinearLayout(mContext);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (CheckBox cbx : cb.cbs) {
                initCheckBox(cbx, content, false, node);
            }
            if (content.getChildCount() > 0) {
                child.addView(content);
            }
        }



        // 生成单选项
        if (cb.rbs != null) {

            RadioGroup rg = new RadioGroup(mContext);
            rg.setOrientation(LinearLayout.VERTICAL);
            rg.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (RadioBox rb : cb.rbs) {
                initRadioBox(rb, rg, false, node);
            }
            if (rg.getChildCount() > 0) {
                child.addView(rg);
            }
        }

        // 生成输入框
        if (cb.inputs != null) {
            for (Input in : cb.inputs) {
                initInput(in, cb.labels, child, node);
            }
        }

        // 生成 时间选择框
        if (cb.datetimes != null) {
            for (DateTime date : cb.datetimes) {
                initDateTime(date, child, node);
            }
        }

        // 生成 数字输入框
       if (cb.numbers != null) {
            for (Numeric number : cb.numbers) {
                initNumberic(number, cb.labels, child, node);
            }
        }
*/
        if (EmptyTool.isBlank(cb.Dzlx) || "0".equals(cb.Dzlx)) {
            // 当是一级控件时，隐藏二级项
            if (isFristClass && !isSelected) {
                child.setVisibility(View.GONE);
            }

            // 监听点击状态
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                private boolean checked = false;

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    checked = isChecked;
                    // 激活二级控件
                    ViewGroup parent = (ViewGroup) buttonView.getParent();
                    for (int itemPos = 0; itemPos < parent.getChildCount(); itemPos++) {
                        View view = parent.getChildAt(itemPos);
                        if (view instanceof LinearLayout) {

                            int tag = (Integer) view.getTag(R.id.tag_KJ);
                            if (tag == buttonView.getId()) {
                                view.setVisibility(isChecked ? View.VISIBLE
                                        : View.GONE);
                                break;
                            }
                        }
                    }

                    VEntity entity = new VEntity();
                    entity.CtrlType = cb.CtrlType;
                    entity.Score = cb.Score;
                    // entity.Value = cb.Value;
                    entity.XXID = cb.ID;
                    entity.XMID = node.ID;

                    if (StringUtils.isNotEmpty(cb.Jfgz)) {
                        entity.JFGZ = Integer.parseInt(cb.Jfgz);
                    } else {
                        entity.JFGZ = 0;
                    }
                    if (StringUtils.isNotEmpty(cb.Xxdj)) {
                        entity.XXDJ = Integer.parseInt(cb.Xxdj);
                    } else {
                        entity.XXDJ = 0;
                    }
                    entity.Score = computeItemScore(entity.JFGZ, entity.XXDJ,
                            "0", "0", "btn", checked);
                    ItemCheck(entity, isChecked);
                    //xx
                    if (!isChecked) {
                        dealAutoSign4Remove(node.ID);
                    } else {
                        dealAutoSign4Add(entity);
                    }
                    //XX
                    // 状态未改变
                    if (isSelected == isChecked) {
                        if (map.indexOfKey(node.ID) >= 0) {
                            ArrayList<VEntity> list = map.get(node.ID);
                            if (list != null) {
                                list.remove(entity);
                            }
                        }
                        return;
                    }

                    if (map.indexOfKey(node.ID) < 0) {
                        ArrayList<VEntity> list = new ArrayList<VEntity>();
                        map.put(node.ID, list);
                    }
                    ArrayList<VEntity> list = map.get(node.ID);
                    int index = list.indexOf(entity);
                    if (index == -1) {
                        list.add(entity);
                    }
                }
            });
        }
        root.addView(child);
    }

    private void dealAutoSign4Remove(int node_ID) {
        if (map4Sgin.indexOfKey(node_ID) >= 0) {
            ArrayList<VEntity> list = map4Sgin.get(node_ID);
            if (list != null) {
                int removePos = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).XMID == node_ID) {
                        removePos = i;
                        break;
                    }
                }
                if (removePos >= 0) {
                    list.remove(removePos);
                }
            }
        }
        //
        dealAutoSign(node_ID);
    }

    private void dealAutoSign4Add(VEntity entity) {
        //
        if (map4Sgin.indexOfKey(entity.XMID) < 0) {
            ArrayList<VEntity> list = new ArrayList<>();
            list.add(entity);
            map4Sgin.put(entity.XMID, list);
        } else if (map4Sgin.indexOfKey(entity.XMID) >= 0) {
            ArrayList<VEntity> list = map4Sgin.get(entity.XMID);
            if (list != null) {
                int index = list.indexOf(entity);
                if (index != -1) {
                    list.set(index, entity);
                } else {
                    list.add(entity);
                }
            }
        }
        //
        dealAutoSign(entity.XMID);
    }

    private void dealAutoSign(int XMID) {
        if (map4Sgin == null) {
            Toast.makeText(mContext, "map4Sgin null", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        Classification clazzAAA = null;
//        int parentId=-1;
        List<ItemNode> itemNodeList = new ArrayList<>();
        for (Classification clazz : wholeForm.clazzs) {
            for (ItemNode itemNode : clazz.itemNodes) {
                if (XMID == itemNode.ID) {
//                   classicId=clazz.ID;//11
                    clazzAAA = clazz;
//                   parentId = node_ID;//83
                    //
                    itemNodeList = clazz.itemNodes;
                    break;
                }
            }
        }
        boolean hasValue = false;
        for (ItemNode itemNode : itemNodeList) {
            //==========
            int index = map4Sgin.indexOfKey(itemNode.ID);
            if (index >= 0) {
                ArrayList<VEntity> list = map4Sgin.get(map4Sgin.keyAt(index));
                if (list != null && !list.isEmpty()) {
                    hasValue = true;
                    break;
                }
            }
            //===============
        }
        //
        if (clazzAAA == null) {
            Toast.makeText(mContext, "clazzAAA null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hasValue) {
            //自动签名 classicId
//            Toast.makeText(mContext, "自动签名" + clazzAAA.ID, Toast.LENGTH_SHORT).show();
        } else {
            //取消写 classicId
//            Toast.makeText(mContext, "取消写" + clazzAAA.ID, Toast.LENGTH_SHORT).show();
        }
        if (signListener != null) {
            boolean cancelSign = !hasValue;
            signListener.onAutoSign(clazzAAA, cancelSign);
        }
    }


    /**
     * 顶部名称
     *
     * @param content
     * @return
     */
    private TextView addFormTitle(String content, String syzt) {
        TextView txt = ViewBuildHelper.buildTextViewMatchWrap(mContext, content);
        TextViewCompat.setTextAppearance(txt, R.style.ClassicTextAppearancePrimary);
        txt.setPadding(SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary(),
                SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary());
        //
        txt.setGravity(Gravity.CENTER);
        return txt;

    }

    private TextView addTypeTextView(final ItemNode node) {
        if (EmptyTool.isBlank(node.Dzlx)) {
            if (node.cbs != null) {
                for (CheckBox cb : node.cbs) {
                    if (!EmptyTool.isBlank(cb.Dzlx)) {
                        node.Dzlx = cb.Dzlx;
                        node.Dzxm = cb.Dzxm;
                        node.Dzbd = cb.Dzbd;
                        node.Dzbdlx = cb.Dzbdlx;
                        break;
                    }
                }
            }
            if (node.rbs != null) {
                for (RadioBox rb : node.rbs) {
                    node.Dzlx = rb.Dzlx;
                    node.Dzxm = rb.Dzxm;
                    node.Dzbd = rb.Dzbd;
                    node.Dzbdlx = rb.Dzbdlx;
                    break;
                }
            }
        }
        TextView typeView = ViewBuildHelper.buildTextView(mContext, node.NText);
//        typeView.getPaint().setFakeBoldText(true);
//        typeView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
       /* typeView.setTextSize(
                DimensionTool.getDimensionPx(mContext, R.dimen.classic_text_size_primary));*/
        if (!EmptyTool.isBlank(node.Dzlx) && !node.Dzlx.equals("-1")) {
            if (node.Dzlx.equals("2")) {
                // 风险
                typeView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.green));
            } else if (node.Dzlx.equals("3")) {
                // 宣教
                typeView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.blue));
            } else if (node.Dzlx.equals("5")) {
                // 体征
                typeView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.pink));
            }
        }

        typeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (node.Dzlx.equals("2")) {
                    // 风险
                    intent = new Intent(mContext, RiskEvaluateActivity.class);
                  /* 2017年4月27日08:46:43  remove
                    intent.putExtra("PGDH", node.Dzbd);
                    intent.putExtra("PGLX", node.Dzbdlx);*/
                    intent.putExtra("PGDH", "");
                    intent.putExtra("PGLX", node.Dzbd);//(风险评估：一种类型对应多个表单): DZBD对应IENR_PGJL的PGLX 值就是PGZF
                    intent.putExtra("FROMOUT", true);
                    intent.putExtra("TXSJ", Txsj);
                    mFragment.startActivityForResult(intent, REQ_RISK);
                } else if (node.Dzlx.equals("3")) {
                    intent = new Intent(mContext, HealthGuidActivity.class);
                    intent.putExtra("lxbh", node.Dzbd);
                    intent.putExtra("type", node.Dzbdlx);
                    intent.putExtra("operType", "9");
                    intent.putExtra("TXSJ", Txsj);
                    mFragment.startActivityForResult(intent, REQ_GUID);
                } else if (node.Dzlx.equals("5")) {
                    // 体征
                    intent = new Intent(mContext, LifeSymptomActivity.class);
                    intent.putExtra("TXSJ", Txsj);
                    mFragment.startActivityForResult(intent, REQ_LIFE);
                }
            }
        });
        return typeView;
    }


    /**
     * 计算项目及整张表单的分数
     *
     * @param jfgz        计分规则
     * @param xxdj        选项等级
     * @param srnr        输入类容
     * @param score       原先值
     * @param controlType 控件类型（txt，btn）
     * @param isChecked   是否选中（供btn）
     * @return 选项新值
     */
    private String computeItemScore(int jfgz, int xxdj, String srnr,
                                    String score, String controlType, boolean isChecked) {
        float value = 0L;
        float returnValue = 0L;
        float oldScore = 0L;
        // 不计分直接返回0
        if (jfgz == 0) {
            return "0";
        }
        // 项目分
        if (jfgz == 1) {
            returnValue = xxdj;
        }
        // 输入分,项目分*输入分
        if (jfgz == 2 || jfgz == 3) {
            try {
                if (StringUtils.isNotEmpty(srnr)) {
                    value = Float.parseFloat(srnr);
                } else {
                    value = 0;
                }
                if (StringUtils.isNotEmpty(score)) {
                    oldScore = Float.parseFloat(score);
                } else {
                    oldScore = 0;
                }
            } catch (NumberFormatException e) {
                value = 0;
                oldScore = 0;
            }
            returnValue = value;
        }
        if ("txt".equals(controlType)) {
            if (jfgz == 3) {
                totalScore += (returnValue - oldScore) * xxdj;
            } else {
                totalScore += returnValue - oldScore;
            }
        } else if ("btn".equals(controlType)) {
            if (isChecked) {
                totalScore += returnValue;
            } else {
                totalScore -= returnValue;
            }
        }
        wholeForm.Score = String.valueOf((int) totalScore);
        return String.valueOf(returnValue);
    }

    /**
     * @param @param entity
     * @param @param isAdd
     * @return void
     * @throws
     * @Description: 必填项是否填写设置
     */
    private void ItemCheck(VEntity entity, boolean isAdd) {
        // Radio,Input,Check,Numeric,
        for (CheckCls cls : mCheckForm.CLS) {
            if (cls.ITEMS == null)
                continue;
            for (CheckItem item : cls.ITEMS) {
                if (entity.XMID == item.XMID) {
                    if (entity.CtrlType.equals("Numeric")//change  2017-7-3 17:12:03
                            || entity.CtrlType.equals("Input")
                            || entity.CtrlType.equals("SpinnerDataInfo")
                            || entity.CtrlType.equals("SpecSpinnerDataInfo")) {
                        // 输入型的，字符为空了即为未填写
                        if (EmptyTool.isBlank(entity.Value))
                            item.TXBZ = "0";
                        else
                            item.TXBZ = "1";
                    } else if (entity.CtrlType.equals("RadioBox")
                            || entity.CtrlType.equals("CheckBox")) {
                        if (item.ITEM == null) {
                            // 选择型的如果是添加则直接加入
                            if (isAdd) {
                                item.ITEM = new ArrayList<ItemChild>();
                                ItemChild child = new ItemChild();
                                child.XXID = entity.XXID;
                                item.ITEM.add(child);
                            }
                        } else {
                            if (isAdd) {
                                ItemChild child = new ItemChild();
                                child.XXID = entity.XXID;
                                item.ITEM.add(child);
                            } else {
                                for (int i = 0; i < item.ITEM.size(); i++) {
                                    if (item.ITEM.get(i).XXID == entity.XXID) {
                                        item.ITEM.remove(i);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 下拉控件适配器
     * @date 2015-12-28 下午1:34:40
     */
    class SpinnerAdapter extends BaseAdapter {
        private List<DropData> mData;

        public SpinnerAdapter(List<DropData> datas) {
            this.mData = datas;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public DropData getItem(int position) {
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
            tv.setText(mData.get(position).XMMC);
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
            tv.setText(mData.get(position).XMMC);
            return convertView;
        }

    }
}
