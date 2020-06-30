package com.bsoft.mob.ienr.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.MessageCursorTreeAdapter;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.MessageUtils;
import com.classichu.adapter.widget.ClassicEmptyView;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;

/**
 * 消息列表
 *
 * @author Tank
 */
public class MessageFragment extends LeftMenuItemFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ExpandableListView mExpandableListView;

    private MessageCursorTreeAdapter mAdapter;

    private DateChangeObserver observer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer = new DateChangeObserver(null);
    }


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        mExpandableListView = (ExpandableListView) rootLayout
                .findViewById(R.id.id_elv);
        EmptyViewHelper.setEmptyView(mExpandableListView, new ClassicEmptyView.OnEmptyViewClickListener() {
            @Override
            public void onClickEmptyLayout(View view) {
                super.onClickEmptyLayout(view);
                toRefreshData();
            }

            @Override
            public void onClickEmptyView(View view) {
                super.onClickEmptyView(view);
                toRefreshData();
            }
        });
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mExpandableListView);

        initActionBar();
        actionLoader();
        initBroadCast();

    }

    private void actionLoader() {
        showSwipeRefreshLayout();
        getLoaderManager().restartLoader(0, null, MessageFragment.this);
    }

    private void initListView(Cursor topicCursor) {
        mAdapter = new MessageCursorTreeAdapter(topicCursor, getActivity());
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                TextView ListHeader = (TextView) v.findViewById(R.id.id_tv);
                ListHeader.setSelected(!ListHeader.isSelected());
                return true;
            }
        });
        mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Cursor cursor = mAdapter.getChild(groupPosition, childPosition);
                String content = cursor.getString(cursor
                        .getColumnIndex(Database.Message.CONTENT));
                int state = cursor.getInt(cursor
                        .getColumnIndex(Database.Message.STATE));
                long msgId = cursor.getLong(cursor
                        .getColumnIndex(Database.Message.REMOTE_ID));
                int topic = cursor.getInt(cursor
                        .getColumnIndex(Database.Message.TOPIC));
                cursor.close();
                DetailFragment dfragment =  DetailFragment.newInstance(topic, msgId, state,
                        content,new DetailFragment.ClickListener(){

                            @Override
                            public void click() {
                                if (state==0){
                                    new DBTask().execute(msgId);
                                }
                            }
                        });
               /* try {
                    getFragmentManager().beginTransaction()
                            .add(dfragment, "DetailFragment")
                            .commitAllowingStateLoss();
                } catch (Exception ex) {
                    Log.e(Constant.TAG, ex.getMessage(), ex);
                }*/
                DialogFragmentShowHelper.show(getChildFragmentManager(), dfragment, "DetailFragment");

                return true;
            }
        });
        mAdapter.setOnDelListener(new MessageCursorTreeAdapter.OnDelListener() {
            @Override
            public void del(long msgid) {
                //
                new DeleteTask().execute(msgid);
            }
        });
        mExpandableListView.setAdapter(mAdapter);
        mAdapter.refreshData();
    }

    @Override
    protected void toRefreshData() {
        actionLoader();
    }

    /**
     * 初始化action bar
     */
    private void initActionBar() {
        actionBar.setTitle("消息列表");
    }

    protected class DeleteTask extends AsyncTask<Long, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(getString(R.string.deleteing));
        }

        @Override
        protected Boolean doInBackground(Long... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return false;
            }
            Uri url = Database.Message.CONTENT_URI;
            String where = Database.Message.REMOTE_ID + "=?";
            String[] selectionArgs = {String.valueOf(params[0])};
            int num = getActivity().getContentResolver().delete(url, where,
                    selectionArgs);
            return num > 0;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideSwipeRefreshLayout();
            if (result && mAdapter != null) {
                mAdapter.refreshData();
            }
            actionLoader();
        }

    }

    // protected class LoadTask extends AsyncTask<Void, Integer, Cursor> {
    //
    // @Override
    // protected void onPreExecute() {
    // showSwipeRefreshLayout();
    // }
    //
    // @Override
    // protected Cursor doInBackground(Void... params) {
    //
    // // 查询一级录目
    // Uri uri = Database.Message.CONTENT_URI;
    // String[] projection = { BaseColumns._ID, Database.Message.TOPIC };
    // // 属于个人或院级别的
    // String selection = Database.Message.USER + "=? OR "
    // + Database.Message.USER + "=0 GROUP BY "
    // + Database.Message.TOPIC;
    // String[] selectionArgs = { application.user.YHID };
    // String sortOrder = Database.Message.TOPIC + " DESC";
    // Cursor cursor = getActivity().getContentResolver().query(uri,
    // projection, selection, selectionArgs, sortOrder);
    // return cursor;
    // }
    //
    // @Override
    // protected void onPostExecute(Cursor result) {
    // hideSwipeRefreshLayout();
    // tasks.remove(this);
    // mAdapter = new MessageAdapter(result, getActivity());
    // mExpandableListView.setAdapter(mAdapter);
    // }
    //
    // }

    /**
     * 显示消息详情，后期定位到具体业务
     *
     * @author hy
     */
    public static class DetailFragment extends DialogFragment {
        private static ClickListener mClickListener;
        public interface ClickListener{
            void click();
        }
        public static DetailFragment newInstance(int topic, long msgId, int state, String content,ClickListener clickListener) {
            DetailFragment detailFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putInt("topic", topic);
            args.putString("content", content);
            args.putLong("msgId", msgId);
            args.putInt("state", state);
            detailFragment.setArguments(args);
            //
            mClickListener = clickListener;
            return detailFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String content = getArguments().getString("content");
            int topic = getArguments().getInt("topic");
            final int state = getArguments().getInt("state");
            final long msgId = getArguments().getLong("msgId");

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (mClickListener!=null) {
                        mClickListener.click();
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(content)
                    .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getActivity(),
                            MessageUtils.getTopicChars(topic).toString()))
                    // .setTitle(MessageUtils.getTopicChars(topic))
                    .setPositiveButton(android.R.string.ok, onClickListener);
            return builder.create();

        }
    }

    class DBTask extends AsyncTask<Long, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Long... params) {

            if (params == null || params.length < 1) {
                return null;
            }
            Uri uri = Database.Message.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(Database.Message.STATE, 1);
            String where = Database.Message.REMOTE_ID + "=?";
            String[] selectionArgs = {String.valueOf(params[0])};
            getActivity().getContentResolver().update(uri, values, where,
                    selectionArgs);
            return null;
        }

    }

    class DateChangeObserver extends ContentObserver {

        public DateChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            if (mAdapter != null) {
                mAdapter.refreshData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getContentResolver().registerContentObserver(
                Database.Message.CONTENT_URI, true, observer);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (observer != null) {
            getActivity().getContentResolver().unregisterContentObserver(
                    observer);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // 查询一级录目
        Uri uri = Database.Topic.CONTENT_URI;
        String[] projection = {BaseColumns._ID, Database.Topic.TOPIC};

        String sortOrder = Database.Message.TOPIC + " ASC";
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,
                projection, null, null, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        hideSwipeRefreshLayout();
        initListView(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
    }


    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    FastSwitchUtils.switchUser(MessageFragment.this);
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    AppApplication app = (AppApplication) getActivity()
                            .getApplication();
                    if (mAppApplication.sickPersonVo == null) {
                        return;
                    }
                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    }

                }
            }
        };
    }
}
