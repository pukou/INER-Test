package com.bsoft.mob.ienr.helper;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.bsoft.mob.ienr.listener.AbsListViewOnScrollListenerHelper;

/**
 * Created by Classichu on 2018/1/30.
 */

public class SwipeRefreshEnableHelper {
    private static final String TAG = "SwipeRefreshHelper";

    public static void setSwipeEnable(final SwipeRefreshLayout swipeRefreshLayout, AbsListView absListView) {
        if (swipeRefreshLayout == null || absListView == null) {
            //Log.i(TAG, "setSwipeEnable: swipeRefreshLayout:" + swipeRefreshLayout);
           // Log.i(TAG, "setSwipeEnable: absListView:" + absListView);
            return;
        }
       /* absListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (view != null && view.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                Log.i(TAG, swipeRefreshLayout + "swipeRefreshLayout: setEnabled:" + enable);

                if (absListView.getVisibility() == View.VISIBLE) {
                    swipeRefreshLayout.setEnabled(enable);
                } else {
                    //此处为了显示 emptyview 时候也可以下拉
                    if (absListView.getEmptyView() != null &&
                            absListView.getEmptyView().getVisibility() == View.VISIBLE) {
                        //如果
                        swipeRefreshLayout.setEnabled(true);
                    }
                }

            }
        });*/
        AbsListViewOnScrollListenerHelper.addOnScrollListener(absListView, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (view != null && view.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
               // Log.i(TAG, swipeRefreshLayout + "swipeRefreshLayout: setEnabled:" + enable);

                if (absListView.getVisibility() == View.VISIBLE) {
                    swipeRefreshLayout.setEnabled(enable);
                } else {
                    //此处为了显示 emptyview 时候也可以下拉
                    if (absListView.getEmptyView() != null &&
                            absListView.getEmptyView().getVisibility() == View.VISIBLE) {
                        //如果
                        swipeRefreshLayout.setEnabled(true);
                    }
                }
            }
        });
    }

    @Deprecated  // 使用 ViewPagerSwipeRefreshLayout 代替
    public static void setSwipeEnable(final SwipeRefreshLayout swipeRefreshLayout, ViewPager viewPager) {
        if (swipeRefreshLayout == null || viewPager == null) {
            Log.e(TAG, "setSwipeEnable: swipeRefreshLayout:" + swipeRefreshLayout);
            Log.e(TAG, "setSwipeEnable: viewPager:" + viewPager);
            return;
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                boolean enable = false;
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    //滑动状态
                    enable = false;
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    //滑动后自然停下来的状态
                    enable = false;
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //停止状态
                    enable = true;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }

    @Deprecated //使用  NestedScrollView 代替
    public static void setSwipeEnable(final SwipeRefreshLayout swipeRefreshLayout, final ScrollView scrollView) {
        if (swipeRefreshLayout == null || scrollView == null) {
            Log.e(TAG, "setSwipeEnable: swipeRefreshLayout:" + swipeRefreshLayout);
            Log.e(TAG, "setSwipeEnable: scrollView:" + scrollView);
            return;
        }
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                boolean enable = scrollView.getScrollY() == 0;
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }

  /*  public static void setSwipeEnable(final SwipeRefreshLayout swipeRefreshLayout, final HorizontalScrollView horizontalScrollView) {
        if (swipeRefreshLayout == null || horizontalScrollView == null) {
            Log.e(TAG, "setSwipeEnable: swipeRefreshLayout:" + swipeRefreshLayout);
            Log.e(TAG, "setSwipeEnable: scrollView:" + horizontalScrollView);
            return;
        }
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                boolean enable = scrollView.getScrollY() == 0;
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }*/
}
