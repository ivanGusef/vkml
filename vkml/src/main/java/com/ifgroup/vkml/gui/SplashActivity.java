package com.ifgroup.vkml.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.VkLoaderApplication;
import com.ifgroup.vkml.preferences.PreferencesManager;

public class SplashActivity extends Activity {

    private PreferencesManager mPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_splash);
        mPreferencesManager = PreferencesManager.getInstance(this);
        final View bgView = findViewById(R.id.bg);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                bgView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgView.clearAnimation();
                final String token = mPreferencesManager.get(C.Pref.ACCESS_TOKEN, null);
                if (token != null) {
                    VkLoaderApplication.login(SplashActivity.this, token);
                    startActivity(new Intent(SplashActivity.this, AudioListActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        bgView.startAnimation(animation);
    }
}
