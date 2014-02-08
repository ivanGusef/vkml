package com.ifgroup.vkml.client;

import android.content.*;
import android.database.Cursor;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.client.service.DownloaderService;
import com.ifgroup.vkml.db.AudioContentProvider;
import com.ifgroup.vkml.db.table.Audio;
import com.ifgroup.vkml.db.table.ContentValuesUtils;
import com.ifgroup.vkml.db.table.Genre;
import com.ifgroup.vkml.db.table.Status;
import com.ifgroup.vkml.preferences.PreferencesManager;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/25/13
 * Time: 9:44 PM
 * May the force be with you always.
 */
public class VkAudioManager {

    public static final String SERVER_URL = "https://api.vk.com/method/%s?%s&access_token=%s";

    private static final String KEY_ID = "aid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_GENRE_ID = "genre";
    private static final String KEY_URL = "url";

    private static final int CONNECT_TIMEOUT = 60000; //1 minute


    private static VkAudioManager sVkAudioManager;

    public static VkAudioManager getInstance(Context mContext) {
        if (sVkAudioManager == null) {
            sVkAudioManager = new VkAudioManager(mContext);
        }
        return sVkAudioManager;
    }

    private final Context mContext;
    private final PreferencesManager mPreferencesManager;
    private final ContentResolver mContentResolver;

    private boolean mFirstLoading = true;

    private static AtomicBoolean locker = new AtomicBoolean(true);

    public static void lock() {
        locker.set(false);
    }

    public static void unlock() {
        locker.set(true);
    }

    private VkAudioManager(Context mContext) {
        this.mContext = mContext;
        mPreferencesManager = PreferencesManager.getInstance(mContext);
        mContentResolver = mContext.getContentResolver();
    }

    public void reload() throws IOException {
        mFirstLoading = mPreferencesManager.get(C.Pref.FIRST_LOADING, true);
        final JsonParser parser = new JsonFactory().createJsonParser(doGet(C.API.AUDIO_GET, null));
        final long uptime = System.currentTimeMillis();
        readAllAudio(parser, uptime);
        parser.close();
        mContentResolver.delete(AudioContentProvider.AUDIO_URI, Audio.UPTIME + " < ?",
                new String[]{String.valueOf(uptime)});
        if (mFirstLoading && locker.get()) mPreferencesManager.save(C.Pref.FIRST_LOADING, false);
    }

    private void readAllAudio(JsonParser parser, long uptime) throws IOException {
        final JsonToken token = parser.nextToken();
        if (token == JsonToken.START_OBJECT) {
            readAudioResponse(parser, token, uptime);
        }
    }

    private void readAudioResponse(JsonParser parser, JsonToken token, long uptime) throws IOException {
        while (token != JsonToken.END_OBJECT) {
            token = parser.nextToken();
            if (token == JsonToken.START_ARRAY) {
                readAudio(parser, token, uptime);
            }
        }
    }

    private void readAudio(JsonParser parser, JsonToken token, long uptime) throws IOException {
        while (token != JsonToken.END_ARRAY) {
            token = parser.nextToken();
            if (token == JsonToken.START_OBJECT) {
                final ContentValues cv = new ContentValues();
                cv.put(Audio.UPTIME, uptime);
                readAudioFields(parser, token, cv);
                if (!cv.containsKey(Audio.GENRE)) cv.put(Audio.GENRE, Genre.UNKNOWN.name());
                if (locker.get()) saveOrUpdate(cv);
            }
        }
    }

    private void readAudioFields(JsonParser parser, JsonToken token, ContentValues cv) throws IOException {
        while (token != JsonToken.END_OBJECT) {
            token = parser.nextToken();
            if (token == JsonToken.FIELD_NAME) {
                final String fieldName = parser.getCurrentName();
                parser.nextToken();
                if (fieldName.equals(KEY_ID)) {
                    cv.put(Audio.AID, parser.getIntValue());
                } else if (fieldName.equals(KEY_ARTIST)) {
                    cv.put(Audio.ARTIST, parser.getText());
                } else if (fieldName.equals(KEY_TITLE)) {
                    cv.put(Audio.TITLE, parser.getText());
                } else if (fieldName.equals(KEY_GENRE_ID)) {
                    cv.put(Audio.GENRE, Genre.getById(parser.getIntValue()).name());
                } else if (fieldName.equals(KEY_DURATION)) {
                    cv.put(Audio.DURATION, parser.getIntValue() * 1000);
                } else if (fieldName.equals(KEY_URL)) {
                    cv.put(Audio.EXT_URL, parser.getText());
                }
            }
        }
    }

    private void saveOrUpdate(ContentValues cv) {
        final Cursor cursor = mContentResolver.query(AudioContentProvider.AUDIO_URI, null,
                Audio.AID + " = ?", new String[]{cv.getAsString(Audio.AID)}, null);
        if (cursor != null && cursor.moveToFirst()) {
            //update
            mContentResolver.update(AudioContentProvider.AUDIO_URI, cv, Audio._ID + " = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex(Audio._ID))});
            if (!ContentValuesUtils.checkEqualExistence(ContentValuesUtils.convertUIFields(cursor), cv)) {
                mContentResolver.notifyChange(AudioContentProvider.AUDIO_URI, null);
            }
        } else {
            //insert
            cv.put(Audio.STATUS, Status.REMOTE.name());
            cv.put(Audio._ID, ContentUris.parseId(mContentResolver.insert(AudioContentProvider.AUDIO_URI, cv)));
            if (mPreferencesManager.get(C.Pref.AUTODOWNLOAD, true) && !mFirstLoading) {
                startDownloading(cv);
            }
        }
        closeCursor(cursor);
    }

    private InputStream doGet(String method, List<NameValuePair> params) throws IOException {
        return createClient().execute(new HttpGet(constructRequest(method, params))).getEntity().getContent();
    }

    private String constructRequest(String method, List<NameValuePair> params) {
        final String paramsStr = params != null && params.size() > 0 ? URLEncodedUtils.format(params, C.App.CHARSET) : "";
        return String.format(SERVER_URL, method, paramsStr, mPreferencesManager.get(C.Pref.ACCESS_TOKEN, null));
    }

    private HttpClient createClient() {
        final HttpParams httpParameters = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(httpParameters, C.App.CHARSET);
        HttpProtocolParams.setHttpElementCharset(httpParameters, C.App.CHARSET);
        HttpConnectionParams.setSoTimeout(httpParameters, CONNECT_TIMEOUT);
        return new DefaultHttpClient(httpParameters);
    }

    private void startDownloading(ContentValues values) {
        final Intent intent = new Intent(mContext, DownloaderService.class);
        intent.putExtra(C.Extra.URL, values.getAsString(Audio.EXT_URL));
        intent.putExtra(C.Extra.ID, values.getAsLong(Audio._ID));
        intent.putExtra(C.Extra.ARTIST, values.getAsString(Audio.ARTIST));
        intent.putExtra(C.Extra.TITLE, values.getAsString(Audio.TITLE));
        mContext.startService(intent);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) cursor.close();
    }
}
