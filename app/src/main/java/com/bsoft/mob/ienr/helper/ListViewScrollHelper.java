package com.bsoft.mob.ienr.helper;

import android.support.v4.widget.ListViewCompat;
import android.view.View;
import android.widget.ListView;

/**
 * Created by classichu on 2018/3/20.
 */

public class ListViewScrollHelper {


    public static void smoothScrollToPosition(ListView listView, int position) {
        if (listView == null) {
            return;
        }
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(position);
            }
        });
        //1 smoothScrollToPosition 起作用后 修正位置
        //2 smoothScrollToPosition 不起作用 还能够生效
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(position);
            }
        }, 500);
    }


    public static void smoothScrollToTop(ListView listView) {
        smoothScrollToPosition(listView, 0);
    }

    public static void smoothScrollToBottom(ListView listView) {
        int position = listView.getAdapter().getCount() - 1;
        smoothScrollToPosition(listView, position);
    }

    public static void scrollToPosition(ListView listView, int position) {
        if (listView == null) {
            return;
        }
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(position);
            }
        });

    }


    public static void scrollToTop(ListView listView) {
        scrollToPosition(listView, 0);
    }

    public static void scrollToBottom(ListView listView) {
        int position = listView.getAdapter().getCount() - 1;
        scrollToPosition(listView, position);
    }

    public static void scrollToTop2(ListView listView) {
        if (!listView.isStackFromBottom()) {
            listView.setStackFromBottom(true);
        }
        listView.setStackFromBottom(false);
    }

    public static void scrollToBottom2(ListView listView) {
        if (listView.isStackFromBottom()) {
            listView.setStackFromBottom(false);
        }
        listView.setStackFromBottom(true);
    }

    @Deprecated
    public static void scrollToTop3(ListView listView) {
        int position = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(0);
        int top = 0;
        if (view != null) {
            top = view.getTop() - listView.getPaddingTop();
        }
        int finalTop = top;
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelectionFromTop(position, finalTop);
            }
        });
    }


    public static boolean canScrollListUp(ListView listView) {
        return ListViewCompat.canScrollList(listView, -1);
    }

    public static boolean canScrollListDown(ListView listView) {
        return ListViewCompat.canScrollList(listView, 1);
    }

    @Deprecated
    public static void scrollThePositionItemToTop(ListView listView, int position) {
        int positionNow = listView.getFirstVisiblePosition();
        int positionLast = listView.getLastVisiblePosition();
        View viewNow = listView.getChildAt(positionNow);
        int top = 0;
        if (viewNow != null) {
            top = viewNow.getTop() - listView.getPaddingTop();
        }
        int finalTop = top;
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelectionFromTop(position, finalTop);
            }
        });
    }

}
