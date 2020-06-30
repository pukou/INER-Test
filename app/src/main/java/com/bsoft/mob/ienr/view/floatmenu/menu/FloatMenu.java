
package com.bsoft.mob.ienr.view.floatmenu.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.util.tools.DimensionTool;
import com.bsoft.mob.ienr.view.floatmenu.FloatMenuManager;
import com.bsoft.mob.ienr.view.floatmenu.FloatMenuUtil;
import com.bsoft.mob.ienr.view.floatmenu.runner.IFloatMenuExpandAction;


public class FloatMenu extends FrameLayout {
    private FloatMenuLayout mMenuLayout;
    private ImageView mIconView;
    private int mPosition;
    private int mItemSize;
    private int mSize;
    private int mDuration = 250;

    private FloatMenuManager mFloatMenuManager;
    private WindowManager.LayoutParams mLayoutParams;
    private boolean isAdded = false;
    private int mButtonSize;
    private FloatMenuCfg mConfig;
    private IFloatMenuExpandAction iFloatMenuExpandAction;

    public FloatMenu(Context context, FloatMenuManager floatMenuManager, FloatMenuCfg config, IFloatMenuExpandAction iFloatMenuExpandAction) {
        super(context);
        mFloatMenuManager = floatMenuManager;
        this.iFloatMenuExpandAction = iFloatMenuExpandAction;
        if (config == null) return;
        mConfig = config;
        mItemSize = mConfig.mItemSize;
        mSize = mConfig.mSize;
        init(context);
        mMenuLayout.setChildSize(mItemSize);
//        this.setBackgroundColor(Color.RED);
        if (mConfig.mBackKeyCanHide) {
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            this.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                        mFloatMenuManager.closeMenu();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void removeViewTreeObserver(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public int getSize() {
        return mSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (mMenuLayout.isExpanded()) {
                    toggle(mDuration);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void attachToWindow(WindowManager windowManager) {
        if (!isAdded) {
            mButtonSize = mFloatMenuManager.getButtonSize();
            toggle(mDuration);
          /*  mLayoutParams.x = mFloatMenuManager.floatbuttonX;
            mLayoutParams.y = mFloatMenuManager.floatbuttonY - mSize / 2;
            mPosition = computeMenuLayout(mLayoutParams);
            Log.i("zzzfffqqq", "attachToWindow: mPosition"+mPosition);
            refreshPathMenu(mPosition);*/
            windowManager.addView(this, mLayoutParams);
            isAdded = true;
        }
    }

    public void detachFromWindow(WindowManager windowManager) {
        if (isAdded) {
            toggle(0);
            mMenuLayout.setVisibility(GONE);
            windowManager.removeView(this);
            isAdded = false;
        }
    }

    private void addMenuLayout(Context context) {
        mMenuLayout = new FloatMenuLayout(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mSize, mSize);
        addView(mMenuLayout, layoutParams);
        mMenuLayout.setVisibility(INVISIBLE);
    }

    private void addControllLayout(Context context) {
        mIconView = new ImageView(context);
        LayoutParams sublayoutParams = new LayoutParams(mButtonSize, mButtonSize);
        addView(mIconView, sublayoutParams);
    }

    private void init(Context context) {
        mLayoutParams = FloatMenuUtil.getMyLayoutParams(mConfig.mBackKeyCanHide);
        mLayoutParams.height = mSize;
        mLayoutParams.width = mSize;
        addMenuLayout(context);
        addControllLayout(context);
        mIconView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
    }

    public void closeMenu() {
        if (mMenuLayout.isExpanded()) {
            toggle(mDuration);
        }
    }

    public void remove() {
        mFloatMenuManager.reset();
        mMenuLayout.setExpand(false);
    }

    private void toggle(final int duration) {
        ///!!!!!!!!!!!!!!!!!!!!!!!!
        mLayoutParams.x = mFloatMenuManager.floatbuttonX;
        mLayoutParams.y = mFloatMenuManager.floatbuttonY - mSize / 2;
        mPosition = computeMenuLayout(mLayoutParams);
//        Log.i("zzzfffqqq", "attachToWindow: mPosition" + mPosition);
        refreshPathMenu(mPosition);
        ///!!!!!!!!!!!!!!!!!!!!!!!!
        //duration==0 indicate that close the menu, so if it has closed, do nothing.
        if (!mMenuLayout.isExpanded() && duration <= 0) return;
        mMenuLayout.setVisibility(VISIBLE);

        if (getWidth() == 0) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mMenuLayout.switchState(mPosition, duration, iFloatMenuExpandAction);
                    removeViewTreeObserver(this);
                }
            });
        } else {
            mMenuLayout.switchState(mPosition, duration, iFloatMenuExpandAction);
        }


    }

    public void addItem(final IFloatMenuItem menuItem) {
        if (mConfig == null) return;
        if (AppApplication.getInstance().userConfig.floatMenuShowByIcon){
            ImageView imageview = new ImageView(getContext());
            Drawable drawable = ContextCompatHelper.getDrawable(getContext(), menuItem.resid, 0);
            ViewCompat.setBackground(imageview, drawable);
            //  #####      imageview.setBackgroundResource(menuItem.resid);
            mMenuLayout.addView(imageview);
            imageview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                    menuItem.actionClick(v, menuItem.resid);
                }
            });
            return;
        }
        //支持显示文字
        if (menuItem instanceof FloatMenuItem) {
            ImageView imageview = new ImageView(getContext());
            Drawable drawable = ContextCompatHelper.getDrawable(getContext(), menuItem.resid, 0);
            ViewCompat.setBackground(imageview, drawable);
            //  #####      imageview.setBackgroundResource(menuItem.resid);
            mMenuLayout.addView(imageview);
            imageview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                    menuItem.actionClick(v, menuItem.resid);
                }
            });
        } else if (menuItem instanceof TextFloatMenuItem) {
            TextFloatMenuItem textFloatMenuItem = (TextFloatMenuItem) menuItem;
            TextView textView = ViewBuildHelper.buildTextView(getContext(), textFloatMenuItem.textStr);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
//            textView.setIncludeFontPadding(false);
            textView.setPadding(5,5,5,5);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    DimensionTool.getDimensionPx(getContext(),R.dimen.classic_text_size_secondary));
            textView.setBackgroundResource(R.drawable.shape_menu_item_o_colored);
            mMenuLayout.addView(textView);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                    menuItem.actionClick(v, menuItem.resid);
                }
            });
        }


    }

    public void removeAllItemViews() {
        mMenuLayout.removeAllViews();
    }

    /**
     * 根据按钮位置改变子菜单方向
     */
    public void refreshPathMenu(int position) {
        LayoutParams menuLp = (LayoutParams) mMenuLayout.getLayoutParams();
        LayoutParams iconLp = (LayoutParams) mIconView.getLayoutParams();

        switch (position) {
            case LEFT_TOP://左上
                iconLp.gravity = Gravity.LEFT | Gravity.TOP;
                menuLp.gravity = Gravity.LEFT | Gravity.TOP;
                mMenuLayout.setArc(0, 90, position);
                break;
            case LEFT_CENTER://左中
                iconLp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                menuLp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                mMenuLayout.setArc(270, 270 + 180, position);
                break;
            case LEFT_BOTTOM://左下
                iconLp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                menuLp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                mMenuLayout.setArc(270, 360, position);
                break;
            case RIGHT_TOP://右上
                iconLp.gravity = Gravity.RIGHT | Gravity.TOP;
                menuLp.gravity = Gravity.RIGHT | Gravity.TOP;
                mMenuLayout.setArc(90, 180, position);
                break;
            case RIGHT_CENTER://右中
                iconLp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                menuLp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                mMenuLayout.setArc(90, 270, position);
                break;
            case RIGHT_BOTTOM://右下
                iconLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                menuLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                mMenuLayout.setArc(180, 270, position);
                break;

            case CENTER_TOP://上中
                iconLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                menuLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                mMenuLayout.setArc(0, 180, position);
                break;
            case CENTER_BOTTOM://下中
                iconLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                menuLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                mMenuLayout.setArc(180, 360, position);
                break;
            case CENTER:
                iconLp.gravity = Gravity.CENTER;
                menuLp.gravity = Gravity.CENTER;
                mMenuLayout.setArc(0, 360, position);
                break;
        }
        mIconView.setLayoutParams(iconLp);
        mMenuLayout.setLayoutParams(menuLp);
        mMenuLayout.setVisibility(VISIBLE);
    }

    /**
     * 计算菜单中各个view的位置
     *
     * @return
     */
    public int computeMenuLayout(WindowManager.LayoutParams layoutParams) {
        int position = FloatMenu.RIGHT_CENTER;
        final int halfButtonSize = mButtonSize / 2;
        final int screenWidth = mFloatMenuManager.mScreenWidth;
        final int screenHeight = mFloatMenuManager.mScreenHeight;
        final int floatbuttonCenterY = mFloatMenuManager.floatbuttonY + halfButtonSize;

        int wmX = mFloatMenuManager.floatbuttonX;
        int wmY = floatbuttonCenterY;

        if (wmX <= screenWidth / 3) //左边  竖区域
        {
            wmX = 0;
            if (wmY <= mSize / 2) {
                position = FloatMenu.LEFT_TOP;//左上
                wmY = floatbuttonCenterY - halfButtonSize;
            } else if (wmY > screenHeight - mSize / 2) {
                position = FloatMenu.LEFT_BOTTOM;//左下
                wmY = floatbuttonCenterY - mSize + halfButtonSize;
            } else {
                position = FloatMenu.LEFT_CENTER;//左中
                wmY = floatbuttonCenterY - mSize / 2;
            }
        } else if (wmX >= screenWidth * 2 / 3)//右边竖区域
        {
            wmX = screenWidth - mSize;
            if (wmY <= mSize / 2) {
                position = FloatMenu.RIGHT_TOP;//右上
                wmY = floatbuttonCenterY - halfButtonSize;
            } else if (wmY > screenHeight - mSize / 2) {
                position = FloatMenu.RIGHT_BOTTOM;//右下
                wmY = floatbuttonCenterY - mSize + halfButtonSize;
            } else {
                position = FloatMenu.RIGHT_CENTER;//右中
                wmY = floatbuttonCenterY - mSize / 2;
            }
        }
        layoutParams.x = wmX;
        layoutParams.y = wmY;
        return position;
    }

    public static final int LEFT_TOP = 1;
    public static final int CENTER_TOP = 2;
    public static final int RIGHT_TOP = 3;
    public static final int LEFT_CENTER = 4;
    public static final int CENTER = 5;
    public static final int RIGHT_CENTER = 6;
    public static final int LEFT_BOTTOM = 7;
    public static final int CENTER_BOTTOM = 8;
    public static final int RIGHT_BOTTOM = 9;
}
