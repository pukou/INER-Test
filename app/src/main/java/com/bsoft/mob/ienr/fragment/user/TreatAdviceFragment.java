package com.bsoft.mob.ienr.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.OxygenListActivity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

/**
 * 治疗医嘱（以输氧为主） Created by hy on 14-3-24.
 */
public class TreatAdviceFragment extends BaseUserFragment {

	private RadioGroup mRadioGroup;
	private CheckBox mCheckBox;
	private TextView mTimeTxt;


	private ListView mListView;


	@Override
	protected int configSwipeRefreshLayoutResId() {
		return R.id.id_swipe_refresh_layout;
	}
	@Override
	protected int setupLayoutResId() {
		return R.layout.fragment_treat_advice;
	}

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		mRadioGroup = (RadioGroup) root.findViewById(R.id.id_rg);
		mCheckBox = (CheckBox) root.findViewById(R.id.image);
		mTimeTxt = (TextView) root.findViewById(R.id.time);

		mListView = (ListView) root
				.findViewById(R.id.id_lv);
		EmptyViewHelper.setEmptyView(mListView,"mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mListView);
		initActionBar();
		initRadioGroup();
		initCheckBox();


		String ymdHM = DateTimeHelper.getServer_yyyyMMddHHmm00();
		initTimeTxt(ymdHM, R.id.time);
	}

	@Override
	protected void toRefreshData() {
		//// TODO: 2018/1/30
	}

	private void initActionBar() {

		actionBar.setTitle("治疗医嘱");
		actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
				+ mAppApplication.sickPersonVo.BRXM);
		actionBar.setBackAction(new Action() {

			@Override
			public void performAction(View view) {

				Intent intent = new Intent(getActivity(),
						OxygenListActivity.class);
				startActivity(intent);
			}
			@Override
			public String getText() {
				return getString(R.string.menu_back);
			}
			@Override
			public int getDrawable() {

				return R.drawable.ic_more_horiz_black_24dp;
			}
		});
	}



	private void initTimeTxt(String ymdHM, int viewId) {

		if (viewId == R.id.time) {
			mTimeTxt.setText(ymdHM);
			// TODO 请求数据

		}

	}

	private void initCheckBox() {

		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mRadioGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});
	}

	private void initRadioGroup() {

		mRadioGroup
				.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO 响应选择

					}
				});
	}
}
