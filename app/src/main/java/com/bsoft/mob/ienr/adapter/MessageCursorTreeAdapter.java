package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.MessageUtils;

import java.util.Date;

/**
 * Created by Classichu on 2018/2/1.
 */

public class MessageCursorTreeAdapter extends CursorTreeAdapter {
    private Context mContext;
    private Cursor mTopicCursor;

    public MessageCursorTreeAdapter(Cursor topicCursor, Context context) {
        super(topicCursor, context);
        mContext = context;
        mTopicCursor = topicCursor;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        String topic = groupCursor.getString(1);

        Uri uri = Database.Message.CONTENT_URI;
        String[] projection = {BaseColumns._ID, Database.Message.CONTENT,
                Database.Message.RECEIVE_TIME, Database.Message.STATE,
                Database.Message.REMOTE_ID, Database.Message.TOPIC,
                Database.Message.USER};
        // 属于个人或院级别的
        String selection = Database.Message.TOPIC + "=? AND ("
                + Database.Message.USER + "=?  OR  "
                + Database.Message.USER + "=0)";
        String[] selectionArgs = {
                topic,
                queryUser_ID(AppApplication.getInstance().user.YHID, AppApplication.getInstance().jgId)};

        String sortOrder = Database.Message.RECEIVE_TIME + " DESC";
        Cursor cursor = mContext.getContentResolver().query(uri,
                projection, selection, selectionArgs, sortOrder);
        return cursor;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor,
                                boolean isExpanded, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_list_group_primary, parent,
                false);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor topicCursor,
                                 boolean isExpanded) {

        int topic = topicCursor.getInt(topicCursor
                .getColumnIndex(Database.Message.TOPIC));
        TextView textView = (TextView) view.findViewById(R.id.id_tv);
        textView.setText(MessageUtils.getTopicChars(topic));
    }

    @Override
    protected View newChildView(Context context, Cursor cursor,
                                boolean isLastChild, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_list_message, parent, false);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor,
                                 boolean isLastChild) {

        ViewHolder holder;
        Object tag = view.getTag();
        if (tag == null) {
            holder = new ViewHolder();
            holder.dltBtn = (ImageView) view
                    .findViewById(R.id.msg_dlt_ibtn);
            holder.mContentTxt = (TextView) view
                    .findViewById(R.id.msg_abstract_txt);
            holder.timeTxt = (TextView) view
                    .findViewById(R.id.msg_time_txt);
            holder.stateTxt = (TextView) view
                    .findViewById(R.id.msg_state_txt);
            holder.dltBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long msgid = (Long) v.getTag();
                    if (onDelListener != null) {
                        onDelListener.del(msgid);
                    }
                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) tag;
        }

        String content = cursor.getString(cursor
                .getColumnIndex(Database.Message.CONTENT));
        String time = cursor.getString(cursor
                .getColumnIndex(Database.Message.RECEIVE_TIME));
        time = DateUtil.format_yyyyMMdd_HHmmss.format(new Date(new Long(time)));
        int state = cursor.getInt(cursor
                .getColumnIndex(Database.Message.STATE));
        long msgId = cursor.getLong(cursor
                .getColumnIndex(Database.Message.REMOTE_ID));

        holder.mContentTxt.setText(content);
        holder.timeTxt.setText(time);
        holder.stateTxt.setText(state != 0 ? "已读" : "未读");
        holder.dltBtn.setTag(msgId);

    }


    class ViewHolder {
        ImageView dltBtn;
        TextView mContentTxt;
        TextView timeTxt;
        TextView stateTxt;
    }

    public OnDelListener onDelListener;

    public void setOnDelListener(OnDelListener onDelListener) {
        this.onDelListener = onDelListener;
    }

    public interface OnDelListener {
        void del(long msgid);
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    private String queryUser_ID(String yhid, String jgid) {

        Uri url = Database.User.CONTENT_URI;
        String[] projection = {BaseColumns._ID};
        String selection = Database.User.REMOTE_ID + "=? AND "
                + Database.User.AGENT_ID + "=?";
        String[] selectionArgs = {yhid, jgid};
        Cursor cursor = mContext.getContentResolver().query(url,
                projection, selection, selectionArgs, null);

        int _id = 0;
        if (cursor.moveToNext()) {
            _id = cursor.getInt(0);
        }
        cursor.close();
        return String.valueOf(_id);

    }
}
