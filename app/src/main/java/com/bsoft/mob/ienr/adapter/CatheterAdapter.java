package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.catheter.CatheterMeasurData;
import com.bsoft.mob.ienr.model.catheter.CatheterSpinnerData;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.util.ArrayList;
import java.util.List;

public class CatheterAdapter extends BaseAdapter {

    ArrayList<CatheterMeasurData> list;

    LayoutInflater inflater;

    Context context;


    public CatheterAdapter(Context context, ArrayList<CatheterMeasurData> list) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_list_catheter_record, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.YZMC = (TextView) convertView.findViewById(R.id.record_name);
            viewHolder.YLL = (EditText) convertView.findViewById(R.id.record_text);
            SpinnerLayout spinnerLayout = (SpinnerLayout) convertView.findViewById(R.id.id_spinner_layout);
            viewHolder.GLDX = spinnerLayout.getSpinner();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(list.get(position).spinners);
        viewHolder.GLDX.setAdapter(spinnerAdapter);
        for (int i = 0; i < list.get(position).spinners.size(); i++) {
            CatheterSpinnerData spinner = list.get(position).spinners.get(i);
            if (spinner.MRBZ != null && spinner.MRBZ.equals("1")) {
                viewHolder.GLDX.setSelection(i);
            }
        }
        viewHolder.GLDX.setOnItemSelectedListener(new MyOnItemSelectedListener(viewHolder, position, spinnerAdapter));
        viewHolder.YZMC.setText(list.get(position).YZMC);
        viewHolder.YLL.addTextChangedListener(new MyTextWatcher(viewHolder, position));
        return convertView;
    }


    class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private ViewHolder viewHolder;
        private int position;
        private SpinnerAdapter adapter;

       public MyOnItemSelectedListener(ViewHolder viewHolder,int position,SpinnerAdapter adapter){
           this.viewHolder = viewHolder;
           this.position = position;
           this.adapter = adapter;
       }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            viewHolder.GLDX.setSelection(i);
            CatheterMeasurData catheterMeasurData = list.get(position);
            catheterMeasurData.DZXH = adapter.getSpinnerXH(i);
            list.set(position,catheterMeasurData);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class MyTextWatcher implements TextWatcher {
        private ViewHolder mHolder;
        private int position;

        public MyTextWatcher(ViewHolder holder, int position) {
            mHolder = holder;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start/*开始的位置*/, int count/*被改变的旧内容数*/, int after/*改变后的内容数量*/) {
            //这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start/*开始位置*/, int before/*改变前的内容数量*/, int count/*新增数*/) {
            //这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
        }

        @Override
        public void afterTextChanged(Editable editable) {//表示最终内容
//            mHolder.YLL.setText(editable);
            CatheterMeasurData catheterMeasurData = list.get(position);
            catheterMeasurData.YLL = editable.toString();
            list.set(position, catheterMeasurData);
        }
    }

    static class ViewHolder {
        TextView YZMC;
        EditText YLL;
        Spinner GLDX;
    }


    //下拉adapter
    class SpinnerAdapter extends BaseAdapter {

        List<CatheterSpinnerData> lists;

        public SpinnerAdapter(ArrayList<CatheterSpinnerData> list) {
            super();
            this.lists = list;
        }

        @Override
        public int getCount() {
            return lists != null ? lists.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position).XMMC;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public String getSpinnerXH(int position){return lists.get(position).GLXM;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_spinner_item, parent,false);
            TextView textView = (TextView) convertView.findViewById(R.id.text1);
            textView.setText(lists.get(position).XMMC);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_spinner_dropdown_item, parent,false);
            TextView textView = (TextView) convertView.findViewById(R.id.text1);
            textView.setText(lists.get(position).XMMC);
            return convertView;
        }
    }
}
