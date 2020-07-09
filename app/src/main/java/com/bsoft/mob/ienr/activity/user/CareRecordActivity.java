package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.activity.adapter.NouseRecordLastDataAdapter;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.dynamicui.nurserecord.NurseRecordViewFactory;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PlugIn;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PouponEditView;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PouponEditViewForMultiselect;
import com.bsoft.mob.ienr.dynamicui.nurserecord.PouponItem;
import com.bsoft.mob.ienr.dynamicui.nurserecord.UIView;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurserecord.LastDataBean;
import com.bsoft.mob.ienr.model.nurserecord.SaveOrUpdateItem;
import com.bsoft.mob.ienr.model.nurserecord.SaveOrUpdateRequest;
import com.bsoft.mob.ienr.model.nurserecord.SignatureDataRequest;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 护理记录 Created by hy on 14-3-24.
 */
public class CareRecordActivity extends BaseBarcodeActivity implements
        NurseRecordViewFactory.OnDateTimeClickListener {

    /**
     * 请求获取模板结构树
     */
    private static final int RQ_GET_STRUCT_TREE = 0;

    /***
     * 请求查看记录
     */
    private static final int RQ_VIEW_HISTORY = RQ_GET_STRUCT_TREE + 1;

    /**
     * 请求获取助手
     */
    private static final int RQ_GET_HELP = RQ_VIEW_HISTORY + 1;

    /**
     * 请求获取引用
     */
    private static final int RQ_GET_REFER = RQ_GET_HELP + 1;

    /**
     * 请求获取签名用户ID
     */
    private static final int RQ_GET_USERID = RQ_GET_REFER + 1;

    // private ViewPager mViewPager;
    // private RadioGroup mRadioGroup;

    private NestedScrollView mScrollView;

    private View timePageView;

    private TextView mTimeView;

    private Request request;

    private CheckBox cb;


    @Override
    protected void toRefreshData() {
        getData();
    }

    private void getData() {
        String timeStr = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(timeStr);

        String jgbh = getIntent().getStringExtra("jgbh");

        request = new Request();
        request.jgbh = jgbh;
        request.type = GetDateTask.QRT_BY_JGBH;
        ationGetDateTask(GetDateTask.QRT_BY_JGBH, jgbh);
    }


    private void taggleMenu(boolean canUpdate, boolean expand) {
        int[] itemDrawables = {};
        int[][] itemStringDrawables = {};
        if (canUpdate) {
     /*       itemDrawables = ArrayUtils.addAll(itemDrawables,
                    R.drawable.menu_fresh, R.drawable.menu_help,
                    R.drawable.menu_refer, R.drawable.menu_create,
                    R.drawable.menu_view);*/
            itemDrawables = ArrayUtils.addAll(itemDrawables,
                    R.drawable.menu_fresh, R.drawable.menu_help,
                    R.drawable.menu_refer, R.drawable.menu_create);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_fresh, R.string.comm_menu_refresh},
                    new int[]{R.drawable.menu_help, R.string.comm_menu_help},
                    new int[]{R.drawable.menu_refer, R.string.comm_menu_refer},
                    new int[]{R.drawable.menu_create, R.string.comm_menu_add});
        } else {
            itemDrawables = ArrayUtils.addAll(itemDrawables,
                    R.drawable.menu_create);
           /* itemDrawables = ArrayUtils.addAll(itemDrawables,
                    R.drawable.menu_create, R.drawable.menu_view);*/
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_create, R.string.comm_menu_add});
        }

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
     /*   for (int itemDrawableResid : itemDrawables) {
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
        updateFloatMenuItems(floatMenuItemList);

    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
//        int[] itemDrawables = {R.drawable.menu_create, R.drawable.menu_view};
        int[] itemDrawables = {R.drawable.menu_create};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_create, R.string.comm_menu_add}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
    /*    for (int itemDrawableResid : itemDrawables) {
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


    protected void onMenuItemClick(int drawableId) {

        if (drawableId == R.drawable.menu_create) {// 新增护理记录
            // 返回模板界面
            finish();
        }/* else if (drawableId == R.drawable.menu_view) {// 查看先前记录

            goToCareRecordHistoryActivity();

        }*/ else if (drawableId == R.drawable.menu_save) {// 保存

            if (request != null && request.signed) {
                showMsgAndVoiceAndVibrator("表单已签名，无法继续修改");
                return;
            }
            actionSaveTask();
        } else if (drawableId == R.drawable.menu_refer) {// 引用

            if (!viewFocused()) {
                showMsgAndVoiceAndVibrator("请求失败：请先定位输入框");
                return;
            }

            Intent intent = new Intent(this, CareRecordReferActivity.class);
            startActivityForResult(intent, RQ_GET_REFER);

        } else if (drawableId == R.drawable.menu_fresh) {// 刷新
            if (request != null) {
                ationGetDateTask(request.type, request.getBH());
            }
        } else if (drawableId == R.drawable.menu_help) {// 助手

            if (request == null || request.jgbh == null) {
                showMsgAndVoiceAndVibrator("请求失败：结构编号为空，请先获取模板或记录");
                return;
            }
            startHelpActivity();

        }
    }

    private void goToCareRecordHistoryActivity() {
        Intent intent = new Intent(this, CareRecordHistoryActivity.class);
        startActivityForResult(intent, RQ_VIEW_HISTORY);
    }

    private void startHelpActivity() {

        if (!viewFocused()) {
            showMsgAndVoiceAndVibrator("请求失败：请先定位输入框");
            return;
        }
        View view = getCurrentFocus();
        EditText edit = (EditText) view;
        Intent intent = new Intent(this, CareRecordHelpActivity.class);

        intent.putExtra("jgbh", request.jgbh);
        PlugIn plug = (PlugIn) edit.getTag(R.id.tag_KJ);
        if (plug != null) {
            intent.putExtra("ysbh", plug.YSBH);
            intent.putExtra("kjh", String.valueOf(plug.KJH));
        }
        startActivityForResult(intent, RQ_GET_HELP);
    }

    private boolean viewFocused() {

        View view = getCurrentFocus();
        if (view == null || !(view instanceof EditText)) {
            return false;
        }
        return true;
    }

    private void actionSaveTask() {
        // 离线保存
        if (!OffLineUtil.WifiConnected(this)) {

            List<SaveOrUpdateItem> itemList = getRequestItem();
            if (itemList == null || itemList.isEmpty()) {

                showMsgAndVoiceAndVibrator("保存失败：请填写数据");
                return;
            }
            SaveOrUpdateRequest saveOrUpdateRequest = new SaveOrUpdateRequest();
            saveOrUpdateRequest.ZYH = application.sickPersonVo.ZYH;
            saveOrUpdateRequest.HHBZ = cb.isChecked() ? "1" : "0";
            saveOrUpdateRequest.JLBH = request.jlbh;
            saveOrUpdateRequest.JGBH = request.jgbh;
            saveOrUpdateRequest.JLSJ = mTimeView.getText().toString();
            saveOrUpdateRequest.ItemList = itemList;
            saveOrUpdateRequest.SXSJ = DateUtil.format_yyyyMMdd_HHmm.format(new Date());
            saveOrUpdateRequest.JGID = application.jgId;
            saveOrUpdateRequest.YHID = application.user.YHID;

            String data = null;
            try {
                data = JsonUtil.toJson(saveOrUpdateRequest);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();

                showMsgAndVoiceAndVibrator("请求失败：解析错误");
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = "";
            if (request.type == GetDateTask.QRT_BY_JGBH) {
                if (application.user == null) {

                    showMsgAndVoiceAndVibrator("当前没有用户登录或用户登录已经过期");
                }
                uri = url + "nurserecord/save/record";
            } else if (request.type == GetDateTask.QRT_BY_JLBH) {
                uri = url + "nurserecord/update/record";
            }

            if (OffLineUtil.offLineSave(CareRecordActivity.this, uri, 2, data,
                    application.sickPersonVo.BRXM, "护理记录",
                    application.user.YHXM)) {
                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");
            }
            return;
        }

        SaveDateTask task = new SaveDateTask();
        tasks.add(task);
        task.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }

        //timePageView.setVisibility(View.VISIBLE);
        if (requestCode == RQ_GET_STRUCT_TREE) {

            String timeStr = DateTimeHelper.getServer_yyyyMMddHHmm00();
            initTimeTxt(timeStr);

            String jgbh = data.getStringExtra("jgbh");

            request = new Request();
            request.jgbh = jgbh;
            request.type = GetDateTask.QRT_BY_JGBH;
            ationGetDateTask(GetDateTask.QRT_BY_JGBH, jgbh);
            return;
        }

        if (requestCode == RQ_VIEW_HISTORY) {

            String jlbh = data.getStringExtra("jlbh");
            String jgbh = data.getStringExtra("jgbh");
            String jlsj = data.getStringExtra("jlsj");
            boolean wczt = data.getBooleanExtra("wczt", false);

            setRecordTime(jlsj);

            request = new Request();
            request.jlbh = jlbh;
            request.jgbh = jgbh;
            request.signed = wczt;
            request.type = GetDateTask.QRT_BY_JLBH;
            ationGetDateTask(GetDateTask.QRT_BY_JLBH, jlbh);
            return;
        }

        if (requestCode == RQ_GET_HELP || requestCode == RQ_GET_REFER) {

            String content = data.getStringExtra("help_content");
            View view = getCurrentFocus();
            if (view == null || !(view instanceof EditText)) {
                showMsgAndVoiceAndVibrator("赋值失败：未定位输入框");
                return;
            }
            EditText edit = (EditText) view;
            setEditText(content, edit);
            return;
        }
        /*====修改编号【FIXME】=====start*/
       /* if (requestCode == RQ_GET_REFER) {

            View view = getCurrentFocus();
            if (view == null || !(view instanceof EditText)) {
                showMsg("赋值失败：未定位输入框");
                return;
            }
            String content = data.getStringExtra("help_content");
            String realContent = "";

            if (content.contains("|||")) {//医嘱类 引用

                EditText edit = (EditText) view;
                PlugIn plugIn = (PlugIn) edit.getTag(R.id.tag_KJ);
                if (plugIn == null) {
                    return;
                }
                StringBuilder ytjsw = new StringBuilder();//液体及食物
                StringBuilder ytjswl = new StringBuilder();//液体及食物量
                StringBuilder ypyf = new StringBuilder();//途径（药品用法）
                String[] rowArray = content.split("\n");
                for (int i = 0; i < rowArray.length; i++) {
                    String str = rowArray[i].replace("|||", "|");
                    String[] array = str.split("\\|");//todo 竖线是特殊字符
                    if (array.length != 4) {
                        showMsg("赋值失败：获取医嘱数据失败！");
                        return;
                    }
                    String yzmc = array[0];
                    String jiliang = array[1];
                    String danwei = array[2];
                    String yongfa = array[3];
                    //
//                    boolean isNeedAddHuanHang =false;
                    int len = yzmc.length();//医嘱名称长度
                    try {
                        len = new String(yzmc.getBytes("GB2312"), "iso-8859-1").length();
                    } catch (UnsupportedEncodingException e) {

                    }
                    int lenTemp = 0;//要补足的空格位数
                    if (len < 22) {
                        lenTemp = 22 - len;
//                        isNeedAddHuanHang = true;
                    }
                    for (int x = 0; x < lenTemp; x++) {
                        yzmc += " ";
                    }
                    if ("ml".equals(danwei)) {
//                        ytjsw += yzmc + "\r\n" + "\r\n";
                        //协和
                        ytjsw.append(yzmc);//
                        ytjsw.append("　");//没有单位  加入全角空格 让 ytjsw 占满 2 行 ！不考虑 2 行以上
                        ytjsw.append("\r\n");
                        //
                        ytjswl.append(jiliang);
                        ytjswl.append("\r\n");
                    } else {
//                        ytjsw += yzmc + "\r\n" + array[1] + array[2] + "\r\n";
                        //协和
                        ytjsw.append(yzmc);
                        ytjsw.append(jiliang);
                        ytjsw.append(danwei);
                        ytjsw.append("\r\n");
                        //
                        ytjswl.append(" ");//不是ml留空白
                        ytjswl.append("\r\n");
                    }
                    ypyf.append(yongfa);
                    ypyf.append("\r\n");
                }

              String  ytjswStr = ytjsw.substring(0, ytjsw.length() - 2);
                String  ytjswlStr = ytjswl.substring(0, ytjswl.length() - 2);
                String  ypyfStr = ypyf.substring(0, ypyf.length() - 2);

                if (plugIn.XSMC.equals("液体及食物")) {//特殊情况

                    realContent = ytjswStr;
                    setEditText(realContent, edit);

                    ViewGroup root = (ViewGroup) mScrollView.getChildAt(0);
                    if (root == null) {
                        return;
                    }
                    int count = root.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View childView = root.getChildAt(i);
                        if (childView instanceof ViewGroup) {
                            for (int j = 0; j < ((ViewGroup) childView).getChildCount(); j++) {
                                View child = ((ViewGroup) childView).getChildAt(j);
                                if (child instanceof EditText) {

                                    EditText editText = (EditText) child;
                                    PlugIn plug = (PlugIn) editText.getTag(R.id.tag_KJ);
                                    if (plug != null) {
                                        if (plug.XSMC.equals("液体及食物量")) {
                                            setEditText(ytjswlStr, editText);

                                        } else if (plug.XSMC.equals("途径")) {
                                            setEditText(ypyfStr, editText);
                                        }
                                    }
                                }
                            }
                        }
                        if (childView instanceof PouponEditViewForMultiselect) {

                            PouponEditViewForMultiselect editText = (PouponEditViewForMultiselect) childView;
                            PlugIn plug = (PlugIn) editText.edit.getTag(R.id.tag_KJ);
                            if (plug != null) {
                                if (plug.XSMC.equals("液体及食物量")) {
                                    setEditText(ytjswlStr, editText.edit);

                                } else if (plug.XSMC.equals("途径")) {
                                    setEditText(ypyfStr, editText.edit);
                                }
                            }
                        }
                        if (childView instanceof PouponEditView) {

                            PouponEditView editText = (PouponEditView) childView;
                            PlugIn plug = (PlugIn) editText.edit.getTag(R.id.tag_KJ);
                            if (plug != null) {
                                if (plug.XSMC.equals("液体及食物量")) {
                                    setEditText(ytjswlStr, editText.edit);

                                } else if (plug.XSMC.equals("途径")) {
                                    setEditText(ypyfStr, editText.edit);
                                }
                            }
                        }
                    }
                    return;

                } else {//一般情况

                    realContent = ytjswStr + "|||" + ytjswlStr + "|||" + ypyfStr;
                    setEditText(realContent, edit);
                    return;
                }

            } else {
                realContent = content;
                EditText edit = (EditText) view;
                setEditText(realContent, edit);
                return;
            }
        }*/
        /*====修改编号【】=====end*/

        if (requestCode == RQ_GET_USERID) {
            String yhid = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_1);
            actionSign(yhid);
            return;
        }

    }

    private void actionSign(String... params) {

        SignTask task = new SignTask();
        tasks.add(task);
        task.execute(params);
    }

    @Override
    public void showPickerDialog(Integer viewId, String value, String format) {
        String dateTimeStr = "";
        // 设置时间
        if (!EmptyTool.isBlank(value)) {
            dateTimeStr= DateTimeFactory.getInstance().custom2DateTime(value, format);
        } else {
            dateTimeStr = DateTimeHelper.getServerDateTime();
        }
        YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateTimeStr);
        showPickerDateTimeCompat(ymdHMs, viewId);
    }

    class SignTask extends AsyncTask<String, String, Response> {
        String yhid;

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.signing);
        }

        @Override
        protected Response doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }

            if (request == null || EmptyTool.isBlank(request.jlbh)) {
                return null;
            }

            NurseRecordApi api = NurseRecordApi
                    .getInstance(CareRecordActivity.this);

            yhid = params[0];
            String jlbh = request.jlbh;
            String jgid = application.jgId;

            SignatureDataRequest obj = new SignatureDataRequest();
            obj.JLBH = jlbh;
            obj.YHID = yhid;
            obj.JGID = jgid;
            String data = null;
            try {
                data = JsonUtil.toJson(obj);
            } catch (IOException e) {
                e.printStackTrace();

                showMsgAndVoiceAndVibrator("请求失败：解析错误");

            }
            Response<String> response = api.SignName(data);
            return response;

        }

        @Override
        protected void onPostExecute(Response result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(CareRecordActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionSign(yhid);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice("签名成功");

                    if (request != null) {
                        request.signed = true;
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(CareRecordActivity.this).playSound(
                            R.raw.wrong, CareRecordActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }
    }

    private void startSignActivity() {
        Intent intent = new Intent(this, SignActivity.class);
        startActivityForResult(intent, RQ_GET_USERID);
    }

    private void setEditText(String txt, EditText edit) {

        PlugIn plug = (PlugIn) edit.getTag(R.id.tag_KJ);
        if (plug == null) {
            return;
        }
        if (plug.SJLX == 2) {// 接收数字

            if (NumberUtils.isNumber(txt)) {
                edit.setText(txt);
            } else {
                showMsgAndVoiceAndVibrator("赋值失败：输入框当前只接收数值数据");
            }
            return;
        }
        String orial = edit.getText().toString();
        edit.setText(orial + txt);

    }

    private void setRecordTime(String jlsj) {

        if (EmptyTool.isBlank(jlsj)) {
            return;
        }

        String timeStr = DateTimeHelper.getServer_yyyyMMddHHmm00(jlsj);
        initTimeTxt(timeStr);

    }

    class Request {
        public String jgbh;
        public String jlbh;
        public byte type;
        public boolean signed;

        /**
         * 根据请求类型，获取编号
         *
         * @return
         */
        public String getBH() {
            if (type == GetDateTask.QRT_BY_JLBH) {
                return jlbh;
            } else {// 默认根据jgbh
                return jgbh;
            }
        }
    }


    private void initTimePageView() {

        mTimeView = (TextView) timePageView
                .findViewById(R.id.nurse_datetime_txt);
        timePageView.setOnClickListener(onClickListener);

        String timeStr = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(timeStr);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            String dateStr = mTimeView.getText().toString();
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }

            // 导入年月数据

            YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
            showPickerDateTimeCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        if (viewId == timePageView.getId()) {
            String dateTime =DateTimeFactory.getInstance().ymdhms2DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);
            initTimeTxt(dateTime);
        } else {
            // 来自动态控件
            View view = mScrollView.findViewById(viewId);
            if (view != null && view instanceof TextView) {
                String format = ((TextView) view).getHint().toString();
                //
                String dateTime =DateTimeFactory.getInstance().ymdhms2DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);
                String value= DateTimeFactory.getInstance().dateTime2Custom(dateTime, format);
                ((TextView) view).setText(value);
            }
        }

    }

    private void initTimeTxt(String yyyyMMddHHmm) {
        mTimeView.setText(yyyyMMddHHmm);
    }

    /**
     * 重置View
     */
    private void resetView() {

        mScrollView.removeAllViews();
        //timePageView.setVisibility(View.GONE);
        taggleMenu(false, true);
    }
/*
升级编号【56010022】============================================= start
护理记录:护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
================= Classichu 2017/10/18 10:33
*/
    /**
     * @param xmbh
     */
    private EditText editText;

    public void showLastXMValueEdit(int xmbh, EditText edit) {
        editText = edit;
        actionGetLastDateTask(String.valueOf(xmbh), "1");
    }

    private PlugIn plugIn;

    public void showLastXMValueCombox(int xmbh, EditText edit, PlugIn plug) {
        editText = edit;
        plugIn = plug;
        actionGetLastDateTask(String.valueOf(xmbh), "2");
    }

    private PouponEditViewForMultiselect pullEditViewForMultiselect;

    public void showLastXMValueMultCombox(int xmbh, PouponEditViewForMultiselect pullEditView, PlugIn plug) {
        pullEditViewForMultiselect = pullEditView;
        plugIn = plug;
        actionGetLastDateTask(String.valueOf(xmbh), "3");
    }


    private void actionGetLastDateTask(String xmbh, String type) {
        GetLastDateTask getLastDateTask = new GetLastDateTask();
        tasks.add(getLastDateTask);
        getLastDateTask.execute(xmbh, type);
    }

    class GetLastDateTask extends AsyncTask<String, String, Response<List<LastDataBean>>> {
        private AlertDialog alertDialog = null;
        private String TYPE;

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }

            if (application.sickPersonVo == null) {
                return null;
            }
            String jgid = application.jgId;
            String hsgh = application.user.YHID;
            String zyh = application.sickPersonVo.ZYH;
            String xmbh = params[0];
            TYPE = params[1];
            NurseRecordApi api = NurseRecordApi
                    .getInstance(CareRecordActivity.this);

            Response<List<LastDataBean>> response = api.getlastXMData(xmbh,
                    zyh,hsgh, jgid, Constant.sysType);
            return response;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<LastDataBean>> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result.ReType == 0 && result.Data != null && result.Data.size() > 0) {
                final ArrayList<LastDataBean> datas = new ArrayList<>(result.Data);
                if ("3".equals(TYPE)) {
                    for (LastDataBean data : datas) {
                        String KJNR = data.XMQZ;
                        String cn = parseTextContent(KJNR, plugIn.DXFG, plugIn.DropdownItem);
                        data.XMQZ_Raw = KJNR;
                        data.XMQZ = cn;
                    }
                }
                //
                Context context = CareRecordActivity.this;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
                ListView listView = new ListView(context);

                linearLayout.addView(listView);  EmptyViewHelper.setEmptyView(listView, "listView");
                NouseRecordLastDataAdapter adapter = new NouseRecordLastDataAdapter(context, datas);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alertDialog.hide();

                        switch (TYPE) {
                            case "1":
                                //
                                editText.setText(datas.get(position).XMQZ);
                                break;
                            case "2":
                                plugIn.KJNR = datas.get(position).XMQZ;
                                editText.setText(datas.get(position).XMQZ);
                                break;
                            case "3":
                                plugIn.KJNR = datas.get(position).XMQZ_Raw;
                                String txt = writeTextContent(plugIn);
                                pullEditViewForMultiselect.edit.setText(txt);
                                break;
                            default:
                        }


                    }
                });
                listView.setAdapter(adapter);
                View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, "最近记录");
                builder.setView(linearLayout)
                        //.setTitle("最近记录")
                        .setCustomTitle(txt);
                builder.setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            } else {
                showMsgAndVoice("无历史数据");
            }
        }
    }

    private String parseTextContent(String kjnr, String DXFG, List<PouponItem> DropdownItemList) {
        String value = "";
        if (!EmptyTool.isBlank(kjnr)) {
            String[] strings = kjnr.split(DXFG);//key
            List<String> asList = Arrays.asList(strings);
            String s = null;
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < asList.size(); j++) {
                String s1 = asList.get(j).trim();
                boolean isOk = false;
                List<PouponItem> datas = DropdownItemList;
                for (int k = 0; k < datas.size(); k++) {
                    String xznr = datas.get(k).XZNR;
                    String key = datas.get(k).VALUE;
                    if (key.equals(s1)) {
                        s = xznr + DXFG;
                        sb.append(s);
                        isOk = true;
                        break;
                    }
                }
                if (!isOk) {
                    sb.append(s1 + DXFG);
                }
            }
            value = sb.toString();
            if (value.endsWith(DXFG)) {
                value = value.substring(0, value.length() - 1);
            }
            return value;
        }
        return null;
    }

    private String writeTextContent(PlugIn plugIn) {
        /**
         * add   2017-9-12 18:58:25
         */
        for (PouponItem pouponItem : plugIn.DropdownItem) {
            pouponItem.ISCHECK = false;
        }
        String value = "";
        if (!EmptyTool.isBlank(plugIn.KJNR)) {
            String[] strings = plugIn.KJNR.split(plugIn.DXFG);//key
            List<String> asList = Arrays.asList(strings);
            String s = null;
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < asList.size(); j++) {
                String s1 = asList.get(j).trim();
                boolean isOk = false;
                List<PouponItem> datas = plugIn.DropdownItem;
                for (int k = 0; k < datas.size(); k++) {
                    String xznr = datas.get(k).XZNR;
                    String key = datas.get(k).VALUE;
                    if (key.equals(s1)) {
                        //add by louis
                        datas.get(k).ISCHECK = true;
                        s = xznr + plugIn.DXFG;
                        sb.append(s);
                        isOk = true;
                        break;
                    }
                }
                if (!isOk) {
                    sb.append(s1 + plugIn.DXFG);
                }
            }
            value = sb.toString();
            if (value.endsWith(plugIn.DXFG)) {
                value = value.substring(0, value.length() - 1);
            }
            return value;
        }
        return null;
    }

    /* =============================================================== end */
    private void initActionBar() {

        actionBar.setTitle("护理记录");
        actionBar.setPatient(application.sickPersonVo.XSCH
                + application.sickPersonVo.BRXM);

        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "历史";
            }

            @Override
            public void performAction(View view) {
                goToCareRecordHistoryActivity();
            }

            @Override
            public int getDrawable() {
                return R.drawable.menu_history_n;
            }
        });

        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {

                ViewGroup root = (ViewGroup) mScrollView.getChildAt(0);
                if (root == null) {
                    showMsgAndVoiceAndVibrator("请求失败：未填写数据");
                    return;
                }

                if (request != null && request.signed) {
                    showMsgAndVoiceAndVibrator("表单已签名，无法继续修改");
                    return;
                }
                actionSaveTask();
            }

            @Override
            public int getDrawable() {

                return R.drawable.ic_done_black_24dp;

            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_care_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        mScrollView = (NestedScrollView) findViewById(R.id.id_sv);

        timePageView = findViewById(R.id.id_ll_controller);
        cb = (CheckBox) findViewById(R.id.care_record_page_cb);

        initActionBar();
        initTimePageView();
        resetView();
        toRefreshData();
    }

    /**
     * 遍历动态生成的控件，并获取输入数据
     *
     * @return
     */
    private ArrayList<SaveOrUpdateItem> getRequestItem() {

        ArrayList<SaveOrUpdateItem> list = new ArrayList<SaveOrUpdateItem>();

        ViewGroup root = (ViewGroup) mScrollView.getChildAt(0);
        if (root == null) {
            return list;
        }

        String emptyTag = "　　";//空2格
        // 获取隐藏基本项
        Object obj = root.getTag();
        if (obj != null) {
            @SuppressWarnings("unchecked")
            List<PlugIn> plugs = (List<PlugIn>) obj;
            for (PlugIn plug : plugs) {
                SaveOrUpdateItem item = new SaveOrUpdateItem();
                item.XMBH = plug.KJH;
                item.VALUE =plug.KJNR;
                list.add(item);
            }
        }

        final int size = list.size();

        int count = root.getChildCount();

        for (int i = 0; i < count; i++) {

            View childView = root.getChildAt(i);
            if (childView instanceof ViewGroup) {

                for (int j = 0; j < ((ViewGroup) childView).getChildCount(); j++) {
                    View child = ((ViewGroup) childView).getChildAt(j);
                    if (child instanceof EditText) {

                        EditText edit = (EditText) child;
                        PlugIn plug = (PlugIn) edit.getTag(R.id.tag_KJ);
                        if (plug != null) {
                            int kjh = plug.KJH;
                            String value = edit.getText().toString();
                            if (!EmptyTool.isBlank(value)) {
                                SaveOrUpdateItem item = new SaveOrUpdateItem();
                                item.XMBH = kjh;
                             /*   if ("病情及护理措施".equals(plug.XSMC)&&!value.startsWith(emptyTag)){
                                    item.VALUE =emptyTag+value;
                                    //
                                    ThreadTool.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            edit.setText(item.VALUE);
                                        }
                                    });
                                }else{
                                    item.VALUE =value;
                                }*/
                                item.VALUE = value;
                                list.add(item);
                                break;
                            }
                        }
                    }
                    /***
                     * 带查询历史项目的LinearLayout 里的PouponEditViewForMultiselect
                     */
                    if (child instanceof PouponEditViewForMultiselect) {

                        PouponEditViewForMultiselect edit = (PouponEditViewForMultiselect) child;
                        PlugIn plug = (PlugIn) edit.edit.getTag(R.id.tag_KJ);
                        if (plug != null) {

                            int kjh = plug.KJH;
                            String value = edit.getEditText().getText().toString();

                            ArrayList<PouponItem> datas = edit.datas;
                            String dxfg = edit.dxfg;
                            if (!EmptyTool.isBlank(value)) {
                                String[] strings = value.split(dxfg);
                                List<String> asList = Arrays.asList(strings);
                                String s = null;
                                StringBuffer sb = new StringBuffer();
                                for (int m = 0; m < asList.size(); m++) {
                                    String s1 = asList.get(m);
                                    boolean isOk = false;
                                    for (int k = 0; k < datas.size(); k++) {
                                        String xznr = datas.get(k).XZNR;
                                        String key = datas.get(k).VALUE;
                                        if (xznr.equals(s1)) {
                                            s = key + dxfg;
                                            sb.append(s);
                                            isOk = true;
                                            break;
                                        }
                                    }
                                    if (!isOk) {
                                        sb.append(s1).append(dxfg);
                                    }
                                }
                                value = sb.toString();
                                if (value.endsWith(dxfg)) {
                                    value = value.substring(0, value.length() - 1);
                                }
                                SaveOrUpdateItem item = new SaveOrUpdateItem();
                                item.XMBH = kjh;
//                                item.VALUE =("病情及护理措施".equals(plug.XSMC)&&!value.startsWith(emptyTag))?emptyTag+value: value;
                                item.VALUE = value;
                                list.add(item);
                            }
                        }
                    }
                    /***
                     * 带查询历史项目的LinearLayout 里的PouponEditView
                     */
                    if (child instanceof PouponEditView) {

                        PouponEditView edit = (PouponEditView) child;
                        PlugIn plug = (PlugIn) edit.edit.getTag(R.id.tag_KJ);
                        if (plug != null) {

                            int kjh = plug.KJH;
                            String value = edit.getEditText().getText().toString();
                            ArrayList<PouponItem> pouponItems = edit.datas;

                            if (!EmptyTool.isBlank(value)) {
                                for (int n = 0; n < pouponItems.size(); n++) {
                                    if (pouponItems.get(n).XZNR.equals(value)) {
                                        value = pouponItems.get(n).VALUE;
                                    }
                                }
                                SaveOrUpdateItem item = new SaveOrUpdateItem();
                                item.XMBH = kjh;
//                                item.VALUE =("病情及护理措施".equals(plug.XSMC)&&!value.startsWith(emptyTag))?emptyTag+value: value;
                                item.VALUE = value;
                                list.add(item);
                            }
                        }
                    }
                }

            }
            /***
             * 不带查询历史项目的LinearLayout 直接是PouponEditViewForMultiselect
             */
            if (childView instanceof PouponEditViewForMultiselect) {

                PouponEditViewForMultiselect edit = (PouponEditViewForMultiselect) childView;
                PlugIn plug = (PlugIn) edit.edit.getTag(R.id.tag_KJ);
                if (plug != null) {

                    int kjh = plug.KJH;
                    String value = edit.getEditText().getText().toString();

                    ArrayList<PouponItem> datas = edit.datas;
                    String dxfg = edit.dxfg;
                    if (!EmptyTool.isBlank(value)) {
                        String[] strings = value.split(dxfg);
                        List<String> asList = Arrays.asList(strings);
                        String s = null;
                        StringBuffer sb = new StringBuffer();
                        for (int j = 0; j < asList.size(); j++) {
                            String s1 = asList.get(j);
                            boolean isOk = false;
                            for (int k = 0; k < datas.size(); k++) {
                                String xznr = datas.get(k).XZNR;
                                String key = datas.get(k).VALUE;
                                if (xznr.equals(s1)) {
                                    s = key + dxfg;
                                    sb.append(s);
                                    isOk = true;
                                    break;
                                }
                            }
                            if (!isOk) {
                                sb.append(s1 + dxfg);
                            }
                        }
                        value = sb.toString();
                        if (value.endsWith(dxfg)) {
                            value = value.substring(0, value.length() - 1);
                        }
                        SaveOrUpdateItem item = new SaveOrUpdateItem();
                        item.XMBH = kjh;
//                        item.VALUE =("病情及护理措施".equals(plug.XSMC)&&!value.startsWith(emptyTag))?emptyTag+value: value;
                                item.VALUE = value;
                        list.add(item);
                    }
                }
            }
            /***
             * 不带查询历史项目的LinearLayout 直接是PouponEditView
             */
            if (childView instanceof PouponEditView) {

                PouponEditView edit = (PouponEditView) childView;
                PlugIn plug = (PlugIn) edit.edit.getTag(R.id.tag_KJ);
                if (plug != null) {

                    int kjh = plug.KJH;
                    String value = edit.getEditText().getText().toString();
                    ArrayList<PouponItem> pouponItems = edit.datas;

                    if (!EmptyTool.isBlank(value)) {
                        for (int j = 0; j < pouponItems.size(); j++) {
                            if (pouponItems.get(j).XZNR.equals(value)) {
                                value = pouponItems.get(j).VALUE;
                            }
                        }
                        SaveOrUpdateItem item = new SaveOrUpdateItem();
                        item.XMBH = kjh;
//                        item.VALUE =("病情及护理措施".equals(plug.XSMC)&&!value.startsWith(emptyTag))?emptyTag+value: value;
                                item.VALUE = value;
                        list.add(item);
                    }
                }
            }
        }
        if (size == list.size()) {
            return null;
        }
        return list;
    }

    class SaveDateTask extends AsyncTask<String, String, Response> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response doInBackground(String... params) {

            if (request == null || application.sickPersonVo == null) {
                return null;
            }

            List<SaveOrUpdateItem> itemList = getRequestItem();

            if (itemList == null || itemList.isEmpty()) {
                Response response = new Response();
                response.ReType = 1;
                response.Msg = "保存失败：请填写数据";
                return response;
            }

            SaveOrUpdateRequest saveOrUpdateRequest = new SaveOrUpdateRequest();
            saveOrUpdateRequest.ZYH = application.sickPersonVo.ZYH;
            saveOrUpdateRequest.HHBZ = cb.isChecked() ? "1" : "0";
            saveOrUpdateRequest.JLBH = request.jlbh;
            saveOrUpdateRequest.JGBH = request.jgbh;
            saveOrUpdateRequest.JLSJ = mTimeView.getText().toString();
            saveOrUpdateRequest.ItemList = itemList;
            saveOrUpdateRequest.SXSJ = DateUtil.format_yyyyMMdd_HHmm.format(new Date());
            saveOrUpdateRequest.JGID = application.jgId;

            saveOrUpdateRequest.YHID = application.user.YHID;
            String data = null;
            try {
                data = JsonUtil.toJson(saveOrUpdateRequest);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            NurseRecordApi api = NurseRecordApi
                    .getInstance(CareRecordActivity.this);

            if (request.type == GetDateTask.QRT_BY_JGBH) {

                if (application.user == null) {
                    return null;
                }
                String yhid = application.user.YHID;

                return api.SaveData(data);
            } else if (request.type == GetDateTask.QRT_BY_JLBH) {

                return api.UpdateData(data);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Response result) {

            hideLoadingDialog();
            tasks.remove(this);


            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(CareRecordActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionSaveTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);

                    boolean signed = false;
                    if (request != null) {
                        signed = request.signed;
                    }
                    String jgbh = request.jgbh;
                    request = new Request();
                    request.jlbh = (String) result.Data;
                    request.type = GetDateTask.QRT_BY_JLBH;
                    request.signed = signed;
                    request.jgbh = jgbh;

                    if (!signed) {
//    ##### 协和 不用签名                    startSignActivity();
                    }


                } else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(CareRecordActivity.this).playSound(
                            R.raw.wrong, CareRecordActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }

    }

    @SuppressWarnings("rawtypes")
    class GetDateTask extends AsyncTask<String, String, Response> {

        public static final byte QRT_BY_JGBH = 0;
        public static final byte QRT_BY_JLBH = 1;

        private byte mType = QRT_BY_JGBH;

        public GetDateTask(byte type) {
            this.mType = type;
        }

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }

            if (application.sickPersonVo == null) {
                return null;
            }
            String jgid = application.jgId;
            NurseRecordApi api = NurseRecordApi
                    .getInstance(CareRecordActivity.this);

            if (mType == QRT_BY_JLBH) {
                String jlbh = params[0];
                String zyh = application.sickPersonVo.ZYH;
                Response<List<UIView>> response = api.GetCtrlListByJlbh(
                        zyh, jlbh, jgid,
                        Constant.sysType);

                return response;
            } else {// 默认根据jgbh
                String jgbh = params[0];
                String zyh = application.sickPersonVo.ZYH;
                Response<List<UIView>> response = api.GetCtrlListByJgbh(jgbh,
                        zyh, jgid, Constant.sysType);

                return response;
            }

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (mScrollView.getChildCount() > 0) {
                mScrollView.removeAllViews();
                mScrollView.invalidate();
            }
//            taggleMenu(true, false);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(CareRecordActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    List<UIView> viewsDate = (List<UIView>) result.Data;
                    importViews(viewsDate);

                    if (mType == QRT_BY_JLBH) {

                        if (request != null && request.signed) {
                            taggleMenu(false, false);
                            return;
                        }

                    }

                    taggleMenu(true, false);

                } else {
                    showMsgAndVoice(result.Msg);
                 /*   MediaUtil.getInstance(CareRecordActivity.this).playSound(
                            R.raw.wrong, CareRecordActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }
    }


    public void importViews(List<UIView> viewsDate) {

        if (viewsDate == null) {
            return;
        }
        if (viewsDate.size() > 0 && !EmptyTool.isBlank(viewsDate.get(0).HYBZ)) {
            cb.setChecked(viewsDate.get(0).HYBZ.equals("1"));
        }
        NurseRecordViewFactory factory = new NurseRecordViewFactory(CareRecordActivity.this, this,
                application);

        if (mScrollView.getChildCount() > 0) {
            mScrollView.removeAllViews();
            mScrollView.invalidate();
        }
        mScrollView.addView(factory.builderUi(viewsDate));

    }

    private void ationGetDateTask(byte type, String... params) {
        GetDateTask task = new GetDateTask(type);
        tasks.add(task);
        task.execute(params);

    }

    @Override
    public void initBarBroadcast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    resetView();
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                }
            }
        };
    }

}
