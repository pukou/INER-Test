package com.bsoft.mob.ienr.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.LifeHistoryAdapter;
import com.bsoft.mob.ienr.adapter.LifeHistoryProcessAdapter;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignDoubleCheckCoolingMeasure;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignDoubleCheckHistoryData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignDoubleCheckHistoryDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataTerm;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DensityUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-9 上午9:07:34
 * @类说明 复测控件
 */
@SuppressLint("ResourceAsColor")
public class DoubleCheckView extends LinearLayout {
    private String viewType;//1:体温复测 502:疼痛复测

    private AlertDialog mAlertDialog;
    Context context;

    //体温小键盘支持 start01
    View root;
//    KeyboardUtil keyboard;
    InputMethodManager imm;
    //体温小键盘支持 end01

    int wid = 50 / 2;

//    ImageButton but;
    TextView but;
    EditText editText;
    TextView textView;
    LifeHistoryAdapter adapter_history;
    LifeHistoryProcessAdapter adapter_measure;
    ListView listView_History, listView_Measure;

    int id;
    String data;
    boolean isSave = false;

    AppApplication application;

    public void clear() {
        editText.setText("");
        if (null != adapter_history) {
            adapter_history.clearChose();
        }
        if (null != adapter_measure) {
            adapter_measure.clearChose();
        }
    }

    //体温小键盘支持 start02
    public DoubleCheckView(Context context, View root, String zyh, AppApplication application, String viewType, String jgid) {
        super(context);
        wid = DensityUtil.dp2px(context, 10);
        this.application = application;
        this.viewType = viewType;
        init(context, root, zyh, jgid);
    }

    void init(Context context, View root, String zyh, String jgid) {
        this.context = context;
        this.root = root;
        /*keyboard = new KeyboardUtil(root, context);
        keyboard.configEdit(editText);*/
        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, 30, 0, 0);

        buildLabel();

        buildButton(zyh, jgid);
        this.addView(but);
        buildEditText();
        this.addView(editText);
        buildTextView();
        this.addView(textView);

    }

    //体温小键盘支持 end02
    private void buildLabel() {

        TextView label = new TextView(context);
        label.setTextSize(18);
        label.setTextColor(Color.GRAY);
        label.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(label);
    }

    public LifeSignSaveDataItem getItem(List<LifeSignSaveDataItem> lifeSignSaveDataItemList, String ycbz, String time) {
        String sdata = editText.getText().toString();
        LifeSignSaveDataItem lifeSignSaveDataItem = null;
        if (null != sdata && sdata.length() > 0 && id != -1 && null != data
                && data.length() > 0) {
            if (viewType.equals("1")) {
                lifeSignSaveDataItem = new LifeSignSaveDataItem();
                lifeSignSaveDataItem.TZXM = "1";
                lifeSignSaveDataItem.Data = sdata;
                lifeSignSaveDataItem.YCBZ = ycbz;
                lifeSignSaveDataItem.lifeSignSaveDataTermList = new ArrayList<>();
                LifeSignSaveDataTerm lifeSignSaveDataTerm1 = new LifeSignSaveDataTerm();
                lifeSignSaveDataTerm1.ID = String.valueOf(id);
                lifeSignSaveDataTerm1.Data = data;
                lifeSignSaveDataTerm1.Name = "Twfc";
                lifeSignSaveDataItem.lifeSignSaveDataTermList.add(lifeSignSaveDataTerm1);
                LifeSignSaveDataTerm lifeSignSaveDataTerm2 = new LifeSignSaveDataTerm();
                lifeSignSaveDataTerm2.ID = "1";
                lifeSignSaveDataTerm2.Data = time;
                lifeSignSaveDataTerm2.Name = "Term";
                lifeSignSaveDataItem.lifeSignSaveDataTermList.add(lifeSignSaveDataTerm2);
                lifeSignSaveDataItemList.add(lifeSignSaveDataItem);
            } else if (viewType.equals("502")) {
                lifeSignSaveDataItem = new LifeSignSaveDataItem();
                lifeSignSaveDataItem.TZXM = "502";
                lifeSignSaveDataItem.Data = sdata;
                lifeSignSaveDataItem.YCBZ = ycbz;
                lifeSignSaveDataItem.lifeSignSaveDataTermList = new ArrayList<>();
                LifeSignSaveDataTerm lifeSignSaveDataTerm1 = new LifeSignSaveDataTerm();
                lifeSignSaveDataTerm1.ID = String.valueOf(id);
                lifeSignSaveDataTerm1.Data = data;
                lifeSignSaveDataTerm1.Name = "Ttfc";
                lifeSignSaveDataItem.lifeSignSaveDataTermList.add(lifeSignSaveDataTerm1);
                lifeSignSaveDataItemList.add(lifeSignSaveDataItem);
            }
        }
        return lifeSignSaveDataItem;
    }

    void buildButton(final String zyh, final String jgid) {
//        but = new ImageButton(context);
        but = new TextView(context);
        but.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

//        but.setImageResource(R.drawable.btn_replay);
//        but.setBackgroundDrawable(null);
//        ViewCompat.setBackground(but, null);
        but.setText("复测");
        but.setTextColor(Color.WHITE);
        but.setPadding(20,10,20,10);
        but.setBackgroundResource(R.drawable.selector_classic_bg_click_o_colored);
        but.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //
                View viewDialog = null;
                if (viewType.equals("1")) {
                     viewDialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_history_double_check, null);
                    listView_Measure = (ListView) viewDialog.findViewById(R.id.id_lv_2);
                    EmptyViewHelper.setEmptyView(listView_Measure,"listView_Measure");
                    //SwipeRefreshLayoutHelper.setSwipeEnable(id_swipe_refresh_layout,listView_Measure);
                    adapter_measure = new LifeHistoryProcessAdapter(context);
                } else {
                     viewDialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_history, null);
                }

                listView_History = (ListView) viewDialog.findViewById(R.id.id_lv);
                EmptyViewHelper.setEmptyView(listView_History,"listView_History");
               // SwipeRefreshLayoutHelper.setSwipeEnable(id_swipe_refresh_layout,listView_History);
                adapter_history = new LifeHistoryAdapter(context);

                builder.setView(viewDialog);
                LinearLayout id_ll_container = viewDialog.findViewById(R.id.id_ll_container);
                id_ll_container.setBackgroundResource(R.drawable.inset_dialog_bg);
                //
                BsoftActionBar actionBar = (BsoftActionBar) viewDialog
                        .findViewById(R.id.actionbar);
                actionBar.setBackAction(new BsoftActionBar.Action() {
                    @Override
                    public void performAction(View view) {
                        if (!isSave) {
                            adapter_history.clearChose();
                            if (viewType.equals("1")) {
                                adapter_measure.clearChose();
                            }
                        }
                        mAlertDialog.dismiss();
                    }
                    @Override
                    public String getText() {
                        return context.getString(R.string.menu_back);
                    }
                    @Override
                    public int getDrawable() {
                        return R.drawable.ic_arrow_back_black_24dp;
                    }
                });
                actionBar.addAction(new BsoftActionBar.Action() {
                    @Override
                    public String getText() {
                        return "保存";
                    }
                    @Override
                    public void performAction(View view) {
                        id = adapter_history.getChoseId();
                           /*
                           升级编号【56010034】============================================= start
                            复测不选择历史不能保存
                           ================= Classichu 2017/11/20 17:12

                           */
                        if (viewType.equals("1")) {
                            //add by louis
                            if (id == -1 && adapter_history.getCount() > 0) {
                                BSToast.showToast(view.getContext(), "必须选择历史体温！", BSToast.LENGTH_SHORT);
                                return;
                            }
                            data = adapter_measure.getChoseValue();
                            //add by louis
                            if (data == null && adapter_measure.getCount() > 0) {
                                BSToast.showToast(view.getContext(), "必须选择降温措施！", BSToast.LENGTH_SHORT);
                                return;
                            }
                        } else {
                            //add by louis
                            if (id == -1 && adapter_history.getCount() > 0) {

                                BSToast.showToast(view.getContext(), "必须选择疼痛历史评分！", BSToast.LENGTH_SHORT);
                                return;
                            }
                            //
                            data = adapter_history.getChoseVal();
                        }
                            /* =============================================================== end */
                        isSave = true;
                        //
                        mAlertDialog.dismiss();
                    }

                    @Override
                    public int getDrawable() {
                        return R.drawable.ic_done_black_24dp;
                    }
                });
                mAlertDialog = builder.create();
                mAlertDialog.show();


                new AsyncTask<String, Void, Response<LifeSignDoubleCheckHistoryData>>() {

                    @Override
                    protected void onPreExecute() {
                        //
                    }

                    @Override
                    protected Response<LifeSignDoubleCheckHistoryData> doInBackground(String... arg0) {
                        return LifeSignApi.getInstance(context)
                                .GetLifeHistoryItem(viewType, zyh, jgid, Constant.sysType);
                    }

                    @Override
                    protected void onPostExecute(Response<LifeSignDoubleCheckHistoryData> result) {
                        if (null != result) {
                            if (result.ReType == 100) {
                                new AgainLoginUtil(context, application).showLoginDialog();
                                return;
                            } else if (result.ReType == 0) {
                                listView_History.setAdapter(adapter_history);
                                listView_History.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                        adapter_history.changeStatue(adapter_history.getItem(arg2).CJH);
                                    }
                                });
                                LifeSignDoubleCheckHistoryData lifeSignDoubleCheckHistoryData = result.Data;
                                if (null != lifeSignDoubleCheckHistoryData.lifeSignDoubleCheckHistoryDataItemList
                                        && lifeSignDoubleCheckHistoryData.lifeSignDoubleCheckHistoryDataItemList.size() > 0) {
                                    adapter_history.addData((ArrayList<LifeSignDoubleCheckHistoryDataItem>) lifeSignDoubleCheckHistoryData.lifeSignDoubleCheckHistoryDataItemList);
                                }
                                if (viewType.equals("1")) {
                                    listView_Measure.setAdapter(adapter_measure);
                                    listView_Measure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                            adapter_measure.changeStatue(adapter_measure.getItem(arg2).DMSB);
                                        }
                                    });
                                    if (null != lifeSignDoubleCheckHistoryData.lifeSignDoubleCheckCoolingMeasureList
                                            && lifeSignDoubleCheckHistoryData.lifeSignDoubleCheckCoolingMeasureList.size() > 0) {
                                        adapter_measure.addData((ArrayList<LifeSignDoubleCheckCoolingMeasure>) lifeSignDoubleCheckHistoryData.lifeSignDoubleCheckCoolingMeasureList);
                                    }

                                }
                            }
                        }
                       // emptyProgress.setVisibility(View.GONE);
                    }

                }.execute();


            }
        });
    }

    //体温小键盘支持 start03
    void buildEditText() {
        editText = ViewBuildHelper.buildEditTextAutoWrap(context, null);
        if (viewType.equals("1")) {
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // ##DoubleCheckView.this.setSelected(hasFocus);
                }
            });
          /*  editText.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    //## DoubleCheckView.this.setSelected(hasFocus);
                    if (!hasFocus)
                        keyboard.hideKeyboard();
                    else {
                        hideSoftInputMethod(editText);
                    }
                }
            });*/
           /* editText.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    keyboard.showKeyboard(editText);
                    return false;
                }
            });*/
        } else {
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // ##DoubleCheckView.this.setSelected(hasFocus);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //体温小键盘支持 end03
    void buildTextView() {
        textView = ViewBuildHelper.buildTextView(context, null);
        TextViewCompat.setTextAppearance(textView, R.style.ClassicTextAppearanceSecondaryColored);
        if (viewType.equals("1")) {
            textView.setText("℃");
        } else if (viewType.equals("502")) {
            textView.setText("分");
        }
    }

}
