package com.ifgroup.vkml.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 10:22 PM
 * May the force be with you always.
 */
public class AudioContentProvider extends ContentProvider {

    public static final String AUTHORITIES = "com.ifgroup.vkloader.audioprovider";

    public static final String AUDIO_TABLE = "audio";
    public static final String AUDIO_ARTIST = "artist";
    public static final String AUDIO_TITLE = "title";

    public static final Uri AUDIO_URI = Uri.parse("content://" + AUTHORITIES + "/" + AUDIO_TABLE);
    public static final int URI_AUDIO = 1;
    public static final int URI_AUDIO_ID = 2;

    public static final String AUDIO_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITIES + "." + AUDIO_TABLE;
    public static final String AUDIO_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITIES + "." + AUDIO_TABLE;

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITIES, AUDIO_TABLE, URI_AUDIO);
        sUriMatcher.addURI(AUTHORITIES, AUDIO_TABLE + "/#", URI_AUDIO_ID);
    }

    private DBaseOpenHelper mDBaseOpenHelper;
    private SQLiteDatabase mDataBase;

    @Override
    public boolean onCreate() {
        mDBaseOpenHelper = new DBaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case URI_AUDIO:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = AUDIO_ARTIST + " ASC";
                }
                break;
            case URI_AUDIO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDataBase = mDBaseOpenHelper.getWritableDatabase();
        final Cursor cursor = mDataBase.query(AUDIO_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), AUDIO_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != URI_AUDIO)
            throw new IllegalArgumentException("Wrong URI: " + uri);
        mDataBase = mDBaseOpenHelper.getWritableDatabase();
        final long rowID = mDataBase.insert(AUDIO_TABLE, null, values);
        final Uri resultUri = ContentUris.withAppendedId(AUDIO_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case URI_AUDIO:
                break;
            case URI_AUDIO_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDataBase = mDBaseOpenHelper.getWritableDatabase();
        final int cnt = mDataBase.delete(AUDIO_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case URI_AUDIO:
                break;
            case URI_AUDIO_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDataBase = mDBaseOpenHelper.getWritableDatabase();
        return mDataBase.update(AUDIO_TABLE, values, selection, selectionArgs);
    }

    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case URI_AUDIO:
                return AUDIO_CONTENT_TYPE;
            case URI_AUDIO_ID:
                return AUDIO_CONTENT_ITEM_TYPE;
        }
        return null;
    }
}
