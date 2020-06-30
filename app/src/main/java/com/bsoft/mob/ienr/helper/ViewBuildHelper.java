package com.bsoft.mob.ienr.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.tools.DimensionTool;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

/**
 * Created by Classichu on 2018/1/16.
 */

public class ViewBuildHelper {

    public static TextView buildTextView(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        //TextAppearance
        TextViewCompat.setTextAppearance(textView, R.style.ClassicTextAppearanceSecondary);
        //Padding
        textView.setPadding(SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary(),
                SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary());
        //Text
        textView.setText(text);
        //Background
        return textView;
    }

    public static EditText buildEditText(Context context, String value) {
        return buildEditText(context,value,true);
    }
    public static EditText buildEditText(Context context, String value,boolean isSingleLine) {
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.setMargins(SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary(),
                SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary());
        //
        EditText edit = new EditText(context);
        edit.setGravity(Gravity.CENTER_VERTICAL);
        edit.setLayoutParams(ll_lp);
        //TextAppearance
        TextViewCompat.setTextAppearance(edit, R.style.ClassicTextAppearanceSecondary);
        //Padding
        edit.setPadding(SizeHelper.getEditPaddingPrimary(), SizeHelper.getEditPaddingPrimary(),
                SizeHelper.getEditPaddingPrimary(), SizeHelper.getEditPaddingPrimary());
        //Text
        edit.setText(value);
        //Background
        Drawable background = ContextCompatHelper.getDrawable(context, R.drawable.selector_classic_bg_edit, 0);
        ViewCompat.setBackground(edit, background);
        //
        edit.setMinWidth(SizeHelper.getEditMinWidth());
        edit.setLines(1);
        edit.setSingleLine(isSingleLine);
        return edit;
    }
    public static EditText buildEditTextMatchWrap(Context context, String value) {
        return buildEditTextMatchWrap(context, value, true);
    }
    public static EditText buildEditTextMatchWrap(Context context, String value,boolean isSingleLine) {
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.setMargins(SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary(),
                SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary());
        //
        EditText edit = new EditText(context);
        edit.setGravity(Gravity.CENTER_VERTICAL);
        edit.setLayoutParams(ll_lp);
        //TextAppearance
        TextViewCompat.setTextAppearance(edit, R.style.ClassicTextAppearanceSecondary);
        //Padding
        edit.setPadding(SizeHelper.getEditPaddingPrimary(), SizeHelper.getEditPaddingPrimary(),
                SizeHelper.getEditPaddingPrimary(), SizeHelper.getEditPaddingPrimary());
        //Text
        edit.setText(value);
        //Background
        Drawable background = ContextCompatHelper.getDrawable(context, R.drawable.selector_classic_bg_edit, 0);
        ViewCompat.setBackground(edit, background);
        //
        edit.setMinWidth(SizeHelper.getEditMinWidth());
        edit.setSingleLine(isSingleLine);
        //edit.setMaxLines(1);
        return edit;
    }

    public static EditText buildEditTextAutoWrap(Context context, String value) {
        return buildEditTextAutoWrap(context, value, true);
    }
    public static EditText buildEditTextAutoWrap(Context context, String value,boolean isSingleLine) {
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        ll_lp.setMargins(SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary(),
                SizeHelper.getEditMarginPrimary(), SizeHelper.getEditMarginPrimary());
        //
        EditText edit = new EditText(context);
        edit.setGravity(Gravity.CENTER_VERTICAL);
        edit.setLayoutParams(ll_lp);
        //TextAppearance
        TextViewCompat.setTextAppearance(edit, R.style.ClassicTextAppearanceSecondary);
        //Padding
        edit.setPadding(SizeHelper.getEditPaddingPrimary(), SizeHelper.getEditPaddingPrimary(),
                SizeHelper.getEditPaddingPrimary(), SizeHelper.getEditPaddingPrimary());
        //Text
        edit.setText(value);
        //Background
        Drawable background = ContextCompatHelper.getDrawable(context, R.drawable.selector_classic_bg_edit, 0);
        ViewCompat.setBackground(edit, background);
        //
        edit.setMinWidth(SizeHelper.getEditMinWidth());
        edit.setSingleLine(isSingleLine);
        //edit.setMaxLines(1);
        return edit;
    }

    //=================  logic   ========================
    public static TextView buildTextViewAutoWrap(Context context, String text) {
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        //
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(ll_lp);
        //TextAppearance
        TextViewCompat.setTextAppearance(textView, R.style.ClassicTextAppearanceSecondary);
        //Padding
        textView.setPadding(SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary(),
                SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary());
        //Text
        textView.setText(text);
        //Background
        return textView;
    }

    public static TextView buildTextViewMatchWrap(Context context, String text) {
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        //
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(ll_lp);

        //TextAppearance
        TextViewCompat.setTextAppearance(textView, R.style.ClassicTextAppearanceSecondary);
        //Padding
        textView.setPadding(SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary(),
                SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary());
        //Text
        textView.setText(text);
        //Background
        return textView;
    }

    public static TextView buildDialogTitleTextView(Context context, int resid) {
        return buildDialogTitleTextView(context, context.getString(resid));
    }

    public static TextView buildDialogTitleTextView(Context context, String text) {
        TextView dialogTitleTextView = new TextView(context);
        SizeHelper.setPaddingLeftTopRight(dialogTitleTextView, 20);
        dialogTitleTextView.setText(text);
        TextViewCompat.setTextAppearance(dialogTitleTextView, R.style.ClassicTextAppearancePrimaryColored);
        return dialogTitleTextView;
    }

    public static TextView buildDialogMsgTextView(Context context, String text) {
        TextView dialogTitleTextView = new TextView(context);
        SizeHelper.setPaddingLeftRight(dialogTitleTextView, 20);
        dialogTitleTextView.setText(text);
        TextViewCompat.setTextAppearance(dialogTitleTextView, R.style.ClassicTextAppearanceSecondary);
        return dialogTitleTextView;
    }

    public static TextView buildTimeTextView(Context context, String text) {
        TextView timeTextView = new TextView(context);
        //TextAppearance
        TextViewCompat.setTextAppearance(timeTextView, R.style.ClassicTextAppearancePrimary);
        //Padding
        timeTextView.setPadding(SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary(),
                SizeHelper.getTextPaddingPrimary(), SizeHelper.getTextPaddingPrimary());
        //Text
        timeTextView.setText(text);
        //Background
        timeTextView.setBackgroundResource(R.drawable.selector_classic_bg_text);
        timeTextView.setHint("请选择时间");
        timeTextView.setGravity(Gravity.CENTER_VERTICAL);
        Drawable left = ContextCompatHelper.getDrawable(context, R.drawable.ic_date_range_black_24dp);
        Drawable right = ContextCompatHelper.getDrawable(context, R.drawable.ic_keyboard_arrow_down_black_24dp);
        timeTextView.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
        timeTextView.setCompoundDrawablePadding(DimensionTool.getDimensionPx(context,R.dimen.classic_drawable_padding_primary));
        return timeTextView;
    }

    public static Pair<LinearLayout, TextView> buildClassTextViewLayout(Context context, String text) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_list_group_secondary, null, false);
        //
        TextView textView = linearLayout.findViewById(R.id.id_tv);
        //Text
        textView.setText(text);
        return Pair.create(linearLayout, textView);
    }

    public static ListView buildListView(Context context) {
        ListView listView = new ListView(context);
        return listView;
    }

    public static RecyclerView buildRecyclerView(Context context) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setBackgroundResource(R.drawable.shape_classic_bg_shadow);
        //hideLastDivider
        RecyclerViewDivider.with(context).color(Color.parseColor("#21000000")).hideLastDivider().build().addTo(recyclerView);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        //  ViewCompat.setElevation(recyclerView,22);
        return recyclerView;
    }
}
