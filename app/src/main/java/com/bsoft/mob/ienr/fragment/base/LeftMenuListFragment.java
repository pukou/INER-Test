package com.bsoft.mob.ienr.fragment.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.MainActivity;
import com.bsoft.mob.ienr.activity.SettingActivity;
import com.bsoft.mob.ienr.adapter.MenuAdapter;
import com.bsoft.mob.ienr.barcode.Devices;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.MemuVo;
import com.bsoft.mob.ienr.view.DataHolder;
import com.classichu.popupwindow.util.ScreenUtil;

import java.util.ArrayList;

/**
 * 主导航
 *
 * @author zzz
 */
public class LeftMenuListFragment extends ListFragment {

    private View mainView;
    private LinearLayout menu_settingLayout;
    private TextView name;
    private MenuAdapter adapter;
    private AppApplication application;
    private ArrayList<MemuVo> list = new ArrayList<>();
    private int currentItem = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (AppApplication) getActivity().getApplication();
        //
        DataHolder.getInstance().putData("hold_menuListFrag", this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_menu_list, parent, false);
        menu_settingLayout = (LinearLayout) mainView.findViewById(R.id.menu_settingLayout);
        name = (TextView) mainView.findViewById(R.id.name);
        ImageView menu_setting_iv = (ImageView) mainView.findViewById(R.id.menu_setting_iv);
        TextView menu_setting_tv = (TextView) mainView.findViewById(R.id.menu_setting_tv);
        if (AppApplication.getInstance().userConfig.navMenuShowByIcon){
            menu_setting_iv.setVisibility(View.VISIBLE);
            menu_setting_tv.setVisibility(View.GONE);
            menu_settingLayout.setPadding(SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingPrimary(),
                    SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingPrimary());
        }else{
            menu_setting_iv.setVisibility(View.GONE);
            menu_setting_tv.setVisibility(View.VISIBLE);
            menu_settingLayout.setPadding(0, 0, 0, 0);
        }
         fixNavigationBar(mainView);
        return mainView;
    }

    private void fixNavigationBar(View rootLayout) {
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_sc) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)
                || manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_lachesis) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新SC7 NR510
            rootLayout.setPadding(rootLayout.getPaddingLeft(),
                    rootLayout.getPaddingTop(),
                    rootLayout.getPaddingRight(),
                    rootLayout.getPaddingBottom() + ScreenUtil.getNavigationBarHeight());

        }
       /* if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_nr510) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新NR510 NR510
            rootLayout.setPadding(rootLayout.getPaddingLeft(),
                    rootLayout.getPaddingTop(),
                    rootLayout.getPaddingRight(),
                    rootLayout.getPaddingBottom() + ScreenUtil.getNavigationBarHeight());

        }*/
    }

    /**
     *
     */
    public void refreshMenuView() {
        list.clear();
        list.addAll(application.getMainModel());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list.addAll(application.getMainModel());
        // list = new ArrayList<MemuVo>();
        // list.add(new MemuVo("病人列表", 0));
        // list.add(new MemuVo("体征录入", 0));
        // list.add(new MemuVo("关于我们", 0));
        // list.add(new MemuVo("病人列表",
        // 0,"com.bsoft.mob.ienr.fragment.SickPersonListFragment"));
        // list.add(new MemuVo("体征录入",
        // 0,"com.bsoft.mob.ienr.fragment.BatchLifeSymptomFragment"));
        // list.add(new MemuVo("关于我们",
        // 0,"com.bsoft.mob.ienr.fragment.AboutFragment"));
        adapter = new MenuAdapter(getActivity(), list);

        LoginUser user = application.user;
        if (user == null) {
            application.reboot();
            return;
        }
        name.setText(application.user.YHXM);
        setListAdapter(adapter);

        menu_settingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity().startActivity(
                // new Intent(getActivity(), Setting.class));
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_up_in,
                        R.anim.slide_down_out1);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        adapter.changeState(position);
        if (currentItem != position) {
            currentItem = position;
            try {
                ((MainActivity) getActivity()).switchContent(((Fragment) Class
                        .forName(list.get(position).tclass).newInstance()));
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((MainActivity) getActivity()).toggle();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁
        DataHolder.getInstance().putData("hold_menuListFrag", null);
    }
}
