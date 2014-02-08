package com.ifgroup.vkml.client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.client.service.FileRemoveService;
import com.ifgroup.vkml.db.AudioContentProvider;
import com.ifgroup.vkml.db.table.Audio;
import com.ifgroup.vkml.gui.AudioListActivity;
import com.ifgroup.vkml.preferences.PreferencesManager;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/27/13
 * Time: 11:12 PM
 * May the force be with you always.
 */
public class DownloadTask extends AsyncTask<String, Integer, DownloadResult> {

    private static final int NOTIFICATION_ID = 2;
    private static final String EXTENSION = ".mp3";

    private final Context mContext;

    private final PreferencesManager mPreferencesManager;
    private final NotificationManager mNotificationManager;

    private final NotificationCompat.Builder mBuilder;

    private final long mId;
    private final String mArtist;
    private final String mTitle;

    private int mThreadId;
    private File mDstFile;

    private final WeakReference<DownloadExecutor> mOwnerExecutor;

    public DownloadTask(Context context, DownloadExecutor downloadExecutor, long id, String artist, String title) {
        mContext = context;
        mId = id;
        mArtist = artist;
        mTitle = title;
        mOwnerExecutor = new WeakReference<DownloadExecutor>(downloadExecutor);
        mPreferencesManager = PreferencesManager.getInstance(context);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }

    @Override
    protected void onPreExecute() {
        DownloadExecutor executor;
        if ((executor = mOwnerExecutor.get()) != null) {
            mThreadId = executor.getNextId();
        }
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(mArtist + "-" + mTitle);
        mBuilder.setContentText(mContext.getString(R.string.loading));
        mBuilder.setProgress(100, 0, true);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setOngoing(true);
        final Intent intent = new Intent(mContext, AudioListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(mThreadId, mBuilder.build());

        ContentValues cv = new ContentValues();
        cv.put(Audio.STATUS, com.ifgroup.vkml.db.table.Status.LOADING.name());
        mContext.getContentResolver().update(AudioContentProvider.AUDIO_URI, cv,
                Audio._ID + " = ?", new String[]{String.valueOf(mId)});
        mContext.getContentResolver().notifyChange(AudioContentProvider.AUDIO_URI, null);
    }

    @Override
    protected DownloadResult doInBackground(String... params) {
        publishProgress(0);

        final DownloadResult result = new DownloadResult();
        final String audioUrl = params[0];

        final String fileName = prepareFileName(mArtist + "-" + mTitle + EXTENSION);

        final File dstFolder = new File(mPreferencesManager.get(C.Pref.DEST_FOLDER,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()));
        mDstFile = new File(dstFolder, fileName);

        result.uri = Uri.fromFile(mDstFile);
        if(isCancelled()) return result;
        try {
            if (!dstFolder.exists() && !dstFolder.mkdirs()) {
                throw new FileNotFoundException(mContext.getString(R.string.e_folder_not_exists));
            }

            final URL url = new URL(audioUrl);
            final URLConnection urlConnection = url.openConnection();
            final int lengthOfFile = urlConnection.getContentLength();
            final InputStream input = new BufferedInputStream(url.openStream(), 8192);
            final OutputStream output = new FileOutputStream(mDstFile);

            final byte data[] = new byte[1024];
            long total = 0;
            int count;

            int limiter = 0;
            int percent;

            while ((count = input.read(data)) != -1) {
                total += count;

                percent = (int) ((total * 100) / lengthOfFile);
                if (percent - limiter > 4) {
                    limiter = percent;
                    publishProgress(percent);
                }

                output.write(data, 0, count);
                if(isCancelled()) return result;
            }

            output.flush();

            output.close();
            input.close();
        } catch (IOException e) {
            Log.e("DownloadTask -> doInBackground", e.getMessage(), e);
            result.errorMessage = e.getMessage();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mBuilder.setProgress(100, values[0], false);
        mNotificationManager.notify(mThreadId, mBuilder.build());
    }

    @Override
    protected void onPostExecute(DownloadResult result) {
        final ContentValues cv = new ContentValues();
        if(result.errorMessage != null) {
            cv.put(Audio.STATUS, com.ifgroup.vkml.db.table.Status.FAILED.name());
        } else {
            cv.put(Audio.LOC_URI, result.uri.getPath());
            cv.put(Audio.STATUS, com.ifgroup.vkml.db.table.Status.LOCAL.name());
        }
        mContext.getContentResolver().update(AudioContentProvider.AUDIO_URI, cv,
                Audio._ID + " = ?", new String[]{String.valueOf(mId)});
        mContext.getContentResolver().notifyChange(AudioContentProvider.AUDIO_URI, null);
        quit();
    }

    @Override
    protected void onCancelled(DownloadResult result) {
        delete();
        quit();
    }

    @Override
    protected void onCancelled() {
        delete();
        quit();
    }

    private void delete() {
        final Intent intent = new Intent(mContext, FileRemoveService.class);
        intent.putExtra(C.Extra.FILE_PATH, mDstFile.getPath());
        mContext.startService(intent);
    }

    private void quit() {
        mNotificationManager.cancel(mThreadId);
        final DownloadExecutor executor;
        if ((executor = mOwnerExecutor.get()) != null) {
            executor.remove(this);
        }
    }

    private String prepareFileName(String fileName) {
        return fileName.replaceAll("[|/?*<\":>+\\[\\]/]", "");
    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + (mArtist != null ? mArtist.hashCode() : 0);
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + mThreadId;
        return result;
    }
}

class DownloadResult {
    public Uri uri;
    public String errorMessage;
}
