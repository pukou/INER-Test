/**
 * @Title: NurseEvaluateTemplateFragment.java
 * @Package com.bsoft.mob.ienr.fragment.user
 * @Description: 护理评估模板页
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-4-7 下午12:09:09
 * @version V1.0
 */
package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NursingEvaluateActivity;
import com.bsoft.mob.ienr.api.EvaluateApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecord;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateStyte;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

/**
 * 护理评估新模板
 */
public class NursingEvaluateTemplateFragment extends BaseUserFragment {



    private ListView listView;

    public static final String EXTRE_INT_START_TYPE = "start_type";

    /**
     * 获取记录列表类型
     */
    public static final int TYPE_RECORD_FORM = 1;

    /**
     * 获取样式列表类型
     */
    public static final int TYPE_FORM = 0;

    private int mCurrType = TYPE_FORM;

   private  View sltStimeView;
   private  View sltEtimeView;

   private  TextView stime;
   private  TextView etime;

   private ImageView searchBtn;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_nursing_evaluate_template;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {


        listView = (ListView) rootLayout
                .findViewById(R.id.id_lv);


        EmptyViewHelper.setEmptyView(listView,"listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,listView);
        sltStimeView = rootLayout.findViewById(R.id.slt_stime_ly);
        sltEtimeView = rootLayout.findViewById(R.id.slt_etime_ly);

        stime = (TextView) rootLayout.findViewById(R.id.stime);
        etime = (TextView) rootLayout.findViewById(R.id.etime);

        searchBtn = (ImageView) rootLayout.findViewById(R.id.search);

        initListView();
        initTime();
        initSearchBtn();
        initActionBar();
        rootLayout.findViewById(R.id.id_layout_double_time).setVisibility(
                mCurrType == TYPE_FORM ? View.GONE : View.VISIBLE);

        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        actionFormTask(mCurrType);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBroadCast();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



    void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-6);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }

    public OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int id = v.getId();
            if (id == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (id == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            showDatePickerCompat(dateStr, id);

        }
    };

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            String endDate = etime.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            String startDate = stime.getText().toString();
            boolean before = DateTimeFactory.getInstance().dateBefore(nowDate, startDate);
            if (before) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间，请重新选择!");
                return;
            }
        }
        initTimeTxt(nowDate, viewId);

    }

    ;

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    private void initActionBar() {

        if (mCurrType == TYPE_FORM) {
            actionBar.setTitle("评估单样式列表");
        } else if (mCurrType == TYPE_RECORD_FORM) {
            actionBar.setTitle("评估单记录列表");
        }
         actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);
    }

    private void initListView() {

        listView.setOnItemClickListener(onItemClickListener);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Object object = listView.getAdapter().getItem(position);
            if (object == null) {
                return;
            }
            //add 2017年4月28日17:13:37  每点一次评估单  重新来过
            if (mAppApplication.mEvaluateTempDataBean != null) {
                mAppApplication.mEvaluateTempDataBean.clear();
            }
            if (mAppApplication.mLifeSymptomTempDataBean != null) {
                mAppApplication.mLifeSymptomTempDataBean.clear();
            }
            //add 2017年4月28日17:13:37
            if (object instanceof NursingEvaluateStyte) {
                NursingEvaluateStyte styte = (NursingEvaluateStyte) object;
                Intent intent = new Intent(getActivity(),
                        NursingEvaluateActivity.class);
                intent.putExtra("YSXH", styte.YSXH);
                intent.putExtra("YSLX", styte.YSLX);
                intent.putExtra("BBH", styte.BBH);
                startActivity(intent);
            } else if (object instanceof NursingEvaluateRecord) {
                NursingEvaluateRecord record = (NursingEvaluateRecord) object;
                Intent intent = new Intent(getActivity(),
                        NursingEvaluateActivity.class);
                intent.putExtra("JLXH", record.JLXH);
                intent.putExtra("TXSJ", record.TXSJ);
                startActivity(intent);
            }

        }

    };


    /**
     * 获取指定级别列表项
     *
     * @param params
     */

    protected void actionFormTask(Integer... params) {

        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(params);
    }

    class GetFormTask extends AsyncTask<Integer, String,
            Pair<Response<List<NursingEvaluateStyte>>,Response<List<NursingEvaluateRecord>>>> {

        private int type;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Pair<Response<List<NursingEvaluateStyte>>, Response<List<NursingEvaluateRecord>>> doInBackground(Integer... params) {

            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String jgid = mAppApplication.jgId;

            EvaluateApi api = EvaluateApi.getInstance(getActivity());

            type = params[0];
            Pair<Response<List<NursingEvaluateStyte>>, Response<List<NursingEvaluateRecord>>> pair = null;
            if (type == TYPE_RECORD_FORM) {
                String zyh = mAppApplication.sickPersonVo.ZYH;
                Response<List<NursingEvaluateStyte>> response = null;
               /* Response<List<EvaluateRecordItem>> response = api.GetEvaluationList(start, end, zyh,
                        jgid, Config.sysType);*/
                pair = Pair.create(response, null);
                return pair;
            } else if (type == TYPE_FORM) {
                String bqdm = mAppApplication.getAreaId();
                String zyh = mAppApplication.sickPersonVo.ZYH;
                String yslx = "";
                Bundle bundle = getArguments();
                if (bundle != null) {
                    yslx = bundle.getString("yslx");
                }
                Response<List<NursingEvaluateStyte>> response = null;
                if (EmptyTool.isBlank(yslx)) {
                    response = api.GetNursingEvaluationStyleList_V56Update1(zyh, bqdm, jgid);
                } else {
                 /*   response = api.GetNewEvaluationListForYslx(yslx, bqdm, jgid,
                            Config.sysType);*/
                }
                pair = Pair.create(response, null);
                return pair;
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Pair<Response<List<NursingEvaluateStyte>>, Response<List<NursingEvaluateRecord>>> resultPair) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (resultPair == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (resultPair.first != null) {
                Response<List<NursingEvaluateStyte>> result = resultPair.first;
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    List<NursingEvaluateStyte> list = result.Data;
                    importList(list);
                } else {   showTipDialog(result.Msg);
//                    AlertBox.Show(mContext, getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
                }
            } else {
           /*     Response<List<NursingEvaluateStyte>> result=resultPair.second;
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), application, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                             toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    List<NursingEvaluateStyte> list = result.Data;
                    importList(list);
                } else {
                    AlertBox.Show(xContext, getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
                }*/
            }


        }
    }

    class ListAdapter extends BaseAdapter {

        private List<NursingEvaluateStyte> list;

        private Context mContext;

        public ListAdapter(Context context, List<NursingEvaluateStyte> _list) {
            this.list = _list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public NursingEvaluateStyte getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_text_one_primary_icon,  parent,false);

                vHolder = new ViewHolder();

                vHolder.nameView = (TextView) convertView
                        .findViewById(R.id.name);
              /*  vHolder.timeView = (TextView) convertView
                        .findViewById(R.id.time);*/

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }

            NursingEvaluateStyte vo = list.get(position);
            vHolder.nameView.setText(vo.YSMC);

          /*  String timeStr = null;
            if (!EmptyTool.isBlank(vo.)) {
                timeStr = "填写时间：" + DateUtil.getDate(DateUtil.format_yyyyMMdd_HHmm, vo.TXSJ);
            }
            vHolder.timeView.setText(timeStr);*/
           // vHolder.timeView.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder {
            public TextView nameView;
            public TextView timeView;
        }

    }


    public void importList(List<NursingEvaluateStyte> list) {

        ListAdapter adapter = new ListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }

    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                }
            }
        };
    }

}
