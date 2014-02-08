package com.ifgroup.vkml.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.client.service.AudioLoaderService;
import com.ifgroup.vkml.preferences.PreferencesManager;
import com.ifgroup.vkml.utils.ConnectivityUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/28/13
 * Time: 5:53 PM
 * May the force be with you always.
 */
public class ServiceControllerReceiver extends BroadcastReceiver {

    private PreferencesManager mPreferencesManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        mPreferencesManager = PreferencesManager.getInstance(context);
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            onNetworkStateChanged(context, ConnectivityUtils.isConnected(context));
        } else if (action.equals(C.Action.TOKEN_STATE_CHANGED)) {
            onTokenStateChanged(context, intent.getStringExtra(C.Extra.ACCESS_TOKEN));
        }
    }

    private void onTokenStateChanged(Context context, String token) {
        mPreferencesManager.save(C.Pref.ACCESS_TOKEN, token);
        if (ConnectivityUtils.isConnected(context)) {
            doService(context, token != null);
        }
    }

    private void onNetworkStateChanged(Context context, boolean connected) {
        if (mPreferencesManager.get(C.Pref.ACCESS_TOKEN, null) != null) {
            doService(context, connected);
        }
    }

    private void doService(Context context, boolean run) {
        if (run) {
            context.startService(new Intent(context, AudioLoaderService.class));
        } else {
            context.stopService(new Intent(context, AudioLoaderService.class));
        }
    }
}
