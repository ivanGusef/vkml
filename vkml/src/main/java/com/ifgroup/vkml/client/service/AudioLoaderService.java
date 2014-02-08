package com.ifgroup.vkml.client.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.client.VkAudioManager;
import com.ifgroup.vkml.gui.AudioListActivity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/25/13
 * Time: 9:28 PM
 * May the force be with you always.
 */
public class AudioLoaderService extends Service {

    private static final Class<?>[] mSetForegroundSignature = new Class[]{boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{boolean.class};

    private static final int NOTIFICATION_ID = 1;

    private VkAudioManager mVkAudioManager;
    private HandlerThread mThread = new LoaderThread();

    private NotificationManager mNotificationManager;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    @Override
    public void onCreate() {
        super.onCreate();
        Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        mVkAudioManager = VkAudioManager.getInstance(this);
        mThread.start();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }
        try {
            mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setOngoing(true);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.online));
        builder.setWhen(System.currentTimeMillis());
        final Intent intent = new Intent(this, AudioListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(contentIntent);
        startForegroundCompat(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThread.quit();
        stopForegroundCompat(NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
            Log.w("AudioLoaderService", "Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            // Should not happen.
            Log.w("AudioLoaderService", "Unable to invoke method", e);
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = id;
            mStartForegroundArgs[1] = notification;
            invokeMethod(mStartForeground, mStartForegroundArgs);
            return;
        }

        mSetForegroundArgs[0] = Boolean.TRUE;
        invokeMethod(mSetForeground, mSetForegroundArgs);
        mNotificationManager.notify(id, notification);
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mStopForeground, mStopForegroundArgs);
            return;
        }

        mNotificationManager.cancel(id);
        mSetForegroundArgs[0] = Boolean.FALSE;
        invokeMethod(mSetForeground, mSetForegroundArgs);
    }

    private class LoaderThread extends HandlerThread implements Handler.Callback {

        public static final int INTERVAL = 30000; //3 minutes

        private Handler mHandler;
        private boolean mDead;

        public LoaderThread() {
            super("loader_thread", Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        protected void onLooperPrepared() {
            mHandler = new Handler(getLooper(), this);
            mHandler.sendEmptyMessage(C.Message.REFRESH);
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == C.Message.REFRESH) {
                try {
                    mVkAudioManager.reload();
                } catch (IOException e) {
                    Log.e("AudioLoaderService -> LoaderThread -> handleMessage", e.getMessage(), e);
                } finally {
                    if (!mDead) mHandler.sendEmptyMessageDelayed(C.Message.REFRESH, INTERVAL);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean quit() {
            mDead = super.quit();
            if (mDead) mHandler.removeMessages(C.Message.REFRESH);
            return mDead;
        }
    }
}
