package com.aspire.firstprovider.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by lijun on 2015/12/10.
 */
public class MyContentProvider extends ContentProvider{

    private static final String TAG = MyContentProvider.class.getSimpleName();

    private static final boolean DEBUGABLE = true;

    private static final String DB_NAME = "myproviderdb";

    private static final String PACKAGE_NAME = MyContentProvider.class.getPackage().getName();

    private MySQLiteOpenHelper mOpenHelper;

    private SQLiteDatabase mDb;

    private static final int PERSON_LIST = 1;
    private static final int PERSON_ITEM = 2;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(CustomContract.AUTHORITY, CustomContract.Person.LOWER_CLASS_NAME, PERSON_LIST);
        URI_MATCHER.addURI(CustomContract.AUTHORITY, CustomContract.Person.LOWER_CLASS_NAME + "/#", PERSON_ITEM);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MySQLiteOpenHelper(getContext(), DB_NAME, null, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mDb = mOpenHelper.getReadableDatabase();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case PERSON_ITEM:
            case PERSON_LIST:
                table = CustomContract.Person.TABLE_NAME;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CustomContract.Person.DEFAULT_SORT_ORDER;
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for query: " + uri);
        }
        return mDb.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case PERSON_LIST:
                return CustomContract.Person.CONTENT_TYPE;
            case PERSON_ITEM:
                return CustomContract.Person.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return super.getStreamTypes(uri, mimeTypeFilter);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDb = mOpenHelper.getWritableDatabase();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case PERSON_LIST:
                table = CustomContract.Person.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for inserting: " + uri);
        }
        long id = mDb.insertOrThrow(table, null, values);
        Uri newUri = getUriFromID(id, uri);
        return null == newUri ? null : newUri;
    }

    private Uri getUriFromID(long id, Uri uri) {
        if (-1 != id) {
            return ContentUris.withAppendedId(uri, id);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mDb = mOpenHelper.getWritableDatabase();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case PERSON_ITEM:
                table = CustomContract.Person.TABLE_NAME;
                String idStr = uri.getLastPathSegment();
                String where = CustomContract.Person.COLUMN_ID  + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    selection += where;
                }
                break;
            case PERSON_LIST:
                table = CustomContract.Person.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for deleting: " + uri);
        }
        return mDb.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        mDb = mOpenHelper.getWritableDatabase();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case PERSON_ITEM:
                table = CustomContract.Person.TABLE_NAME;
                String idStr = uri.getLastPathSegment();
                String where = CustomContract.Person.COLUMN_ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    selection += where;
                }
                break;
            case PERSON_LIST:
                table = CustomContract.Person.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported updating for uri: " + uri);
        }
        return mDb.update(table, values, selection, selectionArgs);
    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private final String TAG = MySQLiteOpenHelper.class.getSimpleName();
        private static final String SQL_CREATE_TABLE = "create table "
                + CustomContract.Person.TABLE_NAME + " ("
                + CustomContract.Person.COLUMN_ID + " integer primary key not null, "
                + CustomContract.Person.COLUMN_NAME + " text not null, "
                + CustomContract.Person.COLUMN_AGE + " integer)";

        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (DEBUGABLE) {
                Log.d(TAG, "being to create database");
            }
            // create table myproviderdb
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
