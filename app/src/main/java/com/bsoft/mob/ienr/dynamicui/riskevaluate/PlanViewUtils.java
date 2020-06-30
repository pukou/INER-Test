package com.bsoft.mob.ienr.dynamicui.riskevaluate;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.risk.PEOption;
import com.bsoft.mob.ienr.model.risk.PainEvaluate;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */
public class PlanViewUtils {

    Context context;
    LinearLayout root;
    LinearLayout.LayoutParams cbparams;
    LinearLayout.LayoutParams params;
//    Drawable drawable;
    private List<PainEvaluate> mList;



    public PlanViewUtils(Context context, LinearLayout root,List<PainEvaluate> mList) {
        super();
        this.context = context;
        this.root = root;
        this.mList = mList;
        cbparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cbparams.setMargins(50, 5, 0, 5);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 5, 10, 5);
//        drawable = context.getResources().getDrawable(R.drawable.menu_help);
//        drawable.setBounds(0, 0, 32, 32);
    }

    public void build() {

        for (int i = 0; i < mList.size() ; i++) {
            PainEvaluate pain = mList.get(i);
            if (mList.get(i).XMLX.equals("2")) {
                //单选
                buildRadio(pain,i);
            } else if (mList.get(i).XMLX.equals("1")){
                //手动输入
                buildInput(pain,i);
            }else {
                //多选
                buildCheck(pain,i);
            }
        }
    }

    private void buildRadio(final PainEvaluate pain, int i) {
        final LinearLayout card = new LinearLayout(context);
        card.setLayoutParams(params);
        card.setBackgroundColor(context.getResources().getColor(
                R.color.classicViewBg));
        card.setOrientation(LinearLayout.VERTICAL);

        final TextView title = new TextView(context);
        title.setText(pain.XMMC);
        title.getPaint().setFakeBoldText(true);
        title.setPadding(10, 5, 0, 5);
        title.setTextSize(18);
//        title.setCompoundDrawables(null, null, drawable, null);
        title.setTag(i);
        card.addView(title);

        if (pain.PGXX != null) {
            for (int j = 0; j < pain.PGXX.size(); j++) {
                final PEOption peOption = pain.PGXX.get(j);
                final CheckBox cb = new AppCompatCheckBox(context);
                if (peOption.XGBZ.equals("0")) {
                    //不允许修改
                    cb.setText(peOption.XXMC);
                    peOption.XMQZ = peOption.XXMC;
                }else if (peOption.XGBZ .equals("1")) {
                    cb.setText(peOption.XXMC + "    (长按可修改)");
                    cb.setTextColor(Color.RED);
                    cb.setTag(j);
                    if (peOption.SELECT) {
                        if (peOption.XMQZ!=null){
                            cb.setText(peOption.XMQZ);
                        }else {
                            cb.setText(peOption.XXMC+ "    (长按可修改)");
                            peOption.XMQZ = peOption.XXMC;

                        }

                    }
                    cb.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final EditText editText = new EditText(context);
                            View txt = ViewBuildHelper.buildDialogTitleTextView(context, "请输入");
                            new AlertDialog.Builder(context)
                                    //.setTitle("请输入")
                                    .setView(editText)
                                    .setCustomTitle(txt)
                                    .setPositiveButton(R.string.project_operate_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String text = editText.getText().toString();
                                            if (EmptyTool.isBlank(text)) {
                                                return;
                                            }
                                            cb.setText(text);
                                            String s = cb.getText().toString();

                                            if (!EmptyTool.isBlank(s)) {
//                                                cb.setChecked(true);
                                                peOption.XMQZ = s;
                                            }
                                        }
                                    })
                                    .setNegativeButton(R.string.project_operate_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                            return true;
                        }
                    });
                }
                String xxxh = peOption.XXXH;
                cb.setId(Integer.parseInt(xxxh));
              /*  cb.setButtonDrawable(context.getResources().getDrawable(
                        R.drawable.ck_tmp));*/
                cb.setLayoutParams(cbparams);
                cb.setTag(j);
                cb.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (cb.isChecked()) {
                            for (int k = 0; k < pain.PGXX.size(); k++) {
                                String xh = pain.PGXX.get(k).XXXH;
                                CheckBox c = (CheckBox) card
                                        .findViewById(Integer.parseInt(xh));
                                if (c != null) {
                                    int p = (Integer) c.getTag();
                                    if (c.getId() != cb.getId()) {
                                        if (c.isChecked()) {
                                            c.setChecked(false);
                                            pain.PGXX.get(p).SELECT = false;
                                        }
                                    } else {
                                        pain.PGXX.get(p).SELECT = true;
                                    }
                                }
                            }
                        } else {
                            int p = (Integer) cb.getTag();
                            pain.PGXX.get(p).SELECT = false;
                        }
                    }
                });
                cb.setChecked(!EmptyTool.isBlank(pain.PGXX.get(j).JLXM));
                card.addView(cb);
            }
        }
        root.addView(card);
    }

    private void buildRadio2(final PainEvaluate pain, int i) {
        final LinearLayout card = new LinearLayout(context);
        card.setLayoutParams(params);
        card.setBackgroundColor(context.getResources().getColor(
                R.color.classicViewBg));
        card.setOrientation(LinearLayout.VERTICAL);

        final TextView title = new TextView(context);
        title.setText(pain.XMMC);
        title.getPaint().setFakeBoldText(true);
        title.setPadding(10, 5, 0, 5);
        title.setTextSize(18);
//        title.setCompoundDrawables(null, null, drawable, null);
        title.setTag(i);
        card.addView(title);

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.setLayoutParams(cbparams);

        if (pain.PGXX != null) {
            for (int j = 0; j < pain.PGXX.size(); j++) {
                final PEOption peOption = pain.PGXX.get(j);
                final RadioButton rb = new AppCompatRadioButton(context);

                if (peOption.XGBZ.equals("0")) {
                    //不允许修改
                    rb.setText(peOption.XXMC);
                }else if (peOption.XGBZ .equals("1")){
                    rb.setText(peOption.XXMC+"    (长按可修改)");

                    rb.setTextColor(Color.RED);
                    rb.setTag(j);

                    if (peOption.SELECT){
                        rb.setText(peOption.XMQZ);
                    }
                    rb.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final EditText editText = new EditText(context);
                            View txt = ViewBuildHelper.buildDialogTitleTextView(context, "请输入");
                            new AlertDialog.Builder(context)
                                    //.setTitle("请输入")
                                    .setView(editText)
                                    .setCustomTitle(txt)
                                    .setPositiveButton(R.string.project_operate_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String text = editText.getText().toString();
                                            if (EmptyTool.isBlank(text)) {
                                                return;
                                            }
                                            rb.setText(text);
                                            String s = rb.getText().toString();

                                            if (!EmptyTool.isBlank(s)) {
                                                rb.setChecked(true);
                                                peOption.XMQZ = s;
                                            }
                                        }
                                    })
                                    .setNegativeButton(R.string.project_operate_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                            return true;
                        }
                    });

                }

               /* rb.setButtonDrawable(context.getResources().getDrawable(
                        R.drawable.ck_tmp));*/
                rb.setLayoutParams(cbparams);
                rb.setTag(j);

                if (peOption.SELECT){
                    rb.setChecked(true);
//                    if (rb.isChecked()){
//                        rb.setChecked(false);
//                    }

                }
                rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            peOption.SELECT = true;

                        } else {
                            peOption.SELECT = false;
                        }
                    }
                });

                radioGroup.addView(rb);

            }
        }
        card.addView(radioGroup);
        root.addView(card);
    }
    private void buildCheck(final PainEvaluate pain, int i) {
        final LinearLayout card = new LinearLayout(context);
        card.setLayoutParams(params);
        card.setBackgroundColor(context.getResources().getColor(
                R.color.classicViewBg));
        card.setOrientation(LinearLayout.VERTICAL);

        final TextView title = new TextView(context);
        title.setText(pain.XMMC);
        title.getPaint().setFakeBoldText(true);
        title.setPadding(10, 5, 0, 5);
        title.setTextSize(18);
//        title.setCompoundDrawables(null, null, drawable, null);
        title.setTag(i);
        card.addView(title);


        if (pain.XMMC != null) {

            for (int j = 0; j < pain.PGXX.size(); j++) {
                final PEOption peOption = pain.PGXX.get(j);
                final CheckBox cb = new AppCompatCheckBox(context);
                if (peOption.XGBZ.equals("0")) {
                    //不允许修改
                    cb.setText(peOption.XXMC);
                    peOption.XMQZ = peOption.XXMC;
                }else if (peOption.XGBZ .equals("1")){
                    cb.setText(peOption.XXMC+"    (长按可修改)");
                    cb.setTextColor(Color.RED);
                    cb.setTag(j);

                    if (peOption.SELECT){
                        if (peOption.XMQZ!=null){
                            cb.setText(peOption.XMQZ);
                        }else {
                            cb.setText(peOption.XXMC+ "    (长按可修改)");
                            peOption.XMQZ = peOption.XXMC;
                        }
                    }

                    cb.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View v) {
                            final EditText editText = new EditText(context);
                            View txt = ViewBuildHelper.buildDialogTitleTextView(context, "请输入");
                            new AlertDialog.Builder(context)
                                    //.setTitle("请输入")
                                    .setView(editText)
                                    .setCustomTitle(txt)
                                    .setPositiveButton(R.string.project_operate_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                    String text = editText.getText().toString();
                                                    if (EmptyTool.isBlank(text)){
                                                        return;
                                                    }
                                                    ((CheckBox) v).setText(text);
                                                    String s = cb.getText().toString();
                                                    if (!EmptyTool.isBlank(s)) {
                                                        ((CheckBox) v).setChecked(true);
                                                        peOption.XMQZ = s;
                                                    }

                                        }
                                    })
                                    .setNegativeButton(R.string.project_operate_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                            return false;
                        }
                    });
                }
               /* cb.setButtonDrawable(context.getResources().getDrawable(
                        R.drawable.ck_mulit_slt));*/
                cb.setLayoutParams(cbparams);
                cb.setTag(j);
                if (peOption.SELECT){
                    cb.setChecked(true);
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            peOption.SELECT = true;

                        }else {
                            peOption.SELECT = false;
                        }
                    }
                });
                card.addView(cb);
            }
        }

        root.addView(card);
    }
    private void buildInput(final PainEvaluate pain, int i) {
        final LinearLayout card = new LinearLayout(context);
        card.setLayoutParams(params);
        card.setBackgroundColor(context.getResources().getColor(
                R.color.classicViewBg));
        card.setOrientation(LinearLayout.VERTICAL);

        final TextView title = new TextView(context);
        title.setText(pain.XMMC);
        title.getPaint().setFakeBoldText(true);
        title.setPadding(10, 5, 0, 5);
        title.setTextSize(18);
//        title.setCompoundDrawables(null, null, drawable, null);
        title.setTag(i);
        card.addView(title);


        final EditText editText = new EditText(context);
        editText.setHint("可以手动输入...");

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
        editText.setSingleLine();
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextColor(Color.RED);
        LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        100);
        layoutParams.setMargins(50, 5, 0, 5);

        editText.setLayoutParams(layoutParams);

        card.addView(editText);

        String text = editText.getText().toString();

//
        List<PEOption> pgxx = pain.PGXX;
        final PEOption peOption = pgxx.get(0);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!EmptyTool.isBlank(s.toString())) {
                       peOption.SELECT = true;
                       peOption.XMQZ = s.toString();
                                                      }
            }
        });
        if (peOption.SELECT){
            editText.setText(peOption.XMQZ);
        }
        root.addView(card);
    }
    private  void showDialog(final View view){

        final int viewTag = (Integer) view.getTag();

        final EditText editText = new EditText(context);
        View txt = ViewBuildHelper.buildDialogTitleTextView(context, "请输入");
        new AlertDialog.Builder(context)
                //.setTitle("请输入")
                .setView(editText)
                .setCustomTitle(txt)
                .setPositiveButton(R.string.project_operate_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        Log.e("111", "onClick: "+text );
                        ((TextView) view).setText(text);
//                        String s = ((TextView) view).getText().toString();
//                        if (!EmptyTool.isBlank(s) || s.equals("其他")) {
//                            ((TextView) view).setChecked(true);
//                            peOption.XMQZ = s;
//                        }
                    }
                })
                .setNegativeButton(R.string.project_operate_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
