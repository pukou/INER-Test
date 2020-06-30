package com.bsoft.mob.ienr.fragment.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.barcode.Devices;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.bsoft.mob.ienr.adapter.MenuAdapter;
import com.bsoft.mob.ienr.model.MemuVo;
import com.bsoft.mob.ienr.view.BSToast;
import com.classichu.popupwindow.util.ScreenUtil;

import java.util.ArrayList;

/**
 * 子导航
 *
 * @author zzz
 */
public class RightMenuListFragment extends BaseFragment {

    private View mainView;
    private TextView text;
    private ListView listView;
    private MenuAdapter adapter;
    //
    private ArrayList<MemuVo> list;

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_user_model_menu_list;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        mainView = rootLayout;
        text = (TextView) mainView.findViewById(R.id.text);
        listView = (ListView) mainView.findViewById(R.id.id_lv);


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(IBarCode.Name_Change);
        getActivity().registerReceiver(barBroadcast, filter);
        if (mAppApplication.sickPersonVo == null) {
            //正好可以延后显示 继续使用
            BSToast.showToast(getActivity(), "缓存数据失效，正在重启...", BSToast.LENGTH_LONG);
            mAppApplication.reboot();
            return;
        }
        text.setText("当前病人：" + mAppApplication.sickPersonVo.BRXM);
        list = mAppApplication.getUserModel();

        adapter = new MenuAdapter(getActivity(), list);

        listView.setAdapter(adapter);

        if (mAppApplication.userModelItem != 0) {
            changePostion(mAppApplication.userModelItem);
        }
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                int usrItem = mAppApplication.userModelItem;
                if (usrItem != position) {
                    changePostion(position);
                    try {
                        String clzzStr = list.get(position).tclass.trim();
                        Class<?> clazz = Class.forName(clzzStr);
                        ((UserModelActivity) getActivity())
                                .switchContent((Fragment) clazz.newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ((UserModelActivity) getActivity()).toggle();
                }
            }
        });
    }

    public void changePostion(int position) {

        if (adapter != null) {
            adapter.changeState(position);
            mAppApplication.userModelItem = position;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(barBroadcast);
    }

    BroadcastReceiver barBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (IBarCode.Name_Change.equals(intent.getAction())) {

                if (mAppApplication.sickPersonVo != null) {
                    String str = "当前病人：" + mAppApplication.sickPersonVo.BRXM;
                    text.setText(str);
                    toastInfo(str);
                }

            }
        }
    };

    private void toastInfo(String msg) {
        showSnack(msg);
        /*Crouton.makeText(getActivity(), msg, Style.INFO)
                .show();*/
    }
}
