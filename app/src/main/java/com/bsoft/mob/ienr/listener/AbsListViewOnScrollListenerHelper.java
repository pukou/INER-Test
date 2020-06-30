package com.bsoft.mob.ienr.listener;

import android.util.SparseArray;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by classichu on 2018/3/19.
 */

public class AbsListViewOnScrollListenerHelper {

    private static SparseArray<List<AbsListView.OnScrollListener>> mOnScrollListenersSparseArray;

    public static void addOnScrollListener(AbsListView absListView, AbsListView.OnScrollListener onScrollListener) {
        int absListViewHashCode = absListView.hashCode();
        if (mOnScrollListenersSparseArray == null) {
            mOnScrollListenersSparseArray = new SparseArray<>();
        }
        if (mOnScrollListenersSparseArray.indexOfKey(absListViewHashCode) < 0) {
            //不存在
            List<AbsListView.OnScrollListener> onScrollListenerList = new ArrayList<>();
            onScrollListenerList.add(onScrollListener);
            mOnScrollListenersSparseArray.put(absListViewHashCode, onScrollListenerList);
        } else {
            //存在
            List<AbsListView.OnScrollListener> onScrollListenerList = mOnScrollListenersSparseArray.get(absListViewHashCode);
            if (onScrollListenerList == null) {
                onScrollListenerList = new ArrayList<>();
            }
            onScrollListenerList.add(onScrollListener);
        }
        //
        resetOnScrollListener(absListView);
    }

    private static void resetOnScrollListener(AbsListView absListView) {
        int absListViewHashCode = absListView.hashCode();
        List<AbsListView.OnScrollListener> onScrollListenerList = mOnScrollListenersSparseArray.get(absListViewHashCode);
        if (onScrollListenerList.isEmpty()) {
            absListView.setOnScrollListener(null);
            return;
        }
        //
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                for (int i = onScrollListenerList.size() - 1; i >= 0; i--) {
                    onScrollListenerList.get(i).onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for (int i = onScrollListenerList.size() - 1; i >= 0; i--) {
                    onScrollListenerList.get(i).onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }

        });
    }

    public static void removeOnScrollListener(AbsListView absListView, AbsListView.OnScrollListener onScrollListener) {
        if (mOnScrollListenersSparseArray == null || onScrollListener == null) {
            return;
        }
        int absListViewHashCode = absListView.hashCode();
        if (mOnScrollListenersSparseArray.indexOfKey(absListViewHashCode) >= 0) {
            //存在
            List<AbsListView.OnScrollListener> onScrollListenerList = mOnScrollListenersSparseArray.get(absListViewHashCode);
            if (onScrollListenerList != null) {
                onScrollListenerList.remove(onScrollListener);
            }
            //
            resetOnScrollListener(absListView);
        }
    }


    public static void clearOnScrollListeners(AbsListView absListView) {
        if (mOnScrollListenersSparseArray != null) {
            int absListViewHashCode = absListView.hashCode();
            if (mOnScrollListenersSparseArray.indexOfKey(absListViewHashCode) >= 0) {
                List<AbsListView.OnScrollListener> onScrollListenerList = mOnScrollListenersSparseArray.get(absListViewHashCode);
                onScrollListenerList.clear();
                //
                resetOnScrollListener(absListView);
            }
        }
    }
}
