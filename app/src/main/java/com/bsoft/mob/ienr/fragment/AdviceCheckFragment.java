package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.AdviceCheckDetailActivity;
import com.bsoft.mob.ienr.adapter.AdviceCheckAdpter;
import com.bsoft.mob.ienr.adapter.MyPagerAdapter;
import com.bsoft.mob.ienr.api.AdviceCheckApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advicecheck.AdviceCheckList;
import com.bsoft.mob.ienr.model.advicecheck.AdviceCheckParams;
import com.bsoft.mob.ienr.model.advicecheck.AdviceForm;
import com.bsoft.mob.ienr.model.advicecheck.CheckDetail;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.AsyncTaskUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 药品核对（加药核对，摆药核对）
 *
 * @author 自聪
 * <p>
 * 2015-5-25
 */
public class AdviceCheckFragment extends LeftMenuItemFragment {

    public static int REQUEST_OPEN = 1;
    public static int RESPONSE_OK = 2;
    private LinearLayout ll_modecontainer;
    private View slt_plandate;// 计划日期选择
    private TextView tv_plandate;// 计划日期显示控件
    private ImageView btn_search;// 刷新按钮
    private TextView tv_jy, tv_by;// 加药 摆药
    private ListView list_by;// 摆药核对列表
    private ListView list_jy;// 加药核对列表

    private CheckBox cb_direction;// 开启和关闭用药途径选择框的选择按钮
    private RadioGroup status_group, direction_group;// 核对类型，用药途径
    private RadioButton rb_all, rb_transfusion, rb_injection;

    protected LinkedList<AsyncTask<?, ?, ?>> tasks = new LinkedList<AsyncTask<?, ?, ?>>();

    private AdviceCheckParams adviceCheckParams = null;

    private AdviceCheckAdpter adviceCheckAdpter = null;

    private String type = "1";// 1:摆药；2：加药
    private String gslx = "-1";// -1全部；4：输液；5：注射
    private String status = "0";// 0未核对；1已核对
    protected ArrayList<AdviceForm> list;
    private BarcodeEntity entity;
    private ViewPager viewPager;
    private AdviceForm curForm = null;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_advice_check;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        // 初始化界面控件
        initView(rootLayout);
        actionBar.setTitle("药品核对");
        initBroadCast();
        toRefreshData();
    }


    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {
                    entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    Intent it = new Intent(getActivity(),
                            AdviceCheckDetailActivity.class);
                    boolean exist = false;
                    it.putExtra("MODE", type);
                    it.putExtra("isDispend",
                            adviceCheckParams.IsDispensingCheck);
                    for (AdviceForm form : list) {
                        if (form.TMBH.equals(entity.source)) {
                            exist = true;
                            curForm = form;
                            it.putExtra("FORM", form);
                            break;
                        }
                    }
                    if (adviceCheckParams.IsSimpleMode.equals("0")) {
                        if (entity.TMFL == 2) {
                            if (entity.FLBS == 4 || entity.FLBS == 5) {
                                CheckTask task = new CheckTask();
                                tasks.add(task);
                                task.execute();
                            } else {
                                showMsgAndVoiceAndVibrator("条码不正确");
                               /* MediaUtil.getInstance(getActivity()).playSound(
                                        R.raw.wrong, getActivity());*/
                            }
                        }
                    } else {
                        if (exist) {
                            startActivityForResult(it, REQUEST_OPEN);
                        } else {
                            showMsgAndVoiceAndVibrator("未核对列表中不存在改条码对应的瓶贴，是否已经核对过了？");
                            /*MediaUtil.getInstance(getActivity()).playSound(
                                    R.raw.wrong, getActivity());*/
                            return;
                        }
                    }
                }
            }
        };

    }

    @Override
    protected void toRefreshData() {
        String jhrq = tv_plandate.getText().toString().trim();
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute(jhrq, gslx, status, type);
    }

    private void initView(View root) {
        ll_modecontainer = (LinearLayout) root
                .findViewById(R.id.id_ll_container);
        slt_plandate = (View) root.findViewById(R.id.slt_plantime);
        tv_plandate = (TextView) root.findViewById(R.id.plantime);
        btn_search = (ImageView) root.findViewById(R.id.search);


        tv_jy = (TextView) root.findViewById(R.id.id_tv);
        tv_jy.setText("摆药");

        tv_by = (TextView) root.findViewById(R.id.id_tv_2);
        tv_by.setText("加药");

      /*  id_rb_status_1_advice_check = (RadioButton) root.findViewById(R.id.id_rb_status_1_advice_check);
        id_rb_status_2_advice_check = (RadioButton) root.findViewById(R.id.id_rb_status_2_advice_check);
*/
        rb_all = (RadioButton) root.findViewById(R.id.id_rb);
        rb_all.setText("全部");
        rb_transfusion = (RadioButton) root.findViewById(R.id.id_rb_2);
        rb_transfusion.setText("输液");
        rb_injection = (RadioButton) root.findViewById(R.id.id_rb_3);
        rb_injection.setText("注射");

        //
        viewPager = (ViewPager) root.findViewById(R.id.id_vp);
        List<View> viewList = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        list_jy = view.findViewById(R.id.id_lv);
        list_by = view2.findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(list_jy, "list_jy");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, list_jy);
        EmptyViewHelper.setEmptyView(list_by, "list_by");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, list_by);
        viewList.add(view);
        viewList.add(view2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(myPagerAdapter);


        cb_direction = (CheckBox) root.findViewById(R.id.id_cb);
        Drawable btnDrawable = ContextCompatHelper.getDrawable(root.getContext(), R.drawable.selector_classic_icon_up_down);
        cb_direction.setButtonDrawable(btnDrawable);
        status_group = (RadioGroup) root.findViewById(R.id.id_rg_status_advice_check);
        direction_group = (RadioGroup) root.findViewById(R.id.id_rg);
        direction_group.setVisibility(View.GONE);
        String date = DateTimeHelper.getServerDate();
        tv_plandate.setText(date);
        // 显示下拉刷新图标

        // checked/activated
        list_by.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);
        // checked/activated
        list_jy.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);

        //###list_by.setVisibility(View.GONE);// 默认显示待核对的项目
        changeStatusView();
        // 显示和和隐藏相关的控件
        tv_jy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                type = "1";
                changeStatusView();
                toRefreshData();

            }
        });
        tv_by.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                type = "2";
                changeStatusView();
                toRefreshData();
            }
        });
        // 选择日期
        slt_plandate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int id = v.getId();

                showDatePickerCompat(DateTimeHelper.getServerDate(), id);


             /*   int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(
                        getActivity(), new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        monthOfYear += 1;
                        String strTime = year
                                + "-"
                                + (monthOfYear < 10 ? "0" + monthOfYear
                                : monthOfYear)
                                + "-"
                                + (dayOfMonth < 10 ? "0" + dayOfMonth
                                : dayOfMonth) + " ";
                        tv_plandate.setText(strTime);
                    }
                }, mYear, mMonth, mDay);
                dateDialog.setTitle("请选择日期");
                dateDialog.show();*/
            }
        });
        // 显示和隐藏用药途径顾虑条件
        cb_direction
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            direction_group.setVisibility(View.VISIBLE);
                        } else {
                            direction_group.setVisibility(View.GONE);
                        }
                    }
                });
        btn_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
        status_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.id_rb_status_2_advice_check) {
                    status = "1";
                } else if (checkedId == R.id.id_rb_status_1_advice_check) {
                    status = "0";
                }
                toRefreshData();
            }
        });
        direction_group
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.id_rb) {
                            gslx = "-1";
                        } else if (checkedId == R.id.id_rb_2) {
                            gslx = "4";
                        } else if (checkedId == R.id.id_rb_3) {
                            gslx = "5";
                        }
                        toRefreshData();
                    }
                });


        list_by.setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        curForm = (AdviceForm) parent.getAdapter().getItem(position);
                        Intent intent = new Intent(getActivity(),
                                AdviceCheckDetailActivity.class);
                        intent.putExtra("MODE", type);
                        intent.putExtra("FORM", curForm);
                        intent.putExtra("isDispend",
                                adviceCheckParams.IsDispensingCheck);
                        startActivityForResult(intent, REQUEST_OPEN);
                    }
                });
        list_jy.setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        curForm = (AdviceForm) parent.getAdapter().getItem(position);
                        Intent intent = new Intent(getActivity(),
                                AdviceCheckDetailActivity.class);
                        intent.putExtra("MODE", type);
                        intent.putExtra("FORM", curForm);
                        intent.putExtra("isDispend",
                                adviceCheckParams.IsDispensingCheck);
                        startActivityForResult(intent, REQUEST_OPEN);
                    }
                });
    }

    private void changeStatusView() {
        if ("2".equals(type)) {
            tv_jy.setSelected(false);
            tv_by.setSelected(true);
            viewPager.setCurrentItem(1);
        } else {
            tv_jy.setSelected(true);
            tv_by.setSelected(false);
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN && resultCode == RESPONSE_OK) {
            list.remove(curForm);
            curForm = null;
            adviceCheckAdpter = new AdviceCheckAdpter(list, type, getActivity());
            list_by.setAdapter(adviceCheckAdpter);
            list_jy.setAdapter(adviceCheckAdpter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {
        String date = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);
        tv_plandate.setText(date);
    }


    class GetDataTask extends AsyncTask<String, Void, Response<AdviceCheckList>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<AdviceCheckList> doInBackground(String... params) {
            if (params == null || params.length < 4) {
                return null;
            }
            String jgid = mAppApplication.jgId;
            String bqdm = mAppApplication.getAreaId();
            return AdviceCheckApi.getInstance(getActivity()).getAdviceFrom(
                    bqdm, params[0], params[1], params[2], params[3], jgid);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<AdviceCheckList> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            adviceCheckAdpter = null;
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                list = (ArrayList<AdviceForm>) result.Data.adviceForm;
                adviceCheckParams = result.Data.adviceCheckParams;
                if (adviceCheckParams.IsDispensingCheck.equals("0")) {
                    ll_modecontainer.setVisibility(View.GONE);
                }else{
                    ll_modecontainer.setVisibility(View.VISIBLE);
                }
                if (EmptyTool.isEmpty(list)) {
                    list = new ArrayList<>();
                    TestDataHelper.buidTestData(AdviceForm.class, list);
                    // toastInfo("暂无数据", Style.INFO, R.id.actionbar);
                }
                adviceCheckAdpter = new AdviceCheckAdpter(list, type,
                        getActivity());
                list_by.setAdapter(adviceCheckAdpter);
                list_jy.setAdapter(adviceCheckAdpter);
            } else {
                showMsgAndVoice(result.Msg);
                return;
            }
        }
    }

    class CheckTask extends AsyncTask<Void, Void, Response<CheckDetail>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<CheckDetail> doInBackground(Void... params) {
            String jgid = mAppApplication.jgId;
            String userId = mAppApplication.user.YHID;
            return AdviceCheckApi.getInstance(getActivity()).scanExecute(
                    entity.TMNR, entity.TMQZ, userId, type, jgid);

        }

        @Override
        protected void onPostExecute(Response<CheckDetail> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                list.remove(curForm);
                curForm = null;
                if (!EmptyTool.isBlank(result.Msg)) {
                    showMsgAndVoice(result.Msg);
                } else {
                    showMsgAndVoice("操作成功");

                }
                // 通知界面 start01 modify 2015-11-26 吕自聪
                adviceCheckAdpter.notifyDataSetChanged();
                // 通知界面 end01
              /*  MediaUtil.getInstance(getActivity()).playSound(R.raw.success,
                        getActivity());*/
            } else {
                if (!EmptyTool.isBlank(result.Msg)) {
                    showMsgAndVoice(result.Msg);
                } else {
                    showMsgAndVoiceAndVibrator("操作失败");

                }
              /*  MediaUtil.getInstance(getActivity()).playSound(R.raw.wrong,
                        getActivity());*/
            }
        }

    }

    @Override
    public void onDestroy() {

        for (AsyncTask<?, ?, ?> task : tasks) {
            AsyncTaskUtil.cancelTask(task);
        }
        super.onDestroy();
    }


   /* public void toastInfo(String msg, Style style, int viewGroupId) {
        //## Crouton.showText(getActivity(), msg, style, viewGroupId, (new Configuration.Builder()).setDuration(1000).build());
        showSnack(msg);
    }*/

}
