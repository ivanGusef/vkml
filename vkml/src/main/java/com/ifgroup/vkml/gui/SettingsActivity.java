package com.ifgroup.vkml.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.preferences.PreferencesManager;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/27/13
 * Time: 7:35 PM
 * May the force be with you always.
 */
public class SettingsActivity extends ActionBarActivity {

    private static final int GET_FOLDER = 1;

    private CheckBox mAutoDownloadCheck;

    private LinearLayout mWifiOnlyContainer;
    private CheckBox mWifiOnlyCheck;

    private TextView mFolderText;

    private PreferencesManager mPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_settings);
        enableHomeButton();

        mPreferencesManager = PreferencesManager.getInstance(this);
        mAutoDownloadCheck = (CheckBox) findViewById(R.id.autodownload_check);
        mWifiOnlyContainer = (LinearLayout) findViewById(R.id.wifi_only_container);
        mWifiOnlyCheck = (CheckBox) findViewById(R.id.only_wifi_check);
        mFolderText = (TextView) findViewById(R.id.folder_text);

        final boolean autoDownload = mPreferencesManager.get(C.Pref.AUTODOWNLOAD, true);
        mAutoDownloadCheck.setChecked(autoDownload);

        mWifiOnlyContainer.setVisibility(autoDownload ? View.VISIBLE : View.GONE);
        mWifiOnlyCheck.setChecked(mPreferencesManager.get(C.Pref.WIFI_ONLY, false));

        mFolderText.setText(mPreferencesManager.get(C.Pref.DEST_FOLDER,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()));
    }

    private void enableHomeButton() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeAutoDownloadMode(View v) {
        boolean checked = !mAutoDownloadCheck.isChecked();
        mAutoDownloadCheck.setChecked(checked);
        mPreferencesManager.save(C.Pref.AUTODOWNLOAD, checked);
        mWifiOnlyContainer.setVisibility(checked ? View.VISIBLE : View.GONE);
        if (!checked) {
            mWifiOnlyCheck.setChecked(false);
            mPreferencesManager.save(C.Pref.WIFI_ONLY, false);
        }
    }

    public void changeWifiOnlyMode(View v) {
        boolean checked = !mWifiOnlyCheck.isChecked();
        mWifiOnlyCheck.setChecked(checked);
        mPreferencesManager.save(C.Pref.WIFI_ONLY, checked);
    }

    public void changeFolder(View v) {
        startActivityForResult(new Intent(this, FolderSelectorActivity.class), GET_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_FOLDER && resultCode == RESULT_OK) {
            String folderPath = data.getStringExtra(C.Extra.DEST_FOLDER);
            mPreferencesManager.save(C.Pref.DEST_FOLDER, folderPath);
            mFolderText.setText(folderPath);
        }
    }
}
