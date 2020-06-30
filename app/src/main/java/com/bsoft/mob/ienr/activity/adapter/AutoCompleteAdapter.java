package com.bsoft.mob.ienr.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.TextView;

import com.bsoft.mob.ienr.db.Database;


/**
 * 用户名自动提示adapter
 */
public class AutoCompleteAdapter extends CursorAdapter implements Filterable {

    // private Cursor cursor;
    private Context mContext;

    public AutoCompleteAdapter(Context context, Cursor c) {
        super(context, c);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate(
                android.R.layout.simple_dropdown_item_1line, parent, false);
        if (android.os.Build.VERSION.SDK_INT < 11) {
			view.setTextColor(Color.BLACK);
		}
        view.setText(cursor.getString(1));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int index = cursor.getColumnIndex(Database.User.USER_NAME);
        ((TextView) view).setText(cursor.getString(index));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {

        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }
        String[] projection = {BaseColumns._ID, Database.User.USER_NAME};
        String where = "UPPER(" + Database.User.USER_NAME + ") GLOB ?";
        String constraintStr = constraint == null ? "" : constraint.toString();
        String[] to = new String[]{"*" + constraintStr.toUpperCase()
                + "*"};

        Cursor cursor = mContext.getContentResolver().query(
                Database.User.CONTENT_URI, projection, where, to, null);
        return cursor;
    }

    @Override
    public String convertToString(Cursor cursor) {
        return cursor.getString(1);
    }

}
