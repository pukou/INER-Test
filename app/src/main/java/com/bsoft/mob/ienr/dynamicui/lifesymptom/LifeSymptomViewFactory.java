package com.bsoft.mob.ienr.dynamicui.lifesymptom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignControlItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignInputItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataTerm;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTypeItem;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.DensityUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.DoubleCheckView;
import com.bsoft.mob.ienr.view.ExceptView;
import com.bsoft.mob.ienr.view.PullEditView;
import com.bsoft.mob.ienr.view.PullEditView.OnSelectListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-28 下午11:05:02
 * @类说明
 */
@Deprecated
public class LifeSymptomViewFactory {
    //异常标志map，key表示控件id，value表示异常标志的值：-2，-1，0，1，2
    Map<String, String> abnormityMap;
    Context context;
    View root;
    ProgressBar emptyProgress;

    int wid = 50 / 2;

    private String saveTime = null;

    /**
     * 控件保存 方便检索
     */
    // 时间控件 特殊处理
    private PullEditView timeEdit;

    // 是否是临时体征
    CheckBox checkBox;
    // 普通编辑控件集合
    public ArrayList<EditText> editViewList;
    // 下拉编辑控件集合
    public ArrayList<PullEditView> pullEditViewList;
    // 复测控件集合
    public ArrayList<DoubleCheckView> doubleCheckViewList;
    // 特殊控件集合
    public ArrayList<ExceptView> exceptViewList;
    // item的集合--只保存动态控件的ItemView
    // public HashMap<Integer, LinearLayout> inputItemMap;
    public SparseArray<LinearLayout> inputItemMap;

    private boolean isTimeMode = false;// 时间是否可修改
    private String tmpTime;// 临时时间
    AppApplication application;
    Date serverDate;


    public LifeSymptomViewFactory(Context context, View root, boolean mode, Date serverDate, AppApplication application) {
        abnormityMap = new HashMap<>();
        this.serverDate = serverDate;
        this.application = application;
        this.context = context;
        this.root = root;
        this.isTimeMode = mode;
        wid = DensityUtil.dp2px(context, 10);
        editViewList = new ArrayList<EditText>();
        pullEditViewList = new ArrayList<PullEditView>();
        doubleCheckViewList = new ArrayList<DoubleCheckView>();
        exceptViewList = new ArrayList<ExceptView>();
        inputItemMap = new SparseArray<LinearLayout>();
    }

    public void setTimeValue(Object obj) {
        if (obj != null && !EmptyTool.isBlank(obj.toString())) {
            String time = obj.toString();
            if (null != timeEdit) {
                timeEdit.getEditText().setText(time);
            } else {
                saveTime = time;
            }
        } else {
            if (tmpTime == null || EmptyTool.isBlank(tmpTime)) {
                tmpTime = DateUtil.format_yyyyMMdd_HHmm.format(serverDate);
            }
        }
    }

    public String getTmpTime() {
        return tmpTime;
    }

    // 数据清空
    public void clearData() {
        abnormityMap = new HashMap<>();
        for (EditText editText : editViewList) {
            editText.setText("");
        }
        for (PullEditView pullEdit : pullEditViewList) {
            pullEdit.getEditText().setText("");
        }
        for (DoubleCheckView doubleCheck : doubleCheckViewList) {
            doubleCheck.clear();
        }
        for (ExceptView exceptView : exceptViewList) {
            exceptView.clear();
        }
    }

    public void setProgressBar(ProgressBar emptyProgress) {
        this.emptyProgress = emptyProgress;
    }

    public String isTemp() {
        return "0";
//        return checkBox.isChecked() ? "1" : "0";
    }

    // 返回需要提交的XML
    public List<LifeSignSaveDataItem> getRealSaveData() {
        List<LifeSignSaveDataItem> lifeSignSaveDataItemList = new ArrayList<>();
        boolean isFlage = false;
        String time;
        if (null != timeEdit) {
            time = String.valueOf(DateUtil.getMinuteTime(timeEdit.getEditText()
                    .getText().toString()));
        } else if (saveTime != null) {
            time = String.valueOf(DateUtil.getMinuteTime(saveTime));
        } else if (tmpTime != null) {
            time = "-1";//当前状态就去取临时时间
        } else {
            time = String.valueOf(DateUtil.getMinuteTime(null));
        }

        // 复测控件列表
        for (DoubleCheckView dv : doubleCheckViewList) {
            if (null != dv.getItem(lifeSignSaveDataItemList, "0", time)) {
                isFlage = true;
            }
        }
        for (ExceptView ev : exceptViewList) {
            if (ev.getItem(lifeSignSaveDataItemList, time)) {
                isFlage = true;
            }
        }
        for (EditText editText : editViewList) {
            String abnormity = getAbnormity(editText.getTag().toString());
            if (EmptyTool.isBlank(editText.getText()) || abnormity.equals("-2") || abnormity.equals("2")) {
                continue;
            }
            getItem(lifeSignSaveDataItemList, editText.getTag().toString(), editText
                    .getText().toString(), abnormity, time);
            isFlage = true;
        }
        for (PullEditView pullEditText : pullEditViewList) {
            if (null == pullEditText.getOnSelectListener()) {
                if (pullEditText.getTag() == null) {
                    continue;
                }
                String abnormity = getAbnormity(pullEditText.getTag().toString());
                if (EmptyTool.isBlank(pullEditText.getEditText().getText()) || abnormity.equals("-2") || abnormity.equals("2")) {
                    continue;
                }
                getItem(lifeSignSaveDataItemList, pullEditText.getTag().toString(),
                        pullEditText.getEditText().getText().toString(),
                        abnormity, time);
                isFlage = true;
            }
        }
        if (isFlage) {
            return lifeSignSaveDataItemList;
        } else {
            return null;
        }
    }

    private String getAbnormity(String id) {
        String abnormity = "0";
        if (abnormityMap.containsKey(id)) {
            abnormity = abnormityMap.get(id);
        }
        return abnormity;
    }

    public void getItem(List<LifeSignSaveDataItem> lifeSignSaveDataItemList, String saveID, String data,
                        String abnormity, String time) {
        LifeSignSaveDataItem lifeSignSaveDataItem = new LifeSignSaveDataItem();
        lifeSignSaveDataItem.TZXM = saveID;
        lifeSignSaveDataItem.Data = data;
        lifeSignSaveDataItem.YCBZ = abnormity;
        lifeSignSaveDataItem.lifeSignSaveDataTermList = new ArrayList<>();
        LifeSignSaveDataTerm lifeSignSaveDataTerm1 = new LifeSignSaveDataTerm();
        lifeSignSaveDataTerm1.ID = "1";
        lifeSignSaveDataTerm1.Data = time;
        lifeSignSaveDataTerm1.Name = "Term";
        lifeSignSaveDataItem.lifeSignSaveDataTermList.add(lifeSignSaveDataTerm1);
        lifeSignSaveDataItemList.add(lifeSignSaveDataItem);
    }

    private boolean mIsChangGui = false;

    public View build(ArrayList<LifeSignTypeItem> list, String zyh, String jgid) {
        return build(list, zyh, jgid, false);
    }

    public View build(ArrayList<LifeSignTypeItem> list, String zyh, String jgid, boolean isChangGui) {
        mIsChangGui = isChangGui;
        //
        LinearLayout view = new LinearLayout(context);
//        view.setBackgroundColor(Color.GREEN);
        view.setOrientation(mIsChangGui ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        //体温控件
        view.addView(getIsTemView());

        for (LifeSignTypeItem vo : list) {
            view.addView(parserType(vo, zyh, jgid));
        }
        return view;
    }

    private View parserType(LifeSignTypeItem lifeSignTypeItem, String zyh, String jgid) {
        LinearLayout rootLayout = LayoutParamsHelper.buildLinearMatchWrap_V(context);
        if (mIsChangGui) {
            rootLayout.setBackgroundColor(Color.BLUE);
            rootLayout = LayoutParamsHelper.buildLinearMatchWrap_H(context);
        }
        //
        Pair<LinearLayout, TextView> classTextViewLayout = ViewBuildHelper.buildClassTextViewLayout(context, lifeSignTypeItem.LBMC);
        LinearLayout classLayout = classTextViewLayout.first;
        TextView classTxt = classTextViewLayout.second;
        //
        LinearLayout contentLayout = LayoutParamsHelper.buildLinearMatchWrap_V(context);
        if (mIsChangGui) {
            contentLayout.setBackgroundColor(Color.YELLOW);
//            contentLayout = LayoutParamsHelper.buildLinearMatchWrap_H(context);
        }

        if ("基本体征".equals(lifeSignTypeItem.LBMC)) {
            //默认显示第一个基本体征
            contentLayout.setVisibility(View.VISIBLE);
        } else {
            contentLayout.setVisibility(View.GONE);
        }
        //协和需求  常规项目 不显示分类
        if (classLayout != null && "基本体征".equals(lifeSignTypeItem.LBMC)) {
            classLayout.setVisibility(mIsChangGui ? View.GONE : View.VISIBLE);
        }

        //
        classTxt.setTag(contentLayout);
        classTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout contentLayout = (LinearLayout) v.getTag();
                if (contentLayout.getVisibility() == View.GONE) {
                    contentLayout.setVisibility(View.VISIBLE);
                    v.setSelected(true);
                } else {
                    contentLayout.setVisibility(View.GONE);
                    v.setSelected(false);
                }
            }
        });
        //
        if (null != lifeSignTypeItem.LifeSignInputItemList && lifeSignTypeItem.LifeSignInputItemList.size() > 0) {
            for (LifeSignInputItem lifeSignInputItem : lifeSignTypeItem.LifeSignInputItemList) {
                String tsbz = "0";
                String tzxm = "0";
                if (null != lifeSignInputItem.LifeSignControlItemList && lifeSignInputItem.LifeSignControlItemList.size() > 0) {
                    for (LifeSignControlItem lifeSignControlItem : lifeSignInputItem.LifeSignControlItemList) {
                        if (lifeSignControlItem.TSBZ != null && !lifeSignControlItem.TSBZ.equals("0")) {
                            tsbz = lifeSignControlItem.TSBZ;
                        }
                        if (lifeSignControlItem.TZXM != null && !lifeSignControlItem.TZXM.isEmpty() && !lifeSignControlItem.TZXM.equals("0")) {
                            tzxm = lifeSignControlItem.TZXM;
                        }
                    }
                }
                View addView = null;
                switch (tsbz) {
                    case "1":
                        // 时间控件
                        addView = parserInputItem(lifeSignInputItem, zyh, jgid);
                        if (null != addView) {
                            contentLayout.addView(addView);
                        }
                        break;
                    case "2":
                        // 体温类型控件
                        ExceptView exceptView = new ExceptView(context, root, lifeSignInputItem,
                                zyh, application, jgid);
                        contentLayout.addView(exceptView);
                        exceptViewList.add(exceptView);
                        break;
                    default:
                        // 普通类型控件
                        addView = parserInputItem(lifeSignInputItem, zyh, jgid);
                        if (null != addView) {
                            contentLayout.addView(addView);
                        }
                        break;
                }
                if (!mIsChangGui) {
                    if ("1".equals(tzxm) || "502".equals(tzxm)) {
                        // 复测控件
                        DoubleCheckView doubleckview = new DoubleCheckView(context,
                                root, zyh, application, tzxm, jgid);
                        contentLayout.addView(doubleckview);
                        doubleCheckViewList.add(doubleckview);
                    }
                }
            }
        }
        rootLayout.addView(classLayout);
        rootLayout.addView(contentLayout);
        return rootLayout;
    }

    private View parserInputItem(LifeSignInputItem lifeSignInputItem, String zyh, String jgid) {
        LinearLayout groupLinearLayout = new LinearLayout(context);
        groupLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//        groupLinearLayout.setBackgroundResource(R.drawable.shape_classic_bg_view);
        groupLinearLayout.setOrientation(mIsChangGui ? LinearLayout.VERTICAL : LinearLayout.VERTICAL);
//        groupLinearLayout.setBackgroundColor(Color.CYAN);
      /*  HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));*/
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(mIsChangGui ? LinearLayout.VERTICAL : LinearLayout.VERTICAL);
        view.setBackgroundResource(R.drawable.shape_classic_bg_view);
        view.setLayoutParams(new HorizontalScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER_VERTICAL);
        if (null != lifeSignInputItem.LifeSignControlItemList && lifeSignInputItem.LifeSignControlItemList.size() > 0) {
            for (LifeSignControlItem lifeSignControlItem : lifeSignInputItem.LifeSignControlItemList) {
                int tsbz = lifeSignControlItem.TSBZ == null ? 0 : Integer.parseInt(lifeSignControlItem.TSBZ);
                View addView = parserItem(lifeSignControlItem, tsbz, zyh, jgid);
                if (null != addView) {
                    view.addView(addView);
                }
            }
        }
        view.setId(Integer.parseInt(lifeSignInputItem.SRXH));
        inputItemMap.put(Integer.parseInt(lifeSignInputItem.SRXH), view);

      /*  scrollView.addView(view);
        return scrollView;*/
        groupLinearLayout.addView(view);
        return groupLinearLayout;
    }

    /*
    升级编号【56010029】============================================= start
体温项目调用体温键盘，体温键盘‘DEL’改为‘删除’、体温支持小键盘输入
    ================= Classichu 2017/11/20 16:47
    */
//    KeyboardUtil keyboard;
    InputMethodManager imm;


    // 隐藏系统键盘
    private void hideSoftInputMethod(EditText ed) {
        imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /* =============================================================== end */
    View parserItem(final LifeSignControlItem lifeSignControlItem, int tsbz,
                    final String zyh, final String jgid) {
        if (lifeSignControlItem.KJLX == null) {
            Log.e(Constant.TAG,
                    "itemType IS NULL IN LifeSymptomViewFactory's parserChildItem method");
            return null;
        }
        switch (lifeSignControlItem.KJLX) {
            case "1":
                return null;
//                return ViewBuildHelper.buildTextView(context, lifeSignControlItem.KJNR);
            case "4":
                final PullEditView pullEditView = new PullEditView(context, mIsChangGui);
                if (null != lifeSignControlItem.LifeSignOptionItemList) {
                    pullEditView.setDataList(lifeSignControlItem.LifeSignOptionItemList);
                }
//                pullEditView.popWidth = Integer.parseInt(lifeSignControlItem.KJCD) * wid + 70;
                /*
                升级编号【56010051】============================================= start
                PB端维护单纯勾选数字输入时候，PDA端不可输入
                ================= Classichu 2017/11/13 14:21
                */
//                if ("1".equals(lifeSignControlItem.QTSR)) {
                if ("1".equals(lifeSignControlItem.QTSR) || "1".equals(lifeSignControlItem.SZSR)) {
                    /* =============================================================== end */
                    pullEditView.setEditIsAdble(true);
                    String text = lifeSignControlItem.KJNR;
                    if (!EmptyTool.isBlank(text)) {
                        pullEditView.getEditText().setText(text);
                    }
                } else {
                    pullEditView.setEditIsAdble(false);
                    // 默认选择第一个选项
                    if (null != lifeSignControlItem.LifeSignOptionItemList
                            && lifeSignControlItem.LifeSignOptionItemList.size() > 0) {
                        pullEditView.getEditText().setText(
                                lifeSignControlItem.LifeSignOptionItemList.get(0).XZNR);
                    }
                }
                // 设置
                setNumListener(pullEditView.getEditText(), lifeSignControlItem);
                   /*
    升级编号【56010029】============================================= start
体温项目调用体温键盘，体温键盘‘DEL’改为‘删除’、体温支持小键盘输入
    ================= Classichu 2017/11/20 16:47
    */

                final EditText editTe = pullEditView.getEditText();
                //默认显示数字键盘
//                editTe.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTe.setRawInputType(Configuration.KEYBOARD_QWERTY);
                if ("1".equals(lifeSignControlItem.TZXM)//体温 1
                        ) {
                    //
                    /*keyboard = new KeyboardUtil(root, context);
                    keyboard.configEdit(editTe);*/
                    imm = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                   /* editTe.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            keyboard.showKeyboard(editTe);
                            return false;
                        }
                    });*/
                  /*  editTe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            pullEditView.setSelected(hasFocus);
                            if (!hasFocus) {
                                keyboard.hideKeyboard();
                            } else {
                                hideSoftInputMethod(editTe);
                            }
                        }
                    });*/
                    editTe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            pullEditView.setSelected(hasFocus);
                        }
                    });
                } else {
                    editTe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            pullEditView.setSelected(hasFocus);
                        }
                    });
                }
                /* =============================================================== end */
                pullEditView.setTag(lifeSignControlItem.TZXM);
                if ("1".equals(lifeSignControlItem.TSBZ)) {
                    // 时间控件 唯一
                    pullEditView.setEditIsAdble(false);
                    timeEdit = pullEditView;
                } else {
                    pullEditViewList.add(pullEditView);
                }
                //
                LinearLayout wapperLinearV = LayoutParamsHelper.buildLinearMatchWrap_V(context);
                if (mIsChangGui) {
                    wapperLinearV.setBackgroundColor(Color.GRAY);
//                    wapperLinearV = LayoutParamsHelper.buildLinearMatchWrap_H(context);
                }
                String name = !TextUtils.isEmpty(lifeSignControlItem.XMDW) ? lifeSignControlItem.XMMC + "(" + lifeSignControlItem.XMDW + ")" : lifeSignControlItem.XMMC;
                wapperLinearV.addView(ViewBuildHelper.buildTextView(context, name));
                wapperLinearV.addView(pullEditView);
                return wapperLinearV;
//                return pullEditView;
            case "2":
                EditText editTextTemps = ViewBuildHelper.buildEditTextMatchWrap(context, null);
                if (mIsChangGui) {
                    editTextTemps = ViewBuildHelper.buildEditText(context, null);
                }
                final EditText editText = editTextTemps;
//                editText.setMinimumWidth(Integer.parseInt(lifeSignControlItem.KJCD) * wid);
                editText.setMinimumWidth(150);
                setNumListener(editText, lifeSignControlItem);

                //默认显示数字键盘
//                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setRawInputType(Configuration.KEYBOARD_QWERTY);
                editText.setTag(lifeSignControlItem.TZXM);
                   /*
    升级编号【56010029】============================================= start
体温项目调用体温键盘，体温键盘‘DEL’改为‘删除’、体温支持小键盘输入
    ================= Classichu 2017/11/20 16:47
    */
                if ("1".equals(lifeSignControlItem.TZXM)//体温 1
                        ) {
                    //
                   /* keyboard = new KeyboardUtil(root, context);
                    keyboard.configEdit(editText);*/
                    imm = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                  /*  editText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            keyboard.showKeyboard(editText);
                            return false;
                        }
                    });*/
                   /* editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                keyboard.hideKeyboard();
                            } else {
                                hideSoftInputMethod(editText);
                            }
                        }
                    });*/
                }
                /* =============================================================== end */
                editViewList.add(editText);
                //
                LinearLayout wapperLinearV_editText = LayoutParamsHelper.buildLinearMatchWrap_V(context);
                if (mIsChangGui) {
                    wapperLinearV_editText = LayoutParamsHelper.buildLinearMatchWrap_H(context);
                }
                String name_editText = !TextUtils.isEmpty(lifeSignControlItem.XMDW) ? lifeSignControlItem.XMMC + "(" + lifeSignControlItem.XMDW + ")" : lifeSignControlItem.XMMC;
                wapperLinearV_editText.addView(ViewBuildHelper.buildTextView(context, name_editText));
                wapperLinearV_editText.addView(editText);
                return wapperLinearV_editText;
//                return editText;
            case "3"://活动控件实现
                PullEditView dynamicPullEditView = new PullEditView(context);
//                dynamicPullEditView.popWidth = Integer.parseInt(lifeSignControlItem.KJCD) * wid + 70;
                dynamicPullEditView.setOnSelectListener(new OnSelectListener() {
                    @Override
                    public void doSelect(final String srxh) {
//                        final LinearLayout rootParet = LayoutParamsHelper.buildLinearMatchWrap_V(context);
                        final LinearLayout layout = inputItemMap
                                .get(Integer.parseInt(lifeSignControlItem.SRXH));
                        LinearLayout groupLinearLayout = (LinearLayout) layout.getParent();
                        new AsyncTask<Void, Void, Response<LifeSignInputItem>>() {
                            @Override
                            protected void onPreExecute() {
                                if (null != emptyProgress) {
                                    emptyProgress.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            protected Response<LifeSignInputItem> doInBackground(Void... arg0) {
                                Response<LifeSignInputItem> sresult = LifeSignApi
                                        .getInstance(context).getLifeSignItem(srxh, zyh, jgid, Constant.sysType);
                                return sresult;
                            }

                            @Override
                            protected void onPostExecute(Response<LifeSignInputItem> result) {
                                if (null != result) {
                                    if (result.ReType == 100) {
                                        new AgainLoginUtil(context, application).showLoginDialog();
                                    } else if (result.ReType == 0) {
                                        View dynamicBox = buildDynamicBox(result.Data, zyh, jgid);
                                        //
                                        groupLinearLayout.removeAllViews();
                                        groupLinearLayout.addView(layout);
                                        //
                                        LinearLayout wapperLinear = LayoutParamsHelper.buildLinearMatchWrap_V(context);
                                        wapperLinear.setBackgroundResource(R.drawable.shape_classic_bg_view_bar);
                                        TextView clickText = ViewBuildHelper.buildTextView(context, "撤销");
                                        clickText.setTextColor(Color.WHITE);
                                        clickText.setPadding(20, 10, 20, 10);
                                        clickText.setBackgroundResource(R.drawable.selector_classic_bg_click_o_colored);
                                        clickText.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //
                                                View txt = ViewBuildHelper.buildDialogTitleTextView(context, "温馨提示");
                                                new AlertDialog.Builder(context)
                                                        //   .setTitle("确定撤销？")
                                                        .setCustomTitle(txt)
                                                        .setMessage("确定撤销?")
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog,
                                                                                int which) {
                                                                //
                                                                layout.setVisibility(View.VISIBLE);
                                                                wapperLinear.setVisibility(View.GONE);
                                                            }
                                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).create().show();
                                            }
                                        });
                                        wapperLinear.addView(clickText);
                                        wapperLinear.addView(dynamicBox);
                                        groupLinearLayout.addView(wapperLinear);
                                        layout.setVisibility(View.GONE);
                                        wapperLinear.setVisibility(View.VISIBLE);
                                     /*   layout.removeAllViews();
                                        layout.addView(view);*/
                                    }
                                }
                                if (null != emptyProgress) {
                                    emptyProgress.setVisibility(View.GONE);
                                }
                            }
                        }.execute();
                    }
                });
                if (null != lifeSignControlItem.LifeSignOptionItemList) {
                    dynamicPullEditView.setDataList(lifeSignControlItem.LifeSignOptionItemList);
                }
                dynamicPullEditView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                /*dynamicPullEditView.setLayoutParams(new LinearLayout.LayoutParams(
                        Integer.parseInt(lifeSignControlItem.KJCD) * wid, LayoutParams.WRAP_CONTENT));*/
                // 默认不能编辑
                dynamicPullEditView.setEditIsAdble(false);
                //默认显示数字键盘
//                dynamicPullEditView.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                dynamicPullEditView.getEditText().setRawInputType(Configuration.KEYBOARD_QWERTY);
                pullEditViewList.add(dynamicPullEditView);
                //
                LinearLayout wapperLinearV_dynamicPullEditView = LayoutParamsHelper.buildLinearMatchWrap_V(context);
                String name_dynamicPullEditView = !TextUtils.isEmpty(lifeSignControlItem.XMDW) ? lifeSignControlItem.XMMC + "(" + lifeSignControlItem.XMDW + ")" : lifeSignControlItem.XMMC;
                wapperLinearV_dynamicPullEditView.addView(ViewBuildHelper.buildTextView(context, name_dynamicPullEditView));
                wapperLinearV_dynamicPullEditView.addView(dynamicPullEditView);

                return wapperLinearV_dynamicPullEditView;
            //
//                return dynamicPullEditView;
            default:
                break;
        }
        return null;
    }

    // 是否设置数字输入
    private void setNumListener(EditText edit, LifeSignControlItem lifeSignControlItem) {
    /*
    升级编号【56010033】============================================= start
    【其他】输入修正
    ================= Classichu 2017/10/24 16:52
    */
        ////########2017-9-8 15:52:20 if ("1".equals(lifeSignControlItem.SZSR)) {
        if ("1".equals(lifeSignControlItem.SZSR) && !"1".equals(lifeSignControlItem.QTSR)) {
            /* =============================================================== end */
            edit.setKeyListener(new NumberKeyListener() {

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
        /*
        升级编号【56010052】============================================= start
        PB端维护非数字输入时候，PDA端上下限校验失效
        ================= Classichu 2017/11/13 14:22
        */
        setNumLimit(edit, lifeSignControlItem);
        /* =============================================================== end */
    }

    // 设置输入的区间限制
    private void setNumLimit(final EditText edit, final LifeSignControlItem lifeSignControlItem) {
        if (lifeSignControlItem.isMaxMinAble()) {

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

                    if (!EmptyTool.isBlank(s.toString())) {
                        //拒测等非数字时 不再转化数值进行各种验证
                        if (!s.toString().matches("^[0-9]*(.[0-9]+)?$")) {
                            return;
                        }
                        float inputText;
                        try {
                            inputText = Float.valueOf(s.toString());
                        } catch (Exception e) {
                            return;
                        }
                        int value = lifeSignControlItem.getMaxMinStatue(inputText);
                        switch (value) {
                            case 1:
                                edit.setTextColor(Color.BLACK);
                                edit.setError("数值超出正常下限");
                                abnormityMap.put(lifeSignControlItem.TZXM, "-2");
                                break;
                            case 2:
                                edit.setTextColor(Color.RED);
                                abnormityMap.put(lifeSignControlItem.TZXM, "-1");
                                break;
                            case 3:
                                edit.setTextColor(Color.BLACK);
                                abnormityMap.put(lifeSignControlItem.TZXM, "0");
                                break;
                            case 4:
                                edit.setTextColor(Color.RED);
                                abnormityMap.put(lifeSignControlItem.TZXM, "1");
                                break;
                            case 5:
                                edit.setTextColor(Color.BLACK);
                                edit.setError("数值超出正常上限");
                                abnormityMap.put(lifeSignControlItem.TZXM, "2");
                                break;
                            default:
                                break;
                        }

                    }
                }

            });

            edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String str = ((EditText) v).getText().toString();
                        if (!EmptyTool.isBlank(str)) {
                            //拒测等非数字时 不再转化数值进行各种验证
                            if (!str.matches("^[0-9]*(.[0-9]+)?$")) {
                                return;
                            }
                            float inputText;
                            try {
                                inputText = Float.valueOf(str);
                            } catch (Exception e) {
                                return;
                            }
                            int value = lifeSignControlItem.getMaxMinStatue(inputText);
                            switch (value) {
                                case 1:
                                    edit.setText("");
                                    Toast.makeText(context, "输入数值超出下限",
                                            Toast.LENGTH_SHORT).show();
                                    edit.getLayoutParams().width = 200;
                                    break;
                                case 5:
                                    edit.setText("");
                                    Toast.makeText(context, "输入数值超出上限",
                                            Toast.LENGTH_SHORT).show();
                                    edit.getLayoutParams().width = 200;
                                    break;

                                default:
                                    break;
                            }
                        }

                    }
                }
            });
        }
    }

    // 隐藏弹出框 --- 优化点(不需要遍历实现)
    public void hidden() {
        if (null != timeEdit) {
            timeEdit.hidden();
        }
        for (PullEditView view : pullEditViewList) {
            view.hidden();
        }
        for (ExceptView view : exceptViewList) {
            view.hidden();
        }
    }

    public View buildDynamicBox(LifeSignInputItem lifeSignInputItem, String zyh, String jgid) {
        return parserInputItem(lifeSignInputItem, zyh, jgid);
    }

    // 是否是临时体征控件
    public View getIsTemView() {
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.HORIZONTAL);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//        view.setBackgroundResource(R.color.red);
        if (isTimeMode) {
            tmpTime = DateUtil.format_yyyyMMdd_HHmm.format(serverDate);

            View layout_item_bar_text = LayoutInflater.from(context).inflate(R.layout.layout_item_bar_image, view, false);
            TextView id_tv_for_bar_image = layout_item_bar_text.findViewById(R.id.id_tv_for_bar_image);
            id_tv_for_bar_image.setText("时间：");
            final TextView timeview = layout_item_bar_text.findViewById(R.id.id_tv_2_for_bar_image);
            ImageView id_iv_for_bar_image = layout_item_bar_text.findViewById(R.id.id_iv_for_bar_image);
            timeview.setText(tmpTime);
            id_iv_for_bar_image.setOnClickListener(new OnClickListener() {

                AlertDialog alertDialog;

                @Override
                public void onClick(View v) {
                    String nowSelected = timeview.getText().toString();
                    YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(nowSelected);
                    int monthOfYear = ymdHMs.month < 1 ? 0 : ymdHMs.month - 1;

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View content = LayoutInflater.from(context).inflate(
                            R.layout.layout_dialog_date_time, null, false);
                    final DatePicker datePicker = (DatePicker) content
                            .findViewById(R.id.datePicker1);
                    datePicker.init(ymdHMs.year, monthOfYear, ymdHMs.day, null);

                    final TimePicker timePicker = (TimePicker) content
                            .findViewById(R.id.timePicker1);

                    boolean is24HourFormat = DateFormat.is24HourFormat(context);
                    timePicker.setIs24HourView(is24HourFormat);
                    int h = ymdHMs.hour;
                    int minute = ymdHMs.minute;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setHour(h);
                        timePicker.setMinute(minute);
                    } else {
                        timePicker.setCurrentHour(h);
                        timePicker.setCurrentMinute(minute);
                    }

                    builder.setView(content);
                    builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(context, "选择日期时间"))
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            //
                                            int monthOfYear = datePicker.getMonth();
                                            //
                                            int year = datePicker.getYear();
                                            int month = monthOfYear > 11 ? 12 : monthOfYear + 1;
                                            int day = datePicker.getDayOfMonth();
                                            int hour;
                                            int minute;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                hour = timePicker.getHour();
                                                minute = timePicker.getMinute();
                                            } else {
                                                hour = timePicker.getCurrentHour();
                                                minute = timePicker.getCurrentMinute();
                                            }
                                            String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, day, hour, minute, 0);
                                            //
                                            tmpTime = dateTime;
                                            timeview.setText(tmpTime);
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel, null);
                    if (alertDialog == null) {
                        alertDialog = builder.create();
                    }
                    alertDialog.show();
                }
            });
            view.addView(layout_item_bar_text);
        }

        return view;
    }
}
