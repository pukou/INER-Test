package com.bsoft.mob.ienr.helper;

import android.util.Pair;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewHelper {
    public  static  Pair<Integer, Integer> getItemWidthHeightAfterSetAdapter(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return null;
        }
        int totalHeight = 0;
        int maxWidth = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View listItemView = listAdapter.getView(i, null, listView);
            listItemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItemView.getMeasuredHeight();
            int width = listItemView.getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        int width = maxWidth;
        int height = totalHeight + (listView.getDividerHeight() * (count - 1));
        return Pair.create(width, height);
    }

}
