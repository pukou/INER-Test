package com.bsoft.mob.ienr.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.listener.ListSelected;
import com.bsoft.mob.ienr.model.DataMapWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
    /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */

/**
 * Created by Classichu on 2017/11/7.
 */
public class ListSelectDialogFragment extends DialogFragment {
    private static ListSelected mListSelected;

    public static ListSelectDialogFragment newInstance(String title, Map<String, String> stringMap, boolean cancelable, ListSelected listSelected) {
        ListSelectDialogFragment fragment = new ListSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
//        args.putInt("id", id);
        args.putSerializable("DataMapWrapper", new DataMapWrapper(stringMap));
        fragment.setArguments(args);
        //
        fragment.setCancelable(cancelable);
        //
        mListSelected = listSelected;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean hasData = false;
        final ListView config_list = new ListView(getActivity());
        final DataMapWrapper dataMapWrapper = (DataMapWrapper) getArguments().getSerializable("DataMapWrapper");
        String title = (String) getArguments().getSerializable("title");
        final List<String> keyList = new ArrayList<>();
        final List<String> valueList = new ArrayList<>();
        if (dataMapWrapper.getMap()!=null){
        for (String key : dataMapWrapper.getMap().keySet()) {
            String value = dataMapWrapper.getMap().get(key);
            keyList.add(key);
            valueList.add(value);
            hasData = true;
        }}

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_list_text_one_primary,
                R.id.name, valueList);
        config_list.setAdapter(arrayAdapter);
        config_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //hide
                ListSelectDialogFragment.this.dismiss();
                if (mListSelected != null) {
                    mListSelected.onListSelected(keyList.get(position), valueList.get(position));
                }

            }
        });


        View txt = ViewBuildHelper.buildDialogTitleTextView(getActivity(), title);
        return new AlertDialog.Builder(getActivity())
                // .setTitle(title)
                .setCustomTitle(txt)
                .setCancelable(!hasData)
                // .setIcon(android.R.drawable.ic_dialog_info)
                .setView(config_list).create();
    }
}

/* =============================================================== end */
