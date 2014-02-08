package com.ifgroup.vkml.db.table;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Creator: Gusev Ivan (ivan.gusev@altarix.ru)
 * Date: 27.09.13
 * Time: 15:23
 * May the Force be with you, always
 */
public class ContentValuesUtils implements Audio {

    public static ContentValues convert(Cursor cursor) {
        final ContentValues cv = new ContentValues();
        cv.put(_ID, cursor.getInt(cursor.getColumnIndex(_ID)));
        cv.put(AID, cursor.getInt(cursor.getColumnIndex(AID)));
        cv.put(TITLE, cursor.getString(cursor.getColumnIndex(TITLE)));
        cv.put(ARTIST, cursor.getString(cursor.getColumnIndex(ARTIST)));
        cv.put(GENRE, cursor.getString(cursor.getColumnIndex(GENRE)));
        cv.put(EXT_URL, cursor.getString(cursor.getColumnIndex(EXT_URL)));
        cv.put(LOC_URI, cursor.getString(cursor.getColumnIndex(LOC_URI)));
        cv.put(UPTIME, cursor.getLong(cursor.getColumnIndex(UPTIME)));
        cv.put(STATUS, cursor.getString(cursor.getColumnIndex(STATUS)));
        return cv;
    }

    public static ContentValues convertUIFields(Cursor cursor) {
        final ContentValues cv = new ContentValues();
        cv.put(TITLE, cursor.getString(cursor.getColumnIndex(TITLE)));
        cv.put(ARTIST, cursor.getString(cursor.getColumnIndex(ARTIST)));
        cv.put(GENRE, cursor.getString(cursor.getColumnIndex(GENRE)));
        cv.put(DURATION, cursor.getInt(cursor.getColumnIndex(DURATION)));
        return cv;
    }

    public static boolean checkEqualExistence(ContentValues values, ContentValues anotherValues) {
        String key;
        Object value;
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            key = entry.getKey();
            if(!anotherValues.containsKey(key)) continue;
            if ((value = anotherValues.get(key)) != null ? !value.equals(values.get(key)) : values.get(key) != null) {
                return false;
            }

        }
        return true;
    }
}
