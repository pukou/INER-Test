package com.bsoft.mob.ienr.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;

import com.bsoft.mob.ienr.Constant;
import com.classichu.adapter.widget.ClassicEmptyView;

/**
 * Created by louisgeek on 2018/1/4.
 */

public class EmptyViewHelper {
    private static final String TAG = "EmptyViewHelper";
    private static final String NO_DATA = "暂无数据";
    private static final String NO_DATA_AND_CLICK = "暂无数据\n点击重试";
    public static ClassicEmptyView buildEmptyView(Context context) {
        ClassicEmptyView classicEmptyView = new ClassicEmptyView(context);
        classicEmptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        classicEmptyView.setEmptyImage(null);
        classicEmptyView.setEmptyText("暂无数据");
        return classicEmptyView;
    }
    public static ClassicEmptyView setEmptyView(View view) {
        return setEmptyView(view, null, null);
    }

    public static ClassicEmptyView setEmptyView(View view, ClassicEmptyView.OnEmptyViewClickListener onEmptyViewClickListener) {
        return setEmptyView(view, null, onEmptyViewClickListener);
    }

    public static ClassicEmptyView setEmptyView(View view, String text) {
        return setEmptyView(view, text, null);
    }

    public static ClassicEmptyView setEmptyView(View view, String text, ClassicEmptyView.OnEmptyViewClickListener onEmptyViewClickListener) {
        if (view == null) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof ClassicEmptyView) {
                //防止重复添加！！！
                if (Constant.DEBUG){
                Log.e(TAG, "setEmptyView: ClassicEmptyView is added");}
                //throw new RuntimeException("setEmptyView: ClassicEmptyView is added");
                return (ClassicEmptyView) childView;
            }
        }
        //###view.setVisibility(View.INVISIBLE);//需要load data后自行显示
        //
        Context context = view.getContext();
        //
        ClassicEmptyView classicEmptyView = new ClassicEmptyView(context);
        //样式和原列表一致
        classicEmptyView.setLayoutParams(view.getLayoutParams());
        if (onEmptyViewClickListener != null) {
            classicEmptyView.setOnEmptyViewClickListener(onEmptyViewClickListener);
        }
        //处理显示文字
        text = onEmptyViewClickListener == null ? NO_DATA : NO_DATA_AND_CLICK;
        text = Constant.DEBUG_DEVELOP_TEST_DATA ? view.toString() : text;

        classicEmptyView.setEmptyImage(null);
        classicEmptyView.setEmptyText(text);
        //
        int visibility = view.getVisibility();

        //原列表所在位置索引 要么放在原列表前面要么放在原列表后面
        int index = viewGroup.indexOfChild(view);
        if (view instanceof GridView) {
            GridView gridView = (GridView) view;
            gridView.setEmptyView(classicEmptyView);
        } else if (view instanceof ExpandableListView) {
            ExpandableListView expandableListView = (ExpandableListView) view;
            expandableListView.setEmptyView(classicEmptyView);
        } else if (view instanceof ListView) {
            ListView listView = (ListView) view;
            listView.setEmptyView(classicEmptyView);
        }
        //setEmptyView =》updateEmptyStatus =》setVisibility
        //初始的时候 占位
        classicEmptyView.setVisibility(View.INVISIBLE);
        //原列表所在位置索引 要么放在原列表前面要么放在原列表后面
        viewGroup.addView(classicEmptyView, index);
        //setEmptyView -》updateEmptyStatus 后恢复状态
        // view.setVisibility(visibility);
       // Log.i(TAG, "setEmptyView: viewGroup:" + viewGroup);
        //
//        classicEmptyView.setDescendantFocusability(ClassicEmptyView.FOCUS_AFTER_DESCENDANTS);
        return classicEmptyView;

    }
}
