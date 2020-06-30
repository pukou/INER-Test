package com.bsoft.mob.ienr.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Content Provider ，对UriMatch,GetType 进行了简化
 */
public class DBContentProvider extends ContentProvider {

    private DBSQLOpenHelper helper;
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "ienr.db";


    @Override
    public boolean onCreate() {

        helper = new DBSQLOpenHelper(this.getContext(), DATABASE_NAME, null,
                DATABASE_VERSION);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = helper.getWritableDatabase();
        String segment = uri.getLastPathSegment();
        Cursor cursor = db.query(segment, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransaction();
            String table = uri.getPathSegments().get(
                    uri.getPathSegments().size() - 1);

            long id = db.insert(table, null, values);
            db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
            // 时提交当前事务，如果不调用此方法会回滚事务
            return ContentUris.withAppendedId(uri, id);

        } finally {
            db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();
        int i = -1;
        try {
            db.beginTransaction();
            String table = uri.getLastPathSegment();
            i = db.delete(table, selection, selectionArgs);
            db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
        } finally {
            db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
        }
        return i;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();
        int row = -1;

        try {
            db.beginTransaction();
            String table = uri.getPathSegments().get(
                    uri.getPathSegments().size() - 1);

            if (!table.contains("t_")) {
                table = uri.getPathSegments().get(
                        uri.getPathSegments().size() - 2);
            }
            row = db.update(table, values, selection, selectionArgs);
            db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
            // 时提交当前事务，如果不调用此方法会回滚事务

        } finally {
            db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
        }
        return row;
    }


}
