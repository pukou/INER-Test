package com.bsoft.mob.ienr.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.SizeHelper;

public class BsoftActionBar extends LinearLayout implements OnClickListener {

	private LayoutInflater mInflater;
	// private ViewGroup mBarView;
	private TextView titleTextView;
	// 病人信息 start
	private TextView patientview;
	// 病人信息 end
	// private ImageView titleImageView;
	private LinearLayout leftLayout;
    private ImageView leftImageView;
	private TextView leftTextView;

	private LinearLayout rightLayout;

	public BsoftActionBar(Context context) {
		super(context);
		init(context);
	}

	public BsoftActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BsoftActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 初始化界面
	 * 
	 * @param context
	 */
	void init(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.layout_actionbar, this);
		// addView(mBarView);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		// 病人信息start
		patientview = (TextView) findViewById(R.id.patientinfo);
		// 病人信息end
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);

		rightLayout = (LinearLayout) findViewById(R.id.rightLayout);
        leftImageView = (ImageView) findViewById(R.id.leftImageView);
		leftTextView = (TextView) findViewById(R.id.leftTextView);

	}

	// 设置返回按钮事件-Action
	public void setBackAction(Action action) {
		if (action==null){
            leftImageView.setVisibility(GONE);
            leftTextView.setVisibility(GONE);
			//
			leftLayout.setOnClickListener(null);
			leftLayout.setTag(null);
			leftLayout.setVisibility(View.INVISIBLE);
			return;
		}
        leftImageView.setImageResource(action.getDrawable());
		leftTextView.setText(action.getText());
        if (AppApplication.getInstance().userConfig.navMenuShowByIcon) {
            leftImageView.setVisibility(VISIBLE);
            leftTextView.setVisibility(GONE);
            leftLayout.setPadding(SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingPrimary(),
                    SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingPrimary());
        } else {
            leftImageView.setVisibility(GONE);
            leftTextView.setVisibility(VISIBLE);
            leftLayout.setPadding(0, 0, 0, 0);
        }
		//
		leftLayout.setOnClickListener(this);
		leftLayout.setTag(action);
		leftLayout.setVisibility(View.VISIBLE);
	}

	public void setTitleText(int resId) {
		titleTextView.setText(resId);
		titleTextView.setVisibility(View.VISIBLE);
	}

	public void setTitle(CharSequence title) {
		titleTextView.setText(title);
		titleTextView.setVisibility(View.VISIBLE);
	}

	public void setTitle(CharSequence title, Action action) {
		titleTextView.setOnClickListener(this);
		titleTextView.setTag(action);
		titleTextView.setText(title);
		titleTextView.setVisibility(View.VISIBLE);
	}

	// 病人信息start
	public void setPatient(String patient) {
		patientview.setText(patient);
		patientview.setVisibility(View.VISIBLE);
	}

	// 病人信息end

	// 添加Action
	public void addAction(Action action) {
		int index = rightLayout.getChildCount();
		rightLayout.addView(inflateAction(action), index);
	}

	// 改变状态
	public void changAction(int flg) {
		rightLayout.setVisibility(flg);
	}

	private View inflateAction(Action action) {
		View view = mInflater.inflate(R.layout.layout_actionbar_item, rightLayout,
				false);
        LinearLayout id_right_item_layout = (LinearLayout) view.findViewById(R.id.id_right_item_layout);
        //
        ImageView right_image = (ImageView) view
				.findViewById(R.id.right_image);
        right_image.setImageResource(action.getDrawable());
        //
		TextView right_text = (TextView) view
				.findViewById(R.id.right_text);
		right_text.setText(action.getText());

        if (AppApplication.getInstance().userConfig.navMenuShowByIcon) {
            right_image.setVisibility(VISIBLE);
            right_text.setVisibility(GONE);
            id_right_item_layout.setPadding(SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingPrimary()
                    , SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingPrimary());
        } else {
            right_image.setVisibility(GONE);
            right_text.setVisibility(VISIBLE);
            id_right_item_layout.setPadding(0, 0, 0, 0);
        }

		view.setTag(action);
		view.setOnClickListener(this);
		return view;
	}

	public boolean removeAction(Action action) {

		View view = findViewWithTag(action);
		if (view != null) {
			rightLayout.removeView(view);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		doAction(v);
		// switch (v.getId()) {
		// case R.id.refreshImageView:
		// statrtRefresh();
		// break;
		// default:
		// doAction(v);
		// break;
		// }
	}

	public void doAction(View v) {
		Object tag = v.getTag();
		if (tag instanceof Action) {
			Action action = (Action) tag;
			action.performAction(v);
		}
	}

	public interface Action {
		 int getDrawable();
		 String getText();
		 void performAction(View view);
	}

	// 扩充Action可继承AbstractAction，实现performAction
	public static abstract class AbstractAction implements Action {
		final private int mDrawable;

		public AbstractAction(int drawable) {
			mDrawable = drawable;
		}

		@Override
		public int getDrawable() {
			return mDrawable;
		}
	}

	// 按钮点击之后的Intent跳转
	@Deprecated
	public static class IntentAction extends AbstractAction {
		private Context mContext;
		private Intent mIntent;

		public IntentAction(Context context, Intent intent, int drawable) {
			super(drawable);
			mContext = context;
			mIntent = intent;
		}

		@Override
		public String getText() {
			return null;
		}

		@Override
		public void performAction(View view) {
			try {
				mContext.startActivity(mIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
