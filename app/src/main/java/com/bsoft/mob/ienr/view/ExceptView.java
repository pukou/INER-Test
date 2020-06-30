package com.bsoft.mob.ienr.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignControlItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignInputItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataTerm;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DensityUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.PullEditView.OnSelectListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-9 上午9:07:34
 * @类说明 特殊控件 exception为2
 */
@SuppressLint("ResourceAsColor")
public class ExceptView extends LinearLayout {
    Context context;

    //体温小键盘支持 start01
    View root;
//    KeyboardUtil keyboard;
    InputMethodManager imm;
    //体温小键盘支持 end01

    ProgressBar emptyProgress;
    // exception 2 控件
    PullEditView exPullEditView;
    LifeSignInputItem lifeSignInputItem;
    int wid = 50 / 2;

    // 普通编辑控件集合
    public ArrayList<EditText> editViewList = new ArrayList<EditText>();
    // 下拉编辑控件集合
    public ArrayList<PullEditView> pullEditViewList = new ArrayList<PullEditView>();
    // item的集合--只保存动态控件的ItemView
    // public HashMap<Integer, LinearLayout> inputItemMap = new HashMap<Integer,
    // LinearLayout>();
    public SparseArray<LinearLayout> inputItemMap = new SparseArray<LinearLayout>();

    AppApplication application;

    //体温小键盘支持 start02
    public ExceptView(Context context, View root, LifeSignInputItem lifeSignInputItem,
                      String zyh, AppApplication application, String jgid) {
        super(context);
        this.lifeSignInputItem = lifeSignInputItem;
        this.application = application;
        wid = DensityUtil.dp2px(context, 10);
        init(context, root, zyh, jgid);
    }
    //体温小键盘支持 end01

    public void clear() {
        for (EditText editText : editViewList) {
            editText.setText("");
        }
        for (PullEditView pullEdit : pullEditViewList) {
            pullEdit.getEditText().setText("");
        }
    }

    // 隐藏弹出框 --- 优化点(不需要遍历实现)
    public void hidden() {
        for (PullEditView view : pullEditViewList) {
            view.hidden();
        }
    }

    //体温小键盘支持 start03
    void init(Context context, View root, String zyh, String jgid) {
        this.context = context;
        this.root = root;
      /*  keyboard = new KeyboardUtil(root, context);
        if (exPullEditView!=null) {
            keyboard.configEdit(exPullEditView.edit);
        }*/
        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, 30, 0, 0);
        if (null != lifeSignInputItem && null != lifeSignInputItem.LifeSignControlItemList
                && lifeSignInputItem.LifeSignControlItemList.size() > 0) {
            for (LifeSignControlItem lifeSignControlItem : lifeSignInputItem.LifeSignControlItemList) {
                View view = parserItem(lifeSignControlItem, zyh, jgid);
                if (null != view) {
                    this.addView(view);
                }
            }
        }

    }
    //体温小键盘支持 end03

    public boolean getItem(List<LifeSignSaveDataItem> lifeSignSaveDataItemList, String time) {
        String s = exPullEditView.getEditText().getText().toString();
        boolean isFlage = false;
        if (null != s && s.length() > 0) {
            for (EditText et : editViewList) {
                String text = et.getText().toString();
                if (null != text && text.length() > 0) {
                    LifeSignSaveDataItem lifeSignSaveDataItem = addEle(time, text, s);
                    lifeSignSaveDataItemList.add(lifeSignSaveDataItem);
                    isFlage = true;
                }
            }

            for (PullEditView pet : pullEditViewList) {
                String text = pet.getEditText().getText().toString();
                if (null != text && text.length() > 0) {
                    LifeSignSaveDataItem lifeSignSaveDataItem = addEle(time, text, s);
                    lifeSignSaveDataItemList.add(lifeSignSaveDataItem);
                    isFlage = true;
                }
            }
        }
        return isFlage;
    }

    private LifeSignSaveDataItem addEle(String time, String text, String s) {
        LifeSignSaveDataItem lifeSignSaveDataItem = new LifeSignSaveDataItem();
        lifeSignSaveDataItem.TZXM = "1";
        lifeSignSaveDataItem.Data = text;
        lifeSignSaveDataItem.YCBZ = "0";
        lifeSignSaveDataItem.lifeSignSaveDataTermList = new ArrayList<>();
        LifeSignSaveDataTerm lifeSignSaveDataTerm1 = new LifeSignSaveDataTerm();
        lifeSignSaveDataTerm1.ID = "1";
        lifeSignSaveDataTerm1.Data = time;
        lifeSignSaveDataTerm1.Name = "Term";
        lifeSignSaveDataItem.lifeSignSaveDataTermList.add(lifeSignSaveDataTerm1);
        LifeSignSaveDataTerm lifeSignSaveDataTerm2 = new LifeSignSaveDataTerm();
        lifeSignSaveDataTerm2.ID = "2";
        lifeSignSaveDataTerm2.Data = s;
        lifeSignSaveDataTerm2.Name = "Term";
        lifeSignSaveDataItem.lifeSignSaveDataTermList.add(lifeSignSaveDataTerm2);
        return lifeSignSaveDataItem;
    }

    View parserItem(final LifeSignControlItem lifeSignControlItem, final String zyh, final String jgid) {
        switch (lifeSignControlItem.KJLX) {
            case "1":
                TextView textView = new TextView(context);
                textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                TextViewCompat.setTextAppearance(textView,R.style.ClassicTextAppearanceSecondaryColored);
                textView.setText(lifeSignControlItem.KJNR);
                return textView;
            case "4":
                PullEditView pullEditView = new PullEditView(context);
                if (null != lifeSignControlItem.LifeSignOptionItemList) {
                    pullEditView.setDataList(lifeSignControlItem.LifeSignOptionItemList);
                }
                pullEditView.popWidth = Integer.parseInt(lifeSignControlItem.KJCD) * wid + 70;
                if ("1".equals(lifeSignControlItem.QTSR)) {
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
                pullEditView.setTag(lifeSignControlItem.TZXM);
                if ("2".equals(lifeSignControlItem.TSBZ)) {
                    exPullEditView = pullEditView;
                } else {
                    pullEditViewList.add(pullEditView);
                }
                return pullEditView;
            case "2":
                EditText editText = new EditText(context);
                editText.setMinimumWidth(Integer.parseInt(lifeSignControlItem.KJCD) * wid);
                editText.setMinWidth(SizeHelper.getEditMinWidth());
                editText.setSingleLine();
                setNumListener(editText, lifeSignControlItem);
                editText.setTag(lifeSignControlItem.TZXM);
                SizeHelper.setPadding(editText,8);
                TextViewCompat.setTextAppearance(editText, R.style.ClassicTextAppearanceSecondary);
                editText.setBackgroundResource(R.drawable.selector_classic_bg_edit);
                editViewList.add(editText);
                return editText;
            case "3"://活动控件
                PullEditView dynamicPullEditView = new PullEditView(context);
                dynamicPullEditView.popWidth = Integer.parseInt(lifeSignControlItem.KJCD) * wid + 70;
                dynamicPullEditView.setOnSelectListener(new OnSelectListener() {
                    @Override
                    public void doSelect(final String srxh) {
                        final LinearLayout layout = inputItemMap
                                .get(Integer.parseInt(lifeSignControlItem.SRXH));
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
                                        View view = buildDynamicBox(result.Data, zyh, jgid);
                                        layout.removeAllViews();
                                        layout.addView(view);
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
                dynamicPullEditView.setLayoutParams(new LinearLayout.LayoutParams(
                        Integer.parseInt(lifeSignControlItem.KJCD) * wid,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                // 默认不能编辑
                dynamicPullEditView.setEditIsAdble(false);
                pullEditViewList.add(dynamicPullEditView);
                return dynamicPullEditView;
            default:
                break;
        }
        return null;
    }

    //体温小键盘支持 start04
    // 是否设置数字输入
    public void setNumListener(final EditText edit, final LifeSignControlItem lifeSignControlItem) {
        if ("1".equals(lifeSignControlItem.SZSR)) {

            setNumLimit(edit, lifeSignControlItem);
      /*      edit.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    keyboard.showKeyboard(edit);
                    return false;
                }
            });*/
            edit.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    ExceptView.this.setSelected(hasFocus);
                    if (!hasFocus) {
                        String str = ((EditText) v).getText().toString();
                        if (!EmptyTool.isBlank(str)) {
                            try {
                                float inputText = Float.valueOf(str);
                                int value = lifeSignControlItem.getMaxMinStatue(inputText);
                                if (value == 0 || value == 6) {
                                    ((EditText) v).setText(null);
                                }
                            } catch (Exception ex) {
                                //((EditText) v).setText(null);
                            }
                        }
//                      ############  keyboard.hideKeyboard();
                    } else {
                        //hideSoftInputMethod(edit);
                    }
                }
            });
        }
    }

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
    //体温小键盘支持 end04

    // 设置输入的区间限制
    public void setNumLimit(final EditText edit, final LifeSignControlItem lifeSignControlItem) {
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
                    float inputText = 0;
                    if (!EmptyTool.isBlank(s.toString())) {
                        try {
                            inputText = Float.valueOf(s.toString());
                        } catch (Exception e) {
                            return;
                        }
                        switch (lifeSignControlItem.getMaxMinStatue(inputText)) {
                            case 1:
                                edit.setTextColor(Color.BLACK);
                                edit.setError("数值超出正常下限");
                                break;
                            case 2:
                                edit.setTextColor(Color.RED);
                                break;
                            case 3:
                                edit.setTextColor(Color.BLACK);
                                break;
                            case 4:
                                edit.setTextColor(Color.RED);
                                break;
                            case 5:
                                edit.setTextColor(Color.BLACK);
                                edit.setError("数值超出正常上限");
                                break;

                            default:
                                break;
                        }

                    }
                }

            });
        }
    }

    public View buildDynamicBox(LifeSignInputItem lifeSignInputItem, String zyh, String jgid) {
        return parserInputItem(lifeSignInputItem, zyh, jgid);
    }

    public View parserInputItem(LifeSignInputItem lifeSignInputItem, String zyh, String jgid) {
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.HORIZONTAL);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER_VERTICAL);
        if (null != lifeSignInputItem.LifeSignControlItemList && lifeSignInputItem.LifeSignControlItemList.size() > 0) {
            for (LifeSignControlItem lifeSignControlItem : lifeSignInputItem.LifeSignControlItemList) {
                View addView = parserItem(lifeSignControlItem, zyh, jgid);
                if (null != addView) {
                    view.addView(addView);
                }
            }
        }
        view.setId(Integer.parseInt(lifeSignInputItem.SRXH));
        inputItemMap.put(Integer.parseInt(lifeSignInputItem.SRXH), view);
        return view;
    }

}
