package com.bsoft.mob.ienr.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.adapter.ChartViewAdapter;
import com.bsoft.mob.ienr.api.InspectionApi;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.inspection.InspectionXMBean;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
public class ShowChartActivity extends BaseActivity {


    private String xmid;
    private String zwmc;
    private LineChart mChart;

    @Deprecated
    public LineData getLineData() {
        int maxX = 10;
        List<Entry> entryList = new ArrayList<>();
        List<Entry> entryList2 = new ArrayList<>();
        for (int i = 0; i < maxX; i++) {
            //每个entry相当于线条上面的一个点
            Entry entry = new BarEntry(i * 5, i);
            entryList.add(entry);
            Entry entry2 = new BarEntry(i * 6 + 5, i);
            entryList2.add(entry2);
        }
        //创建LineDataSet对象，表示一条线的数据
        LineDataSet dataSet = new LineDataSet(entryList, "Line1");
        LineDataSet dataSet2 = new LineDataSet(entryList2, "Line2");
        //设置线条颜色和宽度
        dataSet.setLineWidth(4);
        dataSet.setColor(Color.RED);
        //dataSet.setDrawCircleHole(false);//是否绘制空心圆
        //是否绘制圆形
        dataSet.setDrawCircles(false);//不绘制线条上面的圆
        //创建集合，存储所有线条数据对象
        List<ILineDataSet> list = new ArrayList<>();
        //存放一条线的数据
        list.add(dataSet);
        list.add(dataSet2);
        //生成x轴的数据
        List<String> xVals = ChartData.generateXVals(0, maxX);
        LineData lineData = new LineData(xVals, list);
        return lineData;
    }

    private ChartViewAdapter chartViewAdapter;


    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_show_chart;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        //id_photo_view.setImageResource(R.drawable.ic_launcher);

        Intent intent = getIntent();
        xmid = intent.getStringExtra("xmid");
        zwmc = intent.getStringExtra("zwmc");

        actionBar.setTitle("图表");
        //


        ListView id_lv = (ListView) findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(id_lv);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, id_lv);
        mChart = (LineChart) findViewById(R.id.id_line_chart);
        mChart.setNoDataText("");


        List<InspectionXMBean> pairList = new ArrayList<>();
        chartViewAdapter = new ChartViewAdapter(pairList);
        id_lv.setAdapter(chartViewAdapter);


        //
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void initChartConfig(LineData lineData, float min, float max) {
        mChart.setVisibility(View.VISIBLE);
        int height = Resources.getSystem().getDisplayMetrics().heightPixels / 3;
        ViewGroup.LayoutParams layoutParams = mChart.getLayoutParams();
        layoutParams.height = height;
        mChart.setLayoutParams(layoutParams);
//设置没有数据时候展示的文本
        mChart.setNoDataText("暂无数据");
//1.给Chart设置数据
        mChart.setData(lineData);
//2.设置y轴的取值范围
        YAxis axisLeft = mChart.getAxisLeft();
//设置y轴最小值
        // axisLeft.setAxisMinValue(0);
        axisLeft.setAxisMinValue(min);
//设置y轴最大值
        axisLeft.setAxisMaxValue(max);
        //axisLeft.setAxisMaxValue(100);
//3.设置去掉右边的y轴线
        YAxis axisRight = mChart.getAxisRight();
        axisRight.setEnabled(false);
//4.设置是否启用x轴线
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);//显示x轴线
        /**
         * TOP 默认 　　　　　　　　　 顶端
         TOP_INSIDE　　　　　　　　顶端，label显示在x下方
         BOTTOM 　　　　　　　　　 底部
         BOTTOM_INSIDE　　　　　 底部，Label显示在X轴上方
         BOTH_SIDED　　　　　　　上下均显示
         */
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //设置X轴位置
        xAxis.setValueFormatter(new XAxisValueFormatter() {
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                String showX = original;
                try {
                    int pos = index + 1;
                    Date date = DateUtil.getDateCompat(inspectionXMBeanListNew.get(pos).SHSJ);
                    showX = inspectionXMBeanListNew.get(pos).SHSJ;
                    if (date != null) {
                        showX = DateUtil.dateToString(date, "MM-dd");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return showX;
            }
        });
        Legend le = mChart.getLegend();
        le.setEnabled(false);
        //### le.setPosition(位置);
        le.setFormSize(10f); //图例大小
        le.setForm(Legend.LegendForm.CIRCLE); //图例形状
        //设置说明文字的大小和颜色
        le.setTextSize(12f);
        le.setTextColor(Color.GRAY);

//5.设置表格描述信息
        mChart.setDescription(zwmc);
        //## mChart.setDescription(null);    //右下角说明文字
//6.设置是否绘制表格背景
        mChart.setDrawGridBackground(true);
//设置表格网格背景的颜色
        mChart.setGridBackgroundColor(Color.parseColor("#66EAEAEA"));
//7.设置绘制动画的时间
        // mChart.animateXY(3000, 3000);

    }


    class GetHttpTask extends AsyncTask<Void, Void, Response<List<InspectionXMBean>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<InspectionXMBean>> doInBackground(Void... arg0) {

            if (application.sickPersonVo == null) {
                return null;
            }
            ////写代码时候 需要去确定 LIS 库是通过 ZYH 还是 ZYHM 取值的
            String zyhm = application.sickPersonVo.ZYHM;
            String jgid = application.jgId;
            int sysType = Constant.sysType;
            /*zyh = "0065857";
            xmid = "49";*/
            //写代码时候 需要去确定 LIS 库是通过 ZYH 还是 ZYHM 取值的
            return InspectionApi.getInstance(application).GetInspectionXMBeanList(xmid,
                    zyhm, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<List<InspectionXMBean>> result) {

            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (result == null) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(application, application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                //ArrayList<InspectionVo> tList = (ArrayList<InspectionVo>) result.Data;
                parseData(result.Data);
            } else {
                showMsgAndVoice(result.Msg);
            }

        }
    }

    /**
     * 是否是数字或小数
     */
    private boolean isNumber(String str) {
        if (EmptyTool.isBlank(str)) {
            return false;
        }
        String reg = "\\d+(\\.\\d+)?";
        return str.matches(reg);

    }

    private List<InspectionXMBean> inspectionXMBeanListNew;

    private void parseData(List<InspectionXMBean> inspectionXMBeanList) {
        //
        inspectionXMBeanListNew = new ArrayList<>();
        if (inspectionXMBeanList != null && !inspectionXMBeanList.isEmpty()) {
            InspectionXMBean pairOne = new InspectionXMBean();
            pairOne.SHSJ = "时间";
            pairOne.HYJG = "结果";
            pairOne.DW = "";
            inspectionXMBeanListNew.add(pairOne);
        inspectionXMBeanListNew.addAll(inspectionXMBeanList);
        }
        chartViewAdapter.refreshDataList(inspectionXMBeanListNew);
        //
        if (inspectionXMBeanList==null){
            return;
        }
        int maxX = inspectionXMBeanList.size();
        List<Entry> entryList = new ArrayList<>();
        for (int i = 0; i < maxX; i++) {
            if (!isNumber(inspectionXMBeanList.get(i).HYJG)) {
                continue;
            }
            float hyjg_val = Float.valueOf(inspectionXMBeanList.get(i).HYJG);
            //每个entry相当于线条上面的一个点
            Entry entry = new Entry(hyjg_val, i);
            entryList.add(entry);
        }

        if (entryList.size() > 0) {
            float max_Value = entryList.get(0).getVal();
            float min_Value = entryList.get(0).getVal();
            for (Entry entry : entryList) {
                max_Value = Math.max(max_Value, entry.getVal());
                min_Value = Math.min(min_Value, entry.getVal());
            }
            //偏移20%
            max_Value = (float) (max_Value + (0.2 * max_Value));
            min_Value = (float) (min_Value - (0.2 * max_Value));
            min_Value = min_Value < 0 ? 0 : min_Value;
            //创建LineDataSet对象，表示一条线的数据
            LineDataSet dataSet = new LineDataSet(entryList, "Line1");
            //设置线条颜色和宽度
            // dataSet.setLineWidth(2);
            dataSet.setColor(Color.RED);
            dataSet.setDrawCircleHole(false);//是否绘制空心圆
            //是否绘制圆形
            dataSet.setDrawCircles(true);
            //创建集合，存储所有线条数据对象
            List<ILineDataSet> list = new ArrayList<>();
            //存放一条线的数据
            list.add(dataSet);
            //生成x轴的数据
            List<String> xVals = ChartData.generateXVals(0, maxX);

            LineData lineData = new LineData(xVals, list);
///

            initChartConfig(lineData, min_Value, max_Value);
            mChart.invalidate();
        }/*else {
            mChart.post(new Runnable() {
                @Override
                public void run() {
                    mChart.setVisibility(View.GONE);
                     mChart.setNoDataText("");
                    mChart.invalidate();
                }
            });
        }*/


    }

}
  /* =============================================================== end */