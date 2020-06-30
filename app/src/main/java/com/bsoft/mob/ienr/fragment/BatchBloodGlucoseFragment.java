package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.MainActivity;
import com.bsoft.mob.ienr.adapter.DingBaseAdapter;
import com.bsoft.mob.ienr.adapter.PersonAdapter;
import com.bsoft.mob.ienr.api.BloodGlucoseApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodglucose.BGSavePostData;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseDetail;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseRecord;
import com.bsoft.mob.ienr.model.bloodglucose.GlucoseTime;
import com.bsoft.mob.ienr.model.bloodglucose.GlucoseTimeData;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ding.pengqiang  批量 血糖治疗
 * on 2016/12/23.
 */
@Deprecated //废弃
public class BatchBloodGlucoseFragment extends LeftMenuItemFragment {

     // 标题工具栏
    private ListView mPersonListView;
    private LinearLayout addView;
    private PersonAdapter mAdatper;
    private Spinner mSpinnerTime;

    //血糖按钮
    private Button mBtn1;
    private ImageView mImg1;

    //胰岛素按钮
    private Button mBtn2;
    private ImageView mImg2;

    // 当前选择的模块，刷新时使用
    int current = 1;

    private ListView mListView1;
    private ListView mListView2;

    //当天日期
    private String eTimeStr;

    //选中的时间点
    private String xmxh ;
	// 选中的内容  1 血糖  2 胰岛素
	private String xmlx;

    private ArrayAdapter<String> adapter;
    private GlucoseAdapter adapter1;
    private GlucoseCompleteAdapter adapter2;
    private InsulinAdapter adapter3;
    private InsulinCompleteAdapter adapter4;


//    private LinearLayout layout;
//    private LinearLayout mTemporary;
//    private EditText mInput;
//    private TextView mUnit;

    private BloodGlucoseDetail detail ;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_batch_blood_glucose;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mBtn1 = ((Button) rootView.findViewById(R.id.glucose_but1));
        mBtn2 = ((Button) rootView.findViewById(R.id.glucose_but2));
        mImg1 = ((ImageView)rootView. findViewById(R.id.glucose_img1));
        mImg2 = ((ImageView) rootView.findViewById(R.id.glucose_img2));

        SpinnerLayout spinnerLayout = ((SpinnerLayout) rootView.findViewById(R.id.id_spinner_layout));
        mSpinnerTime = spinnerLayout.getSpinner();

        mPersonListView = (ListView) rootView
                .findViewById(R.id.batch_blood_glucose_person_list);

        EmptyViewHelper.setEmptyView(mPersonListView,"mPersonListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mPersonListView);
//        layout = ((LinearLayout) rootView.findViewById(R.id.batch_blood_glucose_view));
//        mTemporary = (LinearLayout)rootView.findViewById(R.id.batch_blood_glucose_temporary);
//        mInput = (EditText)rootView.findViewById(R.id.batch_glucose_input);
//        mUnit = (TextView)rootView.findViewById(R.id.batch_glucose_unit);
        mListView1 = ((ListView) rootView.findViewById(R.id.id_lv));
        mListView2 = ((ListView) rootView.findViewById(R.id.id_lv_2));

        // 初始化标题工具栏
        initActionbar();
        initButtons();
        initTime();
        initListView();

//        toRefreshData();
    }

    /**
     * 设置ActionBar
     */
    private void initActionbar() {
        actionBar.setTitle("血糖监测");
        actionBar.setBackAction(new BsoftActionBar.Action() {
            @Override
            public void performAction(View view) {
                ((MainActivity) getActivity()).toggle();
            }
            @Override
            public String getText() {
                return "≡菜单";
            }
            @Override
            public int getDrawable() {
                return R.drawable.ic_menu_black_24dp;
            }
        });
        actionBar.addAction(new BsoftActionBar.Action() {

            @Override
            public void performAction(View view) {
                saveData();
            }
            @Deprecated
            public String getText() {
                return "保存";
            }
            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }


    /**
     * 切换两个按钮加载不同的数据
     */
    private void initButtons() {
        mBtn1.setSelected(true);
	    xmlx = "1";

        if (mBtn1.isSelected()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    actionGetTimesTask();
                }
            }, 100);

        }

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 1) {
                    current = 1;
	                xmlx = "1";

                    actionGetTimesTask();
                    mListView1.setVisibility(View.INVISIBLE);
                    mListView1.setVisibility(View.INVISIBLE);
                    mBtn1.setSelected(true);
                    mBtn2.setSelected(false);
                    mImg1.setVisibility(View.VISIBLE);
                    mImg2.setVisibility(View.INVISIBLE);
                    mBtn1.setTextColor(getResources().getColor(
                            R.color.colorPrimaryDark));
                    mBtn2.setTextColor(getResources().getColor(
                            android.R.color.black));
                }
            }
        });
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 2) {
                    current = 2;
	                xmlx = "2";

                    actionGetTimesTask();
                    mListView1.setVisibility(View.INVISIBLE);
                    mListView1.setVisibility(View.INVISIBLE);

                    mBtn2.setSelected(true);
                    mBtn1.setSelected(false);
                    mImg2.setVisibility(View.VISIBLE);
                    mImg1.setVisibility(View.INVISIBLE);
                    mBtn2.setTextColor(getResources().getColor(
                            R.color.colorPrimaryDark));
                    mBtn1.setTextColor(getResources().getColor(
                            android.R.color.black));
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView1.setVisibility(View.VISIBLE);
                mListView2.setVisibility(View.VISIBLE);
            }
        }, 1000);


    }
    /**
     * 设置当天时间
     */
    private void initTime(){
        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        eTimeStr = nowDate;

    }
    // 获取时间列表
    private void actionGetTimesTask() {
        GetTimesTask task = new GetTimesTask();
        tasks.add(task);
        task.execute();
    }
    /**
     * 加载时间过滤列
     */
    private class GetTimesTask extends AsyncTask<Void, Void, Response<GlucoseTimeData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showSwipeRefreshLayout();
        }

        @Override
        protected Response<GlucoseTimeData> doInBackground(Void... params) {

            return BloodGlucoseApi.getInstance(getActivity()).getGlucoseTimes();
        }

        @Override
        protected void onPostExecute(Response<GlucoseTimeData> result) {

//            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionGetTimesTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    GlucoseTimeData timeData = result.Data;
                    //血糖的时间点
                    ArrayList<GlucoseTime> glucosetime = (ArrayList<GlucoseTime>) timeData.GLUCOSETIME;
                    //胰岛素的时间点
                    ArrayList<GlucoseTime> insulintime = (ArrayList<GlucoseTime>) timeData.INSULINTIME;
                    if (mBtn1.isSelected()){
                        importTimeList(glucosetime);
                    }else if (mBtn2.isSelected()){
                        importTimeList(insulintime);
                    }

                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("加载时间列表失败");
                return;
            }
        }
    }


    public void importTimeList(final ArrayList<GlucoseTime> list) {

        if (list == null) {
            return;
        }
        ArrayList<String> time= new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String xmnr = list.get(i).XMNR;
            time.add(xmnr);
        }
        adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item,time);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTime.setAdapter(adapter);
        mSpinnerTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xmxh = list.get(position).XMXH;

                toRefreshData();
                mListView1.setAdapter(new GlucoseAdapter(getActivity(),null,R.layout.item_list_batch_glucose));
                mListView2.setAdapter(new InsulinCompleteAdapter(getActivity(),null,R.layout.item_list_batch_insulin_complete));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        actionGetTask();
    }

    /**
     * 病人列表异步加载
     */
    private class GetPListTask extends AsyncTask<Integer, Void, Response<ArrayList<SickPersonVo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ArrayList<SickPersonVo>> doInBackground(Integer... params) {

            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            String areaId = mAppApplication.getAreaId();

            String jgid = mAppApplication.jgId;

            return BloodGlucoseApi.getInstance(getActivity()).GetPatientList(areaId,
                    eTimeStr, xmlx, xmxh, null, jgid);
        }

        @Override
        protected void onPostExecute(Response<ArrayList<SickPersonVo>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);


            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                }
                if (result.ReType == 0) {
                    @SuppressWarnings("unchecked")
                    ArrayList<SickPersonVo> list = result.Data;
                    importPersons(list);

                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
        }
    }

    /**
     * 绑定病人数据源的适配器
     * @param list
     */
    private void importPersons(ArrayList<SickPersonVo> list) {

        mAdatper = new PersonAdapter(getActivity(), list);
        mPersonListView.setAdapter(mAdatper);


    }

    /**
     * 给PullToRefresh设置刷新，点击事件
     */
    private void initListView() {

        mPersonListView.setTextFilterEnabled(true);
        // checked/activated
        mPersonListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);

        mPersonListView.setOnItemClickListener(onPersonItemClickListener);

    }

    @Override
    protected void toRefreshData() {
        long nowTime = DateTimeHelper.getServerDateTimeInMillis();
        int time = (int) nowTime;
        GetPListTask task = new GetPListTask();
        tasks.add(task);
        task.execute(time);
    }

    public AdapterView.OnItemClickListener onPersonItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // prt listview add a head view
            onPItemClick(position);
        }
    };
    private void onPItemClick(int position) {

        mPersonListView.setItemChecked(position, true);
        SickPersonVo person = (SickPersonVo) mPersonListView.getAdapter().getItem(position);
        mAppApplication.sickPersonVo = person;
        if (person == null){
            return;
        }
        performGetForm(person.ZYH);

    }

    private void performGetForm(String zyh) {
        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(zyh);
    }

    class GetFormTask extends
            AsyncTask<String, Void, Response<BloodGlucoseRecord> >{


        String zyh;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
//            addView.removeAllViews();
        }

        @Override
        protected Response<BloodGlucoseRecord> doInBackground(String... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            zyh = params[0];
//            String time = mTime.getText().toString();

            String cxpb = "1";

            zyh = mAppApplication.sickPersonVo.ZYH;
            String time = eTimeStr;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
//
            return BloodGlucoseApi.getInstance(getActivity())
                    .getBGList(xmlx,zyh,eTimeStr,brbq,jgid,xmxh);
        }

        @Override
        protected void onPostExecute(Response<BloodGlucoseRecord> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    performGetForm(zyh);
                                }
                            }).showLoginDialog();

                }else if (result.ReType == 0){

                    BloodGlucoseRecord data = result.Data;
                    mListView1.setVisibility(View.VISIBLE);
                    mListView2.setVisibility(View.VISIBLE);
                    importList(data);

                }else {
                    showMsgAndVoiceAndVibrator("请求错误");
//                    BloodGlucoseRecord data = result.Data;
//                    importList(data);
                /*    MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            }else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }


    }
    private void importList(BloodGlucoseRecord data) {
        List<BloodGlucoseDetail> details = data.DETAILS;
        List<BloodGlucoseDetail> historys = data.HISTORYS;

        if (historys == null){
            return;
        }
        if (mBtn1.isSelected()){

            if (details == null){
                return;
            }
            adapter1 = new GlucoseAdapter(getActivity(), null, R.layout.item_list_batch_glucose);
            mListView1.setAdapter(adapter1);
            adapter1.updateRes(details);

            adapter2 = new GlucoseCompleteAdapter(getActivity(), null, R.layout.item_list_batch_glucose_complete);
            mListView2.setAdapter(adapter2);
            adapter2.updateRes(historys);

        }else if (mBtn2.isSelected()){

            adapter3 = new InsulinAdapter(getActivity(),null,R.layout.item_list_check_batch_insulin);
            mListView1.setAdapter(adapter3);
            adapter3.addRes(details);

            adapter4= new InsulinCompleteAdapter(getActivity(),null,R.layout.item_list_batch_insulin_complete);
            mListView2.setAdapter(adapter4);
            adapter4.addRes(historys);

        }


    }
    private void saveData() {
        int position = mPersonListView.getCheckedItemPosition();

        if (position == AdapterView.INVALID_POSITION) {
          /*  VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }

        SickPersonVo person = (SickPersonVo) mPersonListView.getAdapter().getItem(position);
        if (person == null) {
         /*   VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        performSaveTask(person);
    }

    private void performSaveTask(SickPersonVo person) {
        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute(person);
    }

    class SaveTask extends AsyncTask<SickPersonVo, Void, Response<BloodGlucoseRecord>> {
        private SickPersonVo sickPersonVo;

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.doing);
        }

        @Override
        protected Response<BloodGlucoseRecord> doInBackground(SickPersonVo... arg0) {

            if (arg0 == null || arg0.length < 1 || arg0[0] == null) {
                return null;
            }
            Response<BloodGlucoseRecord> response = new Response<>();
            response.ReType = -2;
            BloodGlucoseRecord record = new BloodGlucoseRecord();

            sickPersonVo = arg0[0];

            String data = "";
            BGSavePostData save = new BGSavePostData();
//            save.JLXH= ;
            save.ZYH = mAppApplication.sickPersonVo.ZYH;
            save.JGID = mAppApplication.jgId;
            save.BRBQ = mAppApplication.getAreaId();
            save.JHRQ = eTimeStr;
            if (mBtn1.isSelected()){

                if (adapter1 != null) {
                    save.XMLX = "1";
                    save.XMXH = adapter1.xmxh;


//                    if (adapter1.editText == null){
//                        response.Msg = "没有数据可保存！";
//                        return response;
//                    }

                    if (EmptyTool.isBlank(adapter1.editText.getText().toString())) {
                        response.Msg = "请填写数据后再保存！";
                        return response;
                    }

                    detail.JHNR = adapter1.editText.getText().toString();
                    detail.JLGH = mAppApplication.user.YHID;
                }
            }else if (mBtn2.isSelected()){

                save.XMLX = "2";
                save.XMXH = adapter3.xmxh;
                if (adapter3.title == null){
                    response.Msg = "没有数据可保存！";
                    return response;
                }
                if (adapter3.title.isChecked() == true) {
                    detail.ZTBZ = "1";

                    detail.JLXM = mAppApplication.user.YHXM;
                    detail.JLGH = mAppApplication.user.YHID;
                }else {
                    response.Msg = "请选择后再保存！";
                    return response;
                }
            }

            ArrayList<BloodGlucoseDetail> bloodGlucoseDetails = new ArrayList<>();

            bloodGlucoseDetails.add(detail);

            save.DETAILS = bloodGlucoseDetails;

            try {
                data = JsonUtil.toJson(save);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

             response= BloodGlucoseApi.getInstance(getActivity())
                    .saveBloodGlucose(data);
            return response;
        }

        @Override
        protected void onPostExecute(Response<BloodGlucoseRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
//
            if (result != null) {
                if (result.ReType == 100){
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveData();
                        }

                    }).showLoginDialog();
                }else if(result.ReType == 0){
                    if (result.Data != null) {
                        showMsgAndVoice(result.Msg);
                        performGetForm(sickPersonVo.ZYH);
                    }
                }else {
                    showMsgAndVoice(result.Msg);
                 /*   MediaUtil.getInstance(getActivity())
                            .playSound(R.raw.wrong, getActivity());*/
                }
            }else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBarBroadCast();

    }

    /**
     * 初始化扫描枪
     */
    private void initBarBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {

                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");


                }
            }
        };
    }


    public class GlucoseAdapter extends DingBaseAdapter<BloodGlucoseDetail> {

        EditText editText;
        TextView textView;
        private String jlxh;
        private String xmxh;
        int index = -1;
        public GlucoseAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            editText = (EditText) holder.getView(R.id.item_batch_glucose_input);
            textView = (TextView) holder.getView(R.id.item_batch_glucose_unit);
            editText.setHint("请输入剂量");
            textView.setText(item.XMDW);
            if (item != null){
                jlxh = item.JLXH;
                xmxh = item.XMXH;

                detail = new BloodGlucoseDetail();
                detail = item ;
            }
            solutionFocus(editText);
        }
        /**
         * 下面所有代码是解决ListView中，点击EditText弹出软键盘后失去焦点的问题
         */
        private void solutionFocus(EditText editText) {

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = 0 ;// 0 为position 当前就只有一个所以设置为0
                    }
                    return false;
                }
            });
            editText.clearFocus();
            if (index != -1 && index == 0) {
                // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                editText.requestFocus();
            }
//            editText.setSelection(editText .getText().length());
        }
    }
    public class GlucoseCompleteAdapter extends DingBaseAdapter<BloodGlucoseDetail> {


        public GlucoseCompleteAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            TextView content = (TextView) holder.getView(R.id.item_batch_glucose_complete_content);
            TextView input = (TextView) holder.getView(R.id.item_batch_glucose_complete_input);
            TextView unit = (TextView) holder.getView(R.id.item_batch_glucose_complete_unit);

            TextView name = (TextView) holder.getView(R.id.item_batch_glucose_complete_username);
            TextView now = (TextView) holder.getView(R.id.item_batch_glucose_complete_nowtime);

            content.setText(item.XMNR);
            input.setText(item.JHNR);
            unit.setText(item.XMDW);

            String jlxm = item.JLXM == null ? "" : item.JLXM;
            name.setText("执行护士:\t"+jlxm  );
            String jlsj = item.JLSJ == null ? "" : item.JLSJ;
            now.setText("执行时间:\t"+jlsj);
        }
    }
    public class InsulinAdapter extends DingBaseAdapter<BloodGlucoseDetail> {

        CheckBox title;
        private String jlxh;
        private String xmxh;
        public InsulinAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            title = (CheckBox) holder.getView(R.id.item_batch_insulin_title);
//            TextView number = (TextView) holder.getView(R.id.item_batch_insulin_number);
            TextView input = (TextView) holder.getView(R.id.item_batch_insulin_input);
            TextView unit = (TextView) holder.getView(R.id.item_batch_insulin_unit);
            if (item != null ) {
                title.setText(item.YDSMC+"\t\t");
                input.setText("剂量："+item.JHNR+"\t\t");
                unit.setText(item.XMDW);
                jlxh = item.JLXH;
                xmxh = item.XMXH;

                detail = new BloodGlucoseDetail();
                detail = item ;
            }


        }
    }
    public class InsulinCompleteAdapter extends DingBaseAdapter<BloodGlucoseDetail> {


        public InsulinCompleteAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {

            TextView time = (TextView) holder.getView(R.id.item_batch_insulin_complete_time);
            TextView title = (TextView) holder.getView(R.id.item_batch_insulin_complete_title);
            TextView input = (TextView) holder.getView(R.id.item_batch_insulin_complete_number);
            TextView name = (TextView) holder.getView(R.id.item_batch_insulin_complete_username);
            TextView now = (TextView) holder.getView(R.id.item_batch_insulin_complete_nowtime);
            time.setText(item.XMNR);
            title.setText(item.YDSMC);
            input.setText(item.JHNR + "\t\t"+item.XMDW);
            String jlxm = item.JLXM == null ? "" : item.JLXM;
            name.setText("执行护士:\t"+jlxm  );
            String jlsj = item.JLSJ == null ? "" : item.JLSJ;
            now.setText("执行时间:\t"+jlsj);
        }
    }
}
