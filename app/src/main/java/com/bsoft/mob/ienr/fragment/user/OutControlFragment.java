package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.OutControllerHistoryActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.api.OutControlApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.outcontrol.OutControl;
import com.bsoft.mob.ienr.model.outcontrol.OutControlSaveData;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.KeyBoardTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 外出管理页 Created by hy on 14-3-21.
 */
public class OutControlFragment extends BaseUserFragment {


    private TextView mPaitentTxt;
    private TextView mOutTimeTxt;
    private TextView mBackTimeTxt;
    private TextView mOutTitleTxt;
    private TextView mBackTitleTxt;
    private EditText mEdit;

    private Spinner mSpinner;

    private OutControl out = null;

    private View content;

    // View outPView;
    // View backPView;

    private ImageView id_iv_for_bar_image;
    private ImageView id_iv_for_bar_image_copy;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_out_control;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mPaitentTxt = (TextView) root.findViewById(R.id.out_control_name_txt);

        mEdit = (EditText) root.findViewById(R.id.out_control_record_edit);
        //==add by louisgeek at 2017-4-17 17:05:39 start
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 先去掉监听器，否则会出现栈溢出
                mEdit.removeTextChangedListener(this);
                int MAX_CHAR_COUNT = 255;//最大字符数
                int editStart = mEdit.getSelectionStart();
                int editEnd = mEdit.getSelectionEnd();
                while (calculateCharLength(editable.toString()) > MAX_CHAR_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                    //mEdit.setError("过长");
                    editable.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                mEdit.setSelection(editStart);
                // 恢复监听器
                mEdit.addTextChangedListener(this);
            }

            //区分中英文,计算字符长度
            private long calculateCharLength(CharSequence c) {
                int len = 0;
                for (int i = 0; i < c.length(); i++) {
                    int charValue = (int) c.charAt(i);
                    if (charValue > 0 && charValue < 127) {
                        len += 1;
                    } else {
                        len += 2;
                    }
                }
                return Math.round(len);
            }
        });
        //==add by louisgeek at 2017-4-17 17:05:39 end
        SpinnerLayout startTimeLayout = (SpinnerLayout) root.findViewById(R.id.id_spinner_layout);
        mSpinner = startTimeLayout.getSpinner();

        content = root.findViewById(R.id.out_control_content_ll);

        mOutTitleTxt = (TextView) root
                .findViewById(R.id.id_tv_for_bar_image);
        mOutTitleTxt.setText(R.string.out_control_plan_out_time);
        mBackTitleTxt = (TextView) root
                .findViewById(R.id.id_tv_for_bar_image_copy);
        mBackTitleTxt.setText(R.string.out_control_plan_out_time);

        mOutTimeTxt = (TextView) root
                .findViewById(R.id.id_tv_2_for_bar_image);
        mBackTimeTxt = (TextView) root
                .findViewById(R.id.id_tv_2_for_bar_image_copy);

        id_iv_for_bar_image = root.findViewById(R.id.id_iv_for_bar_image);
        id_iv_for_bar_image_copy = root.findViewById(R.id.id_iv_for_bar_image_copy);


        initActionBar();
        initSpinner();
        initName();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        GetStateTask task = new GetStateTask();
        tasks.add(task);
        task.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBroadCast();

    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_view,
                R.drawable.menu_fresh, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_view, R.string.comm_menu_view},
                {R.drawable.menu_fresh, R.string.comm_menu_refresh},
                {R.drawable.menu_save, R.string.comm_menu_save}};

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
       /* for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
        for (int[] itemDrawableRes : itemStringDrawables) {
            int itemDrawableResid = itemDrawableRes[0];
            int textResId = itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid, text) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        return floatMenuItemList;
    }


    /**
     * 响应RayMenu item点击
     */
    private void onMenuItemClick(int drawableRes) {

        if (drawableRes == R.drawable.menu_view) {// 历史

            startActivity(new Intent(getActivity(), OutControllerHistoryActivity.class));
        } else if (drawableRes == R.drawable.menu_fresh) {

            toRefreshData();
        } else if (drawableRes == R.drawable.menu_save) {
            saveData();

        }

    }

    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                            + mAppApplication.sickPersonVo.BRXM);
                    initName();
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    }

                }
            }
        };
    }

    private void setTitle() {

        actionBar.setTitle(isOut ? "外出登记" : "回床登记");
    }

    private void initSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.out_with_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        // mSpinner.setOnItemSelectedListener(onOSListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int id = v.getId();

            String str = null;
            if (id == R.id.id_iv_for_bar_image) {
                if (!isOut){
                    showMsgAndVoiceAndVibrator("不可修改外出时间");
                    return;
                }
                str = mOutTimeTxt.getText().toString();
            } else if (id == R.id.id_iv_for_bar_image_copy) {
                str = mBackTimeTxt.getText().toString();
            }
            showDateTimePickerCompat(str, id);
        }
    };

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);
        if (viewId == R.id.id_iv_for_bar_image_copy) {
            String outStr = mOutTimeTxt.getText().toString();
            if (DateTimeFactory.getInstance().dateTimeBefore(dateTime, outStr)) {
                showMsgAndVoiceAndVibrator("回床时间先于外出时间,请重新选择");
                return;
            }
        }
        initTimeTxt(dateTime, viewId);
    }


    private void initTimeTxt(String dateTimeStr, int viewId) {
        String timeStr = dateTimeStr;
        if (viewId == R.id.id_iv_for_bar_image_copy) {
            mBackTimeTxt.setText(timeStr);
        } else if (viewId == R.id.id_iv_for_bar_image) {
            mOutTimeTxt.setText(timeStr);
        }
    }

    private void initName() {
        if (mAppApplication.sickPersonVo != null) {
            String name = mAppApplication.sickPersonVo.BRXM;
            name = String.format(getString(R.string.out_control_name), name);
            mPaitentTxt.setText(name);
        }
    }



    private void performSaveTask() {
        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute();
    }

    private void initActionBar() {

        // actionBar.setTitle("外出登录");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                saveData();
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    private void saveData() {

        if (content.getVisibility() == View.VISIBLE) {
            KeyBoardTool.hideKeyboard(content);
            performSaveTask();
        } else {
            toastInfo("请求失败：未填写数据");
        }
    }

    private void setTimeTxt(String outStr, String backStr) {

        if (EmptyTool.isBlank(outStr)) {
            String datetime = DateTimeHelper.getServer_yyyyMMddHHmm00();
            outStr = datetime;
        }
        if (EmptyTool.isBlank(backStr)) {
            //
            String dateTime = DateTimeHelper.getServer_yyyyMMddHHmm00();
            dateTime = DateTimeHelper.dateTimeAddedDays(dateTime, 1);
            backStr = dateTime;
        }

        mOutTimeTxt.setText(outStr);
        mBackTimeTxt.setText(backStr);
        // mOutTimeTxt.setOnClickListener(onClickListener);
        id_iv_for_bar_image_copy.setOnClickListener(onClickListener);

    }


    private boolean isOut;
    private void setEdit() {
        mEdit.setVisibility(isOut ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取病人当前状态
     *
     * @author hy
     */
    class GetStateTask extends AsyncTask<Void, Integer, Response<List<OutControl>>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
            out = null;
        }

        @Override
        protected Response<List<OutControl>> doInBackground(Void... params) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;

            OutControlApi api = OutControlApi.getInstance(getActivity());
            return api.GetPatientStatus(zyh, brbq, jgid, sysType);

        }

        @Override
        protected void onPostExecute(Response<List<OutControl>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null == result) {
                showMsgAndVoiceAndVibrator("请求失败");
                showContentView(false);
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {

                showContentView(true);
                @SuppressWarnings("unchecked")
                ArrayList<OutControl> list = (ArrayList<OutControl>) result.Data;

                 isOut = !(list != null && list.size() > 0 && list
                        .get(0) != null);
                // 设置标题
                setTitle();
                setEdit();
                setSpinnerClickable();

                // 设置时项
                if (!isOut) {
                    out = list.get(0);
                    setTimeTxt(getOutTime(out), getBackTime(out));
                    try {
                        setSpinnerSelectItem(Integer.valueOf(out.PTRY));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    setTimeTxt(null, null);
                }
                id_iv_for_bar_image.setOnClickListener(onClickListener);
                setTimeTitile();

            } else {
                showMsgAndVoice(result.Msg);
            }
        }

        private String getBackTime(OutControl out) {

            if (out == null || EmptyTool.isBlank(out.YJHCSJ)) {
                return null;
            }
            return out.YJHCSJ;
        }

        private String getOutTime(OutControl out) {

            if (out == null || EmptyTool.isBlank(out.WCDJSJ)) {
                return null;
            }
            return out.WCDJSJ;
        }
    }

    class SaveTask extends AsyncTask<Void, Integer, Response<String>> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(Void... params) {

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String hcdjsj = mBackTimeTxt.getText().toString();
            String hcdjhs = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            OutControlApi api = OutControlApi.getInstance(getActivity());
            if (out != null) {// 回床登记

                String jlxh = out.JLXH;
                return api.RegisterBackToBed(jlxh, hcdjsj, hcdjhs, jgid,
                        sysType);
            } else {// 外出登记
                String data = "";

                OutControlSaveData outControlSaveData = new OutControlSaveData();
                outControlSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                outControlSaveData.BRBQ = mAppApplication.getAreaId();
                outControlSaveData.WCSJ = mOutTimeTxt.getText().toString();
                outControlSaveData.YJHCSJ = mBackTimeTxt.getText().toString();
                outControlSaveData.WCDJHS = mAppApplication.user.YHID;
                outControlSaveData.PZYS = null;
                outControlSaveData.PTRY = mSpinner.getSelectedItemPosition();
                outControlSaveData.JGID = jgid;
                //2017-4-17 15:46:34 外出原因
                outControlSaveData.WCYY = mEdit.getText().toString();
                //
                outControlSaveData.sysType = sysType;
//				String wcyy = mEdit.getText().toString();
                try {
                    data = JsonUtil.toJson(outControlSaveData);
                    //  Log.i(Constant.TAG_COMM, "doInBackground: data:"+data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return api.RegisterOutPatient(data);
            }

        }

        @Override
        protected void onPostExecute(Response<String> result) {

            hideLoadingDialog();
            tasks.remove(this);
            if (null == result) {
                showMsgAndVoiceAndVibrator("请求失败");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        performSaveTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                toastInfo("登记成功");
                toRefreshData();
            } else {
                showMsgAndVoice(result.Msg);
            }
        }

    }

    private void toastInfo(String msg) {
        // Crouton.showText(getActivity(), msg, Style.INFO, R.id.out_control_content_ll);
        showSnack(msg);
    }

    private void showContentView(boolean show) {
        content.setVisibility(show ? View.VISIBLE : View.GONE);
        // mSaveBtn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setSpinnerClickable() {
        mSpinner.setClickable(isOut ? true : false);
    }

    public void setSpinnerSelectItem(int position) {
        mSpinner.setSelection(position);
    }

    public void setTimeTitile() {

        mOutTitleTxt
                .setText(isOut ? getString(R.string.out_control_plan_out_time)
                        : getString(R.string.out_control_out_time));
        mBackTitleTxt
                .setText(isOut ? getString(R.string.out_control_plan_back_time)
                        : getString(R.string.out_control_back_time));
    }


}
