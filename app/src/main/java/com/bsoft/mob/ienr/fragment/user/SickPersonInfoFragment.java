package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.fragment.dialog.SickPersonDetailFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.HtmlCompatHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.expense.ExpenseTotal;
import com.bsoft.mob.ienr.model.kernel.AllergicDrug;
import com.bsoft.mob.ienr.model.kernel.PatientDetailResponse;
import com.bsoft.mob.ienr.model.kernel.SickPersonDetailVo;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * 病人信息页
 *
 * @author hy
 */
public class SickPersonInfoFragment extends BaseUserFragment {

    private TextView text;

    private AlertDialog.Builder builder;
    private AlertDialog.Builder moreBuilder;
    private AlertDialog moreDialog;
    private final CharSequence[] arr_more = {"病人出院", "腕带损坏"};
    // 是否在绑定状态下
    private boolean flage = false;

    private ListView id_lv;


    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_sickperson_info;
    }

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {
        text = (TextView) mainView.findViewById(R.id.id_tv);
        text.setVisibility(View.GONE);
          /*
           升级编号【56010016】============================================= start
           病人列表：重整病人信息，确定哪些信息需要演示
           ================= Classichu 2017/10/18 9:34
           */
        id_lv = (ListView) mainView.findViewById(R.id.id_lv);
        // RFID支持 start05
        //有需要RFID绑定功能的打开下面代码行
        //checkRFID();
        // RFID支持 end05
        setView();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
          /*
           升级编号【56010016】============================================= start
           病人列表：重整病人信息，确定哪些信息需要演示
           ================= Classichu 2017/10/18 9:34
           */
        //######initMoreTextView();
        actionGetDetailTask();
        /* =============================================================== end */
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadcast();
    }

    private void initBroadcast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    if (flage) {

                        showMsgAndVoiceAndVibrator("此号码已经绑定病人,无法再次绑定");
                    } else {
                        sendUserName();
                          /* actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                        + mAppApplication.sickPersonVo.BRXM);*/
                        setView();
                        toRefreshData();
                    }
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    }
                    // RFID支持 start01
                } else if (BarcodeActions.RFID_Get.equals(intent.getAction())) {
                    if (flage) {

                        final BarcodeEntity bar = (BarcodeEntity) intent
                                .getParcelableExtra("barinfo");

                        builder = new Builder(getActivity());
                        builder.setMessage("扫描的号码:\n" + bar.source
                                + "\n你确定要绑定病人吗?");
                        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips)));
                        builder.setPositiveButton(
                                "确认",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new BindPatientTask()
                                                .execute(bar.source);
                                    }
                                });
                        builder.setNegativeButton(
                                getString(R.string.project_operate_cancel),
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                }
                // RFID支持 end01
            }
        };
    }

    @Override
    public void onDestroy() {
        if (((UserModelActivity) getActivity()).barCode == null) {
            throw new RuntimeException("BarCodeFactory.java  getBarCode() return IBarCode can not null");
        }
        ((UserModelActivity) getActivity()).barCode.setType(1);
        super.onDestroy();
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    void setView() {

        final SickPersonVo vo = mAppApplication.sickPersonVo;
        if (vo == null) {
            return;
        }
        actionBar.setTitle("病人信息");
        actionBar.setPatient(vo.BRCH + vo.BRXM);
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (null != vo.RFID && vo.RFID.length() > 0) {
                    builder = new Builder(getActivity());
                    builder.setMessage("确定要解绑吗?");
                    builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips)));
                    builder.setPositiveButton(
                            "确认",
                            new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    moreBuilder = new AlertDialog.Builder(
                                            getActivity());
                                    moreBuilder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "拒绝理由"));
                                    moreBuilder
                                            .setItems(
                                                    arr_more,
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                final int item) {
                                                            new UnBindPatientTask()
                                                                    .execute(
                                                                            vo.RFID,
                                                                            String.valueOf(item));
                                                        }
                                                    });
                                    moreDialog = moreBuilder.create();
                                    moreDialog.show();
                                }
                            });
                    builder.setNegativeButton(
                            getString(R.string.project_operate_cancel),
                            new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    if (!flage) {
                        builder = new Builder(getActivity());
                        builder.setMessage("确定要进行绑定吗?");
                        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips)));
                        builder.setPositiveButton(
                                "确认",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        flage = true;
                                        ((UserModelActivity) getActivity()).barCode
                                                .setType(2);
                                        text.setText("使用右快捷键进行扫描RFID,点击取消绑定操作");
                                    }
                                });
                        builder.setNegativeButton(
                                getString(R.string.project_operate_cancel),
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else {
                        builder = new Builder(getActivity());
                        builder.setMessage("确定要进行取消绑定吗?");
                        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips)));
                        builder.setPositiveButton(
                                "确认",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        flage = false;
                                        ((UserModelActivity) getActivity()).barCode
                                                .setType(1);
                                        text.setText("未绑定RFID号,点击进行绑定");
                                    }
                                });
                        builder.setNegativeButton(
                                getString(R.string.project_operate_cancel),
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();

                    }
                }
            }
        });
    }


    /*升级编号【56010016】============================================= start
                 病人列表：重整病人信息，确定哪些信息需要演示
                 ================= Classichu 2017/10/18 9:34
                 */
    private void actionGetDetailTask() {

        GetDetailTask task = new GetDetailTask();
        tasks.add(task);
        task.execute();
    }

    class GetDetailTask extends AsyncTask<String, Void, Response<PatientDetailResponse>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();

        }

        @Override
        protected Response<PatientDetailResponse> doInBackground(String... arg0) {

            final SickPersonVo vo = mAppApplication.sickPersonVo;
            if (vo == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;

            PatientApi api = PatientApi.getInstance(getActivity());

            return api.GetPatientDetail(zyh, jgid);
        }

        @Override
        protected void onPostExecute(Response<PatientDetailResponse> result) {

            hideSwipeRefreshLayout();

            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionGetDetailTask();
                        }
                    }).showLoginDialog();
                    return;
                }
                if (result.ReType == 0) {
                    PatientDetailResponse response = result.Data;
  /*
           升级编号【56010016】============================================= start
           病人列表：重整病人信息，确定哪些信息需要演示
           ================= Classichu 2017/10/18 9:34
           */
                    //#### showDetailFragment(person, expenseTotal);
                    setupList(response);
                    /* =============================================================== end */

                } else {
                    showMsgAndVoiceAndVibrator("加载失败");

                }
            } else {
                showMsgAndVoiceAndVibrator("加载失败");
            }
        }
    }

    private void setupList(PatientDetailResponse response) {
        SickPersonDetailVo person = response.patient;
        ExpenseTotal total = response.expenseTotal;
        String diagnose = response.diagnose;
        List<AllergicDrug> allergicDrug = response.allergicDrugs;

        List<CharSequence> list = new ArrayList<>();
        String brch = "床号：" + (EmptyTool.isBlank(person.XSCH) ? "无" : person.XSCH + "床");
        list.add(brch);
        String brxm = "姓名：" + person.BRXM;
        String brxb = "    性别：" + (person.BRXB == 1 ? "男" : "女");
        String brnl = "    年龄：" + (EmptyTool.isBlank(person.BRNL) ? "无" : person.BRNL);
        list.add(brxm + brxb + brnl);

        String zyhm = "病案号：" + person.ZYHM;
        list.add(zyhm);

        if (!EmptyTool.isBlank(person.BRXX)) {
            String lxdh = "血型：" + person.BRXX;
            list.add(lxdh);
        }

       /* if (!EmptyTool.isBlank(person.KSMC)) {
            String ksmc = "病人科室：" + person.KSMC;
            list.add(ksmc);
        }*/

        if (!EmptyTool.isBlank(diagnose)) {
            String zdxx = "诊断：" + diagnose;
            list.add(zdxx);
        }
        //饮食情况
        if (!EmptyTool.isBlank(person.YSDM)) {
            String ysdm = "饮食：" + person.YSDM;
            list.add(ysdm);
        }
        //病人性质
        if (!EmptyTool.isBlank(person.BRXZ)) {
//            String brxz = "病人性质：" + person.BRXZ;
            String brxz = "医保性质：" + person.BRXZ;
            list.add(brxz);
        }
        //联系电话
        if (!EmptyTool.isBlank(person.LXDH)) {
            String lxdh = "联系电话：" + person.LXDH;
            list.add(lxdh);
        }
        //////////////////////
      /*    String ryrqStr = person.RYRQ;
        if (!EmptyTool.isBlank(ryrqStr)) {
            String ryrq = "入院日期：" + ryrqStr.split("T")[0];
            list.add(ryrq);
        }
        if (!EmptyTool.isBlank(person.YSMC)) {
            String ysmc = "主治医生：" + person.YSMC;
            list.add(ysmc);
        }*/
        /*if (total != null) {
            String zjje = "总费用：" + total.ZJJE;
            list.add(zjje);

            String zfje = ("自负金额：" + total.ZFJE);
            list.add(zfje);

            String jkje = ("交款金额：" + total.JKJE);
            list.add(jkje);

            String fyye = ("费用余额：" + total.FYYE);
            list.add(fyye);
        }*/
        //过敏信息
      if (allergicDrug != null && allergicDrug.size() > 0) {
          StringBuilder gmxxSB = new StringBuilder();
          for (int i = 0; i <allergicDrug.size() ; i++) {
              gmxxSB.append(allergicDrug.get(i).YPMC);
            if (i!=allergicDrug.size()-1){
                gmxxSB.append(",");
            }
          }
          String jgmc = ("过敏药物：" + gmxxSB.toString());
          CharSequence jgmcCharSequence = HtmlCompatHelper.fromHtml("<font color=\"#ff0000\">" + jgmc + "</font>");
          list.add(jgmcCharSequence);
        }
      /*    //
        if (!EmptyTool.isBlank(person.XZMC)) {
            String xzmc = "费用性质：" + person.XZMC;
            list.add(xzmc);
        }*/

       /* id_lv.setAdapter(new ArrayAdapter<>(mContext,
                R.layout.item_list_text_one_primary, R.id.name, list));*/
        NameAdapter nameAdapter = new NameAdapter(list);
        id_lv.setAdapter(nameAdapter);

        EmptyViewHelper.setEmptyView(id_lv, "id_lv");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, id_lv);
    }

    /* =============================================================== end */
    public  class NameAdapter extends BaseAdapter{
        public NameAdapter(List<CharSequence> mList) {
            this.mList = mList;
        }

        private List<CharSequence> mList;
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_text_one_primary, parent, false);
                vHolder = new ViewHolder();
                vHolder.XMMC = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.XMMC.setText(mList.get(position));
            return convertView;
        }
    }
    class ViewHolder {
        public TextView XMMC;
    }
    private class BindPatientTask extends AsyncTask<String, Void, Response<String>> {

        String rfid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.binding);
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            if (mAppApplication.user == null || mAppApplication.sickPersonVo == null) {
                return null;
            }
            rfid = params[0];
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            // RFID支持 start02
            String bqid = mAppApplication.getAreaId();
            return PatientApi.getInstance(getActivity()).PatientBindRFID(zyh,
                    params[0], yhid, bqid, jgid);
            // RFID支持 end02
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;

                } else if (result.ReType == 0) {
                    showMsgAndVoice("绑定成功!");
                    mAppApplication.sickPersonVo.RFID = rfid;
                    flage = false;
                    ((UserModelActivity) getActivity()).barCode.setType(1);
                    text.setText("已绑定RFID号,点击取消绑定该病人");
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("绑定失败!");
            }

        }
    }

    private class UnBindPatientTask extends
            AsyncTask<String, Void, Response<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.cancel_binding);
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            if (mAppApplication.user == null || mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String bqid = mAppApplication.getAreaId();
            // RFID支持 start03
            String jgid = mAppApplication.jgId;
            return PatientApi.getInstance(getActivity()).PatientUnBindRFID(
                    zyh, params[0], params[1], yhid, bqid, jgid);
            // RFID支持 end03
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);

            hideSwipeRefreshLayout();
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;

                } else if (result.ReType == 0) {
                    showMsgAndVoice("解绑成功!");
                    mAppApplication.sickPersonVo.RFID = null;
                    flage = false;
                    ((UserModelActivity) getActivity()).barCode.setType(2);
                    text.setText("未绑定RFID号,点击进行绑定");
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("加载失败");
            }

        }
    }


    @Deprecated
    protected void showDetailFragment(SickPersonDetailVo person,
                                      ExpenseTotal expenseTotal) {

        SickPersonDetailFragment detailFragment = SickPersonDetailFragment.newInstance(person, expenseTotal);
   /*     try {
            getFragmentManager().beginTransaction()
                    .add(detailFragment, "DetailFragment")
                    .commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }*/
        DialogFragmentShowHelper.show(getChildFragmentManager(), detailFragment, "DetailFragment");
    }

    // RFID支持 start04
    private void checkRFID() {
        RFIDCheckTask task = new RFIDCheckTask();
        tasks.add(task);
        task.execute();
    }

    class RFIDCheckTask extends AsyncTask<Void, Void, Response<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog("正在检查该病人是否绑定RFID腕带...");
        }

        @Override
        protected Response<String> doInBackground(Void... params) {

            if (mAppApplication.user == null || mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            return PatientApi.getInstance(getActivity()).GetRFID(zyh, jgid);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            tasks.remove(this);
            hideSwipeRefreshLayout();
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;

                } else if (result.ReType == 0) {
                    flage = true;
                    mAppApplication.sickPersonVo.RFID = result.Data;
                    text.setText("已绑定RFID号,点击取消绑定该病人");
                } else {
                    flage = false;
                    mAppApplication.sickPersonVo.RFID = "";
                    text.setText("未绑定RFID号,点击进行绑定");
                }

            } else {
                showMsgAndVoiceAndVibrator("加载失败");
            }

        }
    }
    // RFID支持 end04
}
