package com.ifgroup.vkml;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.ifgroup.vkml.client.VkAudioManager;
import com.ifgroup.vkml.client.service.DownloaderService;
import com.ifgroup.vkml.db.AudioContentProvider;
import com.ifgroup.vkml.preferences.PreferencesManager;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.EmailIntentSender;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/26/13
 * Time: 12:49 AM
 * May the force be with you always.
 */
@ReportsCrashes(formKey = "", mailTo = "ivan.gusef@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.e_acra)
public class VkLoaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        EmailIntentSender eis = new EmailIntentSender(getApplicationContext());
        ACRA.getErrorReporter().addReportSender(eis);
    }

    public static void logout(Context context) {
        VkAudioManager.lock();
        PreferencesManager.getInstance(context).clear();
        context.getContentResolver().delete(AudioContentProvider.AUDIO_URI, null, null);

        /**
         * Shutdown AudioLoader service
         */
        Intent broadCast = new Intent(C.Action.TOKEN_STATE_CHANGED);
        context.sendBroadcast(broadCast);
        /**
         * Shutdown Downloader service
         */
        Intent shutDown = new Intent(context, DownloaderService.class);
        shutDown.setAction(C.Action.SHUTDOWN_DOWNLOAD);
        context.startService(shutDown);
    }

    public static void login(Context context, String accessToken) {
        VkAudioManager.unlock();
        PreferencesManager preferencesManager = PreferencesManager.getInstance(context);
        preferencesManager.save(C.Pref.ACCESS_TOKEN, accessToken);

        /**
         * Start AudioLoader service
         */
        Intent broadCast = new Intent(C.Action.TOKEN_STATE_CHANGED);
        broadCast.putExtra(C.Extra.ACCESS_TOKEN, accessToken);
        context.sendBroadcast(broadCast);
    }
}
