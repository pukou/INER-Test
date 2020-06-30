package com.bsoft.mob.ienr.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

/**
 * Created by Classichu on 2018/2/28.
 */

public class RecyclerViewHelper {
    public static void init(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setBackgroundResource(R.drawable.shape_classic_bg_shadow);
        //hideLastDivider
        RecyclerViewDivider.with(context).color(Color.parseColor("#21000000")).hideLastDivider().build().addTo(recyclerView);
    }
}
