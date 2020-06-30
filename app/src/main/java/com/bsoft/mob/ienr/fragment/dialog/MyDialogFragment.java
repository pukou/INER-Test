package com.bsoft.mob.ienr.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.DataMapWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bsoft.mob.ienr.helper.ViewBuildHelper.buildDialogTitleTextView;

/**
 * dialog framgent ,各user fragment消息传递依赖 activity
 *
 * @author hy
 */
public class MyDialogFragment extends DialogFragment {

    private Bundle customArguments;

    private void setCustomArguments(Bundle customArguments) {
        this.customArguments = customArguments;
    }

    private Bundle getCustomArguments() {
        return customArguments;
    }

    private int viewId = -1;
    // dialog单实例 start01
    private static MyDialogFragment frag;

    public static final byte SHOW_DATE_PICKER = 1;
    // dialog单实例 end01
    public static final byte SHOW_LOADING = SHOW_DATE_PICKER + 1;
    public static final byte SHOW_DATE_TIME_PICKER = SHOW_LOADING + 1;
    public static final byte SHOW_TIME_PICKER = SHOW_DATE_TIME_PICKER + 1;
    public static final byte SHOW_INPUT_DIALOG = SHOW_TIME_PICKER + 1;
    public static final byte SHOW_INFO_DIALOG = SHOW_INPUT_DIALOG + 1;
    public static final byte SHOW_CONFIRM_DIALOG = SHOW_INFO_DIALOG + 1;
    public static final byte SHOW_LIST_DIALOG = SHOW_CONFIRM_DIALOG + 1;

    /**
     * @param @param  msg 消息内容
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newLoadInstance
     * @Description: 消息提示对话框（如：正在加载）
     */
    public static MyDialogFragment newLoadInstance(String msg) {
        // dialog单实例 start02
        if (frag == null) {
            frag = new MyDialogFragment();
        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_LOADING) {
                frag.getCustomArguments().clear();
            }
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_LOADING);
        args.putString("msg", msg);
        //!!!! fragment被commit后，再调用setArguments方法会报异常
        //frag.setArguments(args);
        frag.setCustomArguments(args);
        // dialog单实例 end02
        return frag;
    }

    /**
     * @param @param  y int 年
     * @param @param  month int 月 1`12
     * @param @param  d int 日
     * @param @param  id int 控件id（日期内容设置到改控件上）
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newDateInstance
     * @Description: 日期选择对话框
     */
    public static MyDialogFragment newDateInstance(int y, int month, int d, int id,long minDate,long maxDate) {
        // dialog单实例 start03
        if (frag == null) {
            frag = new MyDialogFragment();

        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_DATE_PICKER) {
                frag.getCustomArguments().clear();
            }
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_DATE_PICKER);
        args.putInt("y", y);
        args.putInt("m", month);
        args.putInt("d", d);
        args.putInt("id", id);
        args.putLong("minDate", minDate);
        args.putLong("maxDate", maxDate);
        frag.setCustomArguments(args);
        // dialog单实例 end03
        return frag;
    }

    /**
     * @param @param  y int 年
     * @param @param  month int 月  1~12
     * @param @param  d int 日
     * @param @param  hour int 小时
     * @param @param  minute int 分钟
     * @param @param  id 控件id（日期时间内容设置到哪个控件上）
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newDateTimeInstance
     * @Description: 日期时间选择对话框
     */
    public static MyDialogFragment newDateTimeInstance(int y, int month, int d,
                                                       int hour, int minute, int id,long minDate,long maxDate) {
        // dialog单实例 start04
        if (frag == null) {
            frag = new MyDialogFragment();
        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_DATE_TIME_PICKER) {
                frag.getCustomArguments().clear();
            }
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_DATE_TIME_PICKER);
        args.putInt("y", y);
        args.putInt("m", month);
        args.putInt("d", d);
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        args.putInt("id", id);
        args.putLong("minDate", minDate);
        args.putLong("maxDate", maxDate);
        frag.setCustomArguments(args);
        // dialog单实例 end04
        return frag;
    }

    /**
     * @param @param  hour int 小时
     * @param @param  minute int 分钟
     * @param @param  id int 控件id（时间内容设置到该控件上）
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newTimeInstance
     * @Description: 时间选择对话框
     */
    public static MyDialogFragment newTimeInstance(int hour, int minute, int id) {
        // dialog单实例 start05
        if (frag == null) {
            frag = new MyDialogFragment();

        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_TIME_PICKER) {
                frag.getCustomArguments().clear();
            }
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_TIME_PICKER);
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        args.putInt("id", id);
        frag.setCustomArguments(args);
        // dialog单实例 end05
        return frag;
    }

    /**
     * @param @param  id
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newInputInstance
     * @Description: 显示输入对话框
     */
    public static MyDialogFragment newInputInstance(String title, int id) {
        if (frag == null) {
            frag = new MyDialogFragment();
        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_INPUT_DIALOG) {
                frag.getCustomArguments().clear();
            }
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_INPUT_DIALOG);
        args.putString("title", title);
        args.putInt("id", id);
        frag.setCustomArguments(args);
        return frag;
    }

    public static MyDialogFragment newListInstance(String title, int id, Map<String, String> stringMap, boolean cancelable) {
        if (frag == null) {
            frag = new MyDialogFragment();
        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_LIST_DIALOG) {
                frag.getCustomArguments().clear();
            }
        }
        frag.setCancelable(cancelable);
        Bundle args = new Bundle();
        args.putByte("type", SHOW_LIST_DIALOG);
        args.putString("title", title);
        args.putInt("id", id);
        args.putSerializable("DataMapWrapper", new DataMapWrapper(stringMap));
        frag.setCustomArguments(args);
        return frag;
    }

    /**
     * @param @param  info 消息内容
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newInfoInstance
     * @Description: 类似alert的消息对话框
     */
    public static MyDialogFragment newInfoInstance(String info) {
        if (frag == null) {
            frag = new MyDialogFragment();
        } else {
            frag.getCustomArguments().clear();
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_INFO_DIALOG);
        args.putString("info", info);
        frag.setCustomArguments(args);
        return frag;
    }

    /**
     * @param @param  info 消息内容
     * @param @param  action 做什么动作
     * @param @return
     * @return MyDialogFragment
     * @throws
     * @Title: newConfirmInstance
     * @Description: 类似messagebox的对话框，用户可点击取消和确定
     */
    public static MyDialogFragment newConfirmInstance(String info, String action) {
        if (frag == null) {
            frag = new MyDialogFragment();
        } else {
            if (frag.getCustomArguments().getByte("type") != SHOW_CONFIRM_DIALOG) {
                frag.getCustomArguments().clear();
            }
        }
        Bundle args = new Bundle();
        args.putByte("type", SHOW_CONFIRM_DIALOG);
        args.putString("info", info);
        args.putString("action", action);
        frag.setCustomArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int type = getCustomArguments().getByte("type");

        switch (type) {
            case SHOW_LOADING:
                String msg = getCustomArguments().getString("msg");
                ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage(msg);
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case SHOW_TIME_PICKER:
                int h = getCustomArguments().getInt("hour");
                int minute = getCustomArguments().getInt("minute");
                viewId = getCustomArguments().getInt("id");

                AlertDialog.Builder builder__ = new AlertDialog.Builder(getActivity());
                View content__ = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_dialog_date_time, null, false);
                final DatePicker datePicker__ = (DatePicker) content__
                        .findViewById(R.id.datePicker1);
                datePicker__.setVisibility(View.GONE);
                final TimePicker timePicker__ = (TimePicker) content__
                        .findViewById(R.id.timePicker1);
                boolean is24HourFormat__ = DateFormat.is24HourFormat(getActivity());
                timePicker__.setIs24HourView(is24HourFormat__);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker__.setHour(h);
                    timePicker__.setMinute(minute);
                } else {
                    timePicker__.setCurrentHour(h);
                    timePicker__.setCurrentMinute(minute);
                }
                builder__.setView(content__);
                OnClickListener listener__ = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            onTimeSet(timePicker__.getHour(),
                                    timePicker__.getMinute());
                        } else {
                            onTimeSet(timePicker__.getCurrentHour(),
                                    timePicker__.getCurrentMinute());
                        }
                    }
                };
                builder__.setCustomTitle(buildDialogTitleTextView(getContext(), "选择时间"))
                        .setPositiveButton(android.R.string.ok, listener__)
                        .setNegativeButton(android.R.string.cancel, null);
                return builder__.create();
            case SHOW_DATE_PICKER:

                long minDate = getCustomArguments().getLong("minDate");
                long maxDate = getCustomArguments().getLong("maxDate");
                int d = getCustomArguments().getInt("d");
                int m = getCustomArguments().getInt("m");
                int y = getCustomArguments().getInt("y");
                viewId = getCustomArguments().getInt("id");

                AlertDialog.Builder builder_ = new AlertDialog.Builder(getActivity());
                View content_ = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_dialog_date_time, null, false);
                final DatePicker datePicker_ = (DatePicker) content_
                        .findViewById(R.id.datePicker1);
                int monthOfYear = m < 1 ? 0 : m - 1;//monthOfYear：  0~11
                datePicker_.init(y, monthOfYear, d, null);
                if (maxDate>=0) {
                    datePicker_.setMaxDate(maxDate);
                }
                if (minDate>=0) {
                    datePicker_.setMinDate(minDate);
                }
                final TimePicker timePicker_ = (TimePicker) content_
                        .findViewById(R.id.timePicker1);
                timePicker_.setVisibility(View.GONE);
                builder_.setView(content_);
                OnClickListener listener_ = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int monthOfYear = datePicker_.getMonth();//0~11
                        int month = monthOfYear > 11 ? 12 : monthOfYear + 1;//1~12
                        onDateSet(datePicker_.getYear(), month,
                                datePicker_.getDayOfMonth());
                    }
                };
                builder_.setCustomTitle(buildDialogTitleTextView(getContext(), "选择日期"))
                        .setPositiveButton(android.R.string.ok, listener_)
                        .setNegativeButton(android.R.string.cancel, null);
                return builder_.create();
            case SHOW_DATE_TIME_PICKER:
                minDate = getCustomArguments().getLong("minDate");
                maxDate = getCustomArguments().getLong("maxDate");
                d = getCustomArguments().getInt("d");
                m = getCustomArguments().getInt("m");
                y = getCustomArguments().getInt("y");
                h = getCustomArguments().getInt("hour");
                minute = getCustomArguments().getInt("minute");
                viewId = getCustomArguments().getInt("id");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View content = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_dialog_date_time, null, false);
                final DatePicker datePicker = (DatePicker) content
                        .findViewById(R.id.datePicker1);
                int monthOfYear_ = m < 1 ? 0 : m - 1;//monthOfYear：  0~11
                datePicker.init(y, monthOfYear_, d, null);
                if (maxDate>=0) {
                    datePicker.setMaxDate(maxDate);
                }
                if (minDate>=0) {
                    datePicker.setMinDate(minDate);
                }
                final TimePicker timePicker = (TimePicker) content
                        .findViewById(R.id.timePicker1);
                boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());
                timePicker.setIs24HourView(is24HourFormat);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(h);
                    timePicker.setMinute(minute);
                } else {
                    timePicker.setCurrentHour(h);
                    timePicker.setCurrentMinute(minute);
                }
                builder.setView(content);
                OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int monthOfYear = datePicker.getMonth();//0~11
                            int month = monthOfYear > 11 ? 12 : monthOfYear + 1;//1~12
                            onDateTimeSet(datePicker.getYear(), month,
                                    datePicker.getDayOfMonth(),
                                    timePicker.getHour(),
                                    timePicker.getMinute());
                        } else {
                            int monthOfYear = datePicker.getMonth();//0~11
                            int month = monthOfYear > 11 ? 12 : monthOfYear + 1;//1~12
                            onDateTimeSet(datePicker.getYear(), month,
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());
                        }
                    }
                };
                builder.setCustomTitle(buildDialogTitleTextView(getContext(), "选择日期时间"))
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            case SHOW_INPUT_DIALOG:

                LinearLayout linearLayout = LayoutParamsHelper.buildLinearMatchWrap_V(getContext());
                linearLayout.setBackgroundResource(R.drawable.inset_dialog_bg);
                final EditText ed = ViewBuildHelper.buildEditTextMatchWrap(getContext(), null);
                linearLayout.addView(ed);
                String title = getCustomArguments().getString("title");
                viewId = getCustomArguments().getInt("id");
                //
                View titleTextView = ViewBuildHelper.buildDialogTitleTextView(getContext(), title);
                return new AlertDialog.Builder(getActivity())
//                        .setTitle(title)
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(linearLayout)
                        .setCustomTitle(titleTextView)
                        .setPositiveButton(getString(R.string.project_operate_ok), new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FragmentActivity activity = getActivity();
                                if (activity instanceof BaseActivity) {
                                    ((BaseActivity) activity).onInputCompleteed(ed
                                            .getText().toString().trim(), viewId);
                                }
                            }
                        }).setNegativeButton(getString(R.string.project_operate_cancel), null).create();
            case SHOW_INFO_DIALOG:
                String info = getCustomArguments().getString("info");
                TextView infoTextView = ViewBuildHelper.buildDialogMsgTextView(getContext(), info);
                View titleTextView2 = ViewBuildHelper.buildDialogTitleTextView(getContext(), getString(R.string.project_tips));
                return new AlertDialog.Builder(getActivity())
                        //.setTitle(getString(R.string.project_tips))
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(infoTextView)
                        .setCustomTitle(titleTextView2)
                        .setNegativeButton("ok", null).create();
            case SHOW_CONFIRM_DIALOG:
                final TextView conf_msg = new TextView(getActivity());
                String infoMsg = getCustomArguments().getString("info");
                conf_msg.setMinHeight(100);
                conf_msg.setTextSize(16);
                conf_msg.setText(infoMsg);
                final String action = getCustomArguments().getString("action");

                View titleTextView3 = ViewBuildHelper.buildDialogTitleTextView(getContext(), getString(R.string.project_tips));
                return new AlertDialog.Builder(getActivity())
                        //.setTitle(getString(R.string.project_tips))
                        // .setIcon(android.R.drawable.ic_dialog_info)
//                        .setView(conf_msg)
                        .setMessage(infoMsg)
                        .setCustomTitle(titleTextView3)
                        .setPositiveButton(getString(R.string.project_operate_ok), new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onConfirmSet(action);
                            }
                        }).setNegativeButton(getString(R.string.project_operate_cancel), null).create();

            case SHOW_LIST_DIALOG:
                final ListView config_list = new ListView(getActivity());
                final DataMapWrapper dataMapWrapper = (DataMapWrapper) getCustomArguments().getSerializable("DataMapWrapper");
                final String titleStr = (String) getCustomArguments().getSerializable("title");
                final List<String> keyList = new ArrayList<>();
                final List<String> valueList = new ArrayList<>();
                for (String key : dataMapWrapper.getMap().keySet()) {
                    String value = dataMapWrapper.getMap().get(key);
                    keyList.add(key);
                    valueList.add(value);
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_list_text_one_primary,
                        R.id.name, valueList);
                config_list.setAdapter(arrayAdapter);
                config_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //hide
                        frag.dismiss();
                        //
                        FragmentActivity activity = getActivity();
                        if (activity instanceof BaseActivity) {
                            ((BaseActivity) activity).onListSelected(keyList.get(position), valueList.get(position));
                        }

                    }
                });
                View titleTextView4 = ViewBuildHelper.buildDialogTitleTextView(getContext(), titleStr);
                return new AlertDialog.Builder(getActivity())
                        //.setTitle(titleStr)
                        // .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(config_list)
                        .setCustomTitle(titleTextView4)
                        .create();
            default:
        }

        return super.onCreateDialog(savedInstanceState);

    }

    public void onDateSet(int year, int month,
                          int dayOfMonth) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).onDateSet(year, month,
                    dayOfMonth, viewId);
        }
    }

    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).onDateTimeSet(year, month,
                    dayOfMonth, hourOfDay, minute, viewId);
        }
    }


    public void onTimeSet(int hourOfDay, int minute) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).onTimeSet(hourOfDay, minute, viewId);
        }
    }

    public void onConfirmSet(String action) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).onConfirmSet(action);
        }
    }
}