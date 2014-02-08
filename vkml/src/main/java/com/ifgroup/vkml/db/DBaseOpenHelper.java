package com.ifgroup.vkml.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.utils.ResourceReader;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 10:06 PM
 * May the force be with you always.
 */
public class DBaseOpenHelper extends SQLiteOpenHelper {

    private final Context mContext;

    public DBaseOpenHelper(Context context) {
        super(context, context.getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql = ResourceReader.readResourceAsString(mContext, R.raw.create_db);
        final String[] strings = sql.split(";");
        executeStatements(strings, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            final String sql = ResourceReader.readResourceAsString(mContext, R.raw.delete_db);
            final String[] strings = sql.split(";");
            executeStatements(strings, db);
            onCreate(db);
        }
    }

    private static void executeStatements(String[] strings, SQLiteDatabase db) {
        try {
            db.beginTransaction();
            for (String string : strings) {
                final String str = string.replace("\n", "").trim().replace("\t", "");
                if (str.length() > 0) {
                    db.execSQL(string);
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
    }
}
