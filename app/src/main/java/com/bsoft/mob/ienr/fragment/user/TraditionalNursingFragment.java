package com.bsoft.mob.ienr.fragment.user;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.TraditionalNursingActivity;
import com.bsoft.mob.ienr.activity.user.TraditionalNursingQueryActivity;
import com.bsoft.mob.ienr.adapter.TradListAdapter;
import com.bsoft.mob.ienr.api.TradApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.trad.TradBean;
import com.bsoft.mob.ienr.model.trad.Traditional_ZZFJ;
import com.bsoft.mob.ienr.model.trad.Traditional_ZZJL;
import com.bsoft.mob.ienr.model.trad.ZZJL_PF;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.classichu.adapter.listview.ClassicBaseAdapter;
import com.classichu.adapter.listview.ClassicBaseViewHolder;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.ui.ClassicDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 中医传统护理
 */
public class TraditionalNursingFragment extends BaseUserFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public TraditionalNursingFragment() {
        // Required empty public constructor
    }

    private TextView id_tv_zyh;
    private TextView id_tv_sex;
    private TextView id_tv_age;
    private TextView id_tv_zyzd;
    private TextView id_tv_hlfa;
    private TextView id_tv_zt;
    private TextView id_tv_pj;
    private TextView id_tv_kssj;
    private TextView id_tv_jssj;

    public static TraditionalNursingFragment newInstance(String param1, String param2) {
        TraditionalNursingFragment fragment = new TraditionalNursingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ExpandableListView id_elv;
    private TradListAdapter mTradListAdapter;

    private void onMenuItemClick(int drawableRes) {


        if (drawableRes == R.drawable.menu_create) {
            // addProblem();
            Intent intent = new Intent(mContext, TraditionalNursingActivity.class);
            intent.putExtra("isAllCanEdit", isAllCanEdit());
            startActivity(intent);
        } else if (drawableRes == R.drawable.menu_view) {
            // saveProblem();
            Intent intent = new Intent(mContext, TraditionalNursingQueryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {
                R.drawable.menu_create, R.drawable.menu_view};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_view, R.string.comm_menu_view}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : itemDrawables) {
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

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_traditional_nursing;
    }

    TextView id_tv_2_for_bar_image;
    LinearLayout id_user_info;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        id_elv = view.findViewById(R.id.id_elv);
        id_elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;//直接消费掉
            }
        });
        EmptyViewHelper.setEmptyView(id_elv, "id_elv");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, id_elv);

        id_tv_2_for_bar_image = view.findViewById(R.id.id_tv_2_for_bar_image);
        //时间
        id_tv_2_for_bar_image.setText(DateTimeHelper.getServerDateTime());

        id_tv_zyh = view.findViewById(R.id.id_tv_zyh);
        id_tv_sex = view.findViewById(R.id.id_tv_sex);
        id_tv_age = view.findViewById(R.id.id_tv_age);
        id_tv_hlfa = view.findViewById(R.id.id_tv_hlfa);
        id_tv_zyzd = view.findViewById(R.id.id_tv_zyzd);
        id_tv_zt = view.findViewById(R.id.id_tv_zt);
        id_tv_pj = view.findViewById(R.id.id_tv_pj);
        id_tv_kssj = view.findViewById(R.id.id_tv_kssj);
        id_tv_jssj = view.findViewById(R.id.id_tv_jssj);

        CheckBox id_cb = view.findViewById(R.id.id_cb);
        id_user_info = view.findViewById(R.id.id_user_info);
        id_user_info.setVisibility(View.GONE);
        Drawable btnDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.selector_classic_icon_up_down);
        id_cb.setButtonDrawable(btnDrawable);
        id_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    id_user_info.setVisibility(View.VISIBLE);
                } else {
                    id_user_info.setVisibility(View.GONE);
                }
            }
        });

        ImageView id_iv_for_bar_image = view.findViewById(R.id.id_iv_for_bar_image);
        id_iv_for_bar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllCanEdit()) {
                    setTime();
                }
            }
        });
        List<TradBean> tradBeanList = new ArrayList<>();
        mTradListAdapter = new TradListAdapter(mContext, tradBeanList);
        mTradListAdapter.setClickListener(new TradListAdapter.ClickListener() {
            @Override
            public void edit(View view, int groupPosition) {
                if (isAllCanEdit()) {
                    String zzbh = mTradListAdapter.getGroup(groupPosition).code;
                    actionGetZZFJ(zzbh);
                } else {
                    showMsgAndVoiceAndVibrator("方案已审核或已结束，不可进行当前操作");
                }

            }
        });
        id_elv.setAdapter(mTradListAdapter);

        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        HttpTask ht = new HttpTask();
        tasks.add(ht);
        ht.execute();
    }

    private void actionGetZZFJ(String zzbh) {
        GetZZFJTask ht = new GetZZFJTask();
        tasks.add(ht);
        ht.execute(zzbh);
    }


    class GetZZFJTask extends AsyncTask<String, Integer, Response<List<Traditional_ZZFJ>>> {


        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<Traditional_ZZFJ>> doInBackground(String... params) {
            String zzbh = params[0];
            TradApi api = TradApi.getInstance(getActivity());
            return api.getZZFJList(zzbh);
        }

        @Override
        protected void onPostExecute(Response<List<Traditional_ZZFJ>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 0) {
//
                    showSelectList(result.Data);

                } else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }

    private MyAdapter mMyAdapter;
    private ClassicDialogFragment listDialog;

    private void showSelectList(List<Traditional_ZZFJ> zzfjList) {
        ListView listview = new ListView(mContext);
        listview.setBackgroundResource(R.drawable.inset_dialog_bg);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //adapterView.getAdapter().getItem(pos); 针对headerview的list也正确
                Traditional_ZZFJ zzfj = (Traditional_ZZFJ) adapterView.getAdapter().getItem(pos);
                editSaveNewFJ(zzfj);
                //
                hideListDialog();
            }
        });
        mMyAdapter = new MyAdapter(zzfjList, R.layout.item_list_text_three_secondary);
        View headView = LayoutInflater.from(mContext).inflate(
                R.layout.item_list_text_three_primary, null, false);
        TextView tv = headView.findViewById(R.id.id_tv_one);
        tv.setText("症状分级");
        TextView id_tv_2 = headView.findViewById(R.id.id_tv_two);
        id_tv_2.setText("分值");
        TextView id_tv_3 = headView.findViewById(R.id.id_tv_three);
        id_tv_3.setText("备注信息");
        listview.addHeaderView(headView);
        listview.setAdapter(mMyAdapter);
        //
        hideListDialog();
        listDialog = new ClassicDialogFragment.Builder(mContext)
                .setContentView(listview)
                .setCustomTitleView(ViewBuildHelper.buildDialogTitleTextView(mContext, "症状分级评估"))
                .setCancelText(getString(R.string.project_operate_cancel))
                .setOnBtnClickListener(new OnBtnClickListener() {
                    @Override
                    public void onBtnClickCancel(DialogInterface dialogInterface) {

                    }
                })
                .build();
        listDialog.show(getChildFragmentManager(), "listDialog");
    }

    private void hideListDialog() {
        if (listDialog != null) {
            listDialog.dismissAllowingStateLoss();
            listDialog = null;
        }
    }

    private String json = "";

    private void editSaveNewFJ(Traditional_ZZFJ zzfj) {
        //  showMsgAndVoice("dasdas"+zzfj);

        ZZJL_PF zzjl_pf = new ZZJL_PF();
        zzjl_pf.zzjl = nowZZJL;
        zzjl_pf.zzfj = zzfj.FJBH;
        zzjl_pf.fjpf = zzfj.FJFZ;
        zzjl_pf.pfgh = mAppApplication.user.YHID;
        zzjl_pf.pfsj = id_tv_2_for_bar_image.getText().toString();
        zzjl_pf.zzbh = zzfj.ZZBH;
        zzjl_pf.fajl = nowFAJL;
        zzjl_pf.ssqpf = nowSSQPF;
        try {
            json = JsonUtil.toJson(zzjl_pf);
            //
            actionHttpSaveTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actionHttpSaveTask() {
        HttpSaveTask httpSaveTask = new HttpSaveTask();
        tasks.add(httpSaveTask);
        httpSaveTask.execute(json);
    }

    class HttpSaveTask extends AsyncTask<String, Integer, Response<String>> {


        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            TradApi api = TradApi.getInstance(getActivity());

            String json = params[0];
            return api.saveZYPJInfo(json);
        }

        @Override
        protected void onPostExecute(Response<String> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionHttpSaveTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    //保存
                    showMsgAndVoice(result.Msg);
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    class HttpTask extends AsyncTask<String, Integer, Response<List<Traditional_ZZJL>>> {


        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<Traditional_ZZJL>> doInBackground(String... params) {

            TradApi api = TradApi.getInstance(getActivity());
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            return api.getZYZZList(zyh, brbq, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<Traditional_ZZJL>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {

                    parseData(result.Data);
                    //##  mTradListAdapter.

                } else {
                    showMsgAndVoice(result.Msg);
               /*     MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private String nowZZJL;
    private String nowFAJL;
    private String nowSSQPF;
    private String nowJLZT;

    public boolean isAllCanEdit() {
        return "2".equals(nowJLZT) || "3".equals(nowJLZT);
    }

    private void parseData(List<Traditional_ZZJL> zyzzList) {
        if (zyzzList == null || zyzzList.isEmpty()) {
            return;
        }
        nowZZJL = zyzzList.get(0).ZZJL;
        nowFAJL = zyzzList.get(0).FAJL;
        nowSSQPF = zyzzList.get(0).SSQPF;
        nowJLZT = zyzzList.get(0).JLZT;
        //
        id_tv_zyh.setText(zyzzList.get(0).ZYHM);
        id_tv_age.setText(zyzzList.get(0).BRNL);
        String sex = "未知";
        if (mAppApplication.sickPersonVo.BRXB == 1) {
            sex = "男";
        } else if (mAppApplication.sickPersonVo.BRXB == 2) {
            sex = "女";
        }
        id_tv_sex.setText(sex);
        id_tv_hlfa.setText(zyzzList.get(0).FAMC);
        id_tv_pj.setText(zyzzList.get(0).FAPJMC);
        id_tv_pj.setTextColor("4".equals(zyzzList.get(0).FAPJ) ? Color.RED : ContextCompat.getColor(mContext, R.color.textColorSecondary));

        id_tv_zt.setText(zyzzList.get(0).JLZTMC);
        id_tv_kssj.setText(zyzzList.get(0).KSSJ);
        id_tv_jssj.setText(zyzzList.get(0).JSSJ);


        String kssj = zyzzList.get(0).KSSJ;
        if (!EmptyTool.isBlank(kssj)) {
            if (kssj.length() > 19) {
                kssj = DateTimeTool.custom2DateTime(kssj, DateTimeFormat.yyyy_MM_dd_HHmmss_S);
            }
            id_tv_kssj.setText(kssj);
        }
        String jssj = zyzzList.get(0).JSSJ;
        if (!EmptyTool.isBlank(jssj)) {
            if (jssj.length() > 19) {
                jssj = DateTimeTool.custom2DateTime(jssj, DateTimeFormat.yyyy_MM_dd_HHmmss_S);
            }
            id_tv_jssj.setText(jssj);
        }
        id_tv_zyzd.setText(zyzzList.get(0).ZYZDMC);
        //
        List<TradBean> tradBeanList = new ArrayList<>();
        for (int i = 0; i < zyzzList.size(); i++) {
            TradBean tb = new TradBean();
            tb.code = zyzzList.get(i).ZZBH;
            tb.name = zyzzList.get(i).ZZMC;
            tb.name2 = zyzzList.get(i).HLXG;
            tb.name3 = zyzzList.get(i).HLXGMC;
            TradBean.TradChild tc = new TradBean.TradChild();
            tc.name = zyzzList.get(i).SSQPF;
            tc.name2 = zyzzList.get(i).SSQSJ;
            tc.name3 = zyzzList.get(i).SSQFJMC;
            tc.name4 = zyzzList.get(i).SSHPF;
            tc.name5 = zyzzList.get(i).SSHSJ;
            tc.name6 = zyzzList.get(i).SSHFJMC;
            tb.tradChildList = new ArrayList<>();
            tb.tradChildList.add(tc);
            tradBeanList.add(tb);
        }
        if (EmptyTool.isEmpty(tradBeanList)) {
            tradBeanList = new ArrayList<>();
            //
            TestDataHelper.buidTestData(TradBean.class, tradBeanList);
        }
        mTradListAdapter.refreshData(tradBeanList);
        //默认展开所有
        for (int i = 0; i < mTradListAdapter.getGroupCount(); i++) {
            id_elv.expandGroup(i);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionBar.setTitle("中医护理");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
    }

    private void setTime() {
        String time = id_tv_2_for_bar_image.getText().toString();
        showDateTimePickerCompat(time, R.id.id_tv_2_for_bar_image);
    }

    public class MyAdapter extends ClassicBaseAdapter<Traditional_ZZFJ> {

        public MyAdapter(List<Traditional_ZZFJ> dataList, int itemLayoutId) {
            super(dataList, itemLayoutId);
        }

        @Override
        public void findBindView(int pos, ClassicBaseViewHolder holder) {
            TextView id_tv_one = holder.findBindItemView(R.id.id_tv_one);
            TextView id_tv_two = holder.findBindItemView(R.id.id_tv_two);
            TextView id_tv_three = holder.findBindItemView(R.id.id_tv_three);
            id_tv_one.setText(mDataList.get(pos).FJMC);
            id_tv_two.setText(mDataList.get(pos).FJFZ);
            id_tv_three.setText(mDataList.get(pos).BZXX);
        }
    }

}
