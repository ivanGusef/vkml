package com.ifgroup.vkml.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.client.DownloadExecutor;
import com.ifgroup.vkml.client.DownloadTask;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/28/13
 * Time: 11:34 PM
 * May the force be with you always.
 */
public class DownloaderService extends Service implements DownloadExecutor {

    private int mCounter = 3;
    private Set<DownloadTask> mTasks = new HashSet<DownloadTask>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        if (action != null && action.equals(C.Action.SHUTDOWN_DOWNLOAD)) {
            shutdown();
            stopSelf();
        } else {
            final String url = intent.getStringExtra(C.Extra.URL);
            final long id = intent.getLongExtra(C.Extra.ID, 0L);
            final String artist = intent.getStringExtra(C.Extra.ARTIST);
            final String title = intent.getStringExtra(C.Extra.TITLE);
            download(url, id, artist, title);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
    }

    public void download(String url, long id, String artist, String title) {
        final DownloadTask task = new DownloadTask(this, this, id, artist, title);
        if (mTasks.add(task)) task.execute(url);
    }

    public void remove(DownloadTask task) {
        mTasks.remove(task);
        if (mTasks.size() == 0) stopSelf();
    }

    public int getNextId() {
        return mCounter++;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void shutdown() {
        for (DownloadTask mTask : mTasks) {
            mTask.cancel(true);
        }
    }
}
