package com.bsoft.mob.ienr.view.waterdropcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.view.waterdropcard.DropCover.OnDragCompeteListener;

public class WaterDrop extends RelativeLayout {
	private Paint mPaint = new Paint();
	private TextView mTextView;
	private DropCover.OnDragCompeteListener mOnDragCompeteListener;
	private boolean mHolderEventFlag;

	public WaterDrop(Context context) {
		super(context);
		init();
	}

	public WaterDrop(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setText(String str) {
		mTextView.setText(str);
	}

	public void setTextSize(int size) {
		mTextView.setTextSize(size);
	}

	@SuppressLint("NewApi")
	private void init() {
		mPaint.setAntiAlias(true);
		if (VERSION.SDK_INT > 11) {
			//关闭硬件加速
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		mTextView = new TextView(getContext());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mTextView.setTextSize(13);
		mTextView.setTextColor(0xffffffff);
		mTextView.setLayoutParams(params);
		addView(mTextView);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		mPaint.setColor(0xffff0000);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2,
				mPaint);
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ViewGroup parent = getScrollableParent();
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			mHolderEventFlag = !CoverManager.getInstance().isRunning();
//			if (mHolderEventFlag) {
//				if (parent != null)
//					parent.requestDisallowInterceptTouchEvent(true);
//				CoverManager.getInstance().start(this, event.getRawX(),
//						event.getRawY(), mOnDragCompeteListener);
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (mHolderEventFlag) {
//				CoverManager.getInstance().update(event.getRawX(),
//						event.getRawY());
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//		case MotionEvent.ACTION_CANCEL:
//			if (mHolderEventFlag) {
//				if (parent != null)
//					parent.requestDisallowInterceptTouchEvent(false);
//				CoverManager.getInstance().finish(this, event.getRawX(),
//						event.getRawY());
//			}
//			break;
//		}

		return true;
	}

	private ViewGroup getScrollableParent() {
		View target = this;
		while (true) {
			View parent;
			try {
				/**
				 * ViewRootImpl cannot be cast to android.view.View
				 */
				parent = (View) target.getParent();
			} catch (Exception e) {
				return null;
			}
			if (parent == null)
				return null;
			if (parent instanceof ListView || parent instanceof NestedScrollView) {
				return (ViewGroup) parent;
			}
			target = parent;
		}

	}

	public void setOnDragCompeteListener(
			OnDragCompeteListener onDragCompeteListener) {
		mOnDragCompeteListener = onDragCompeteListener;
	}
}
