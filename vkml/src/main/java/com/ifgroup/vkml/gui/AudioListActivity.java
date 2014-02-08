package com.ifgroup.vkml.gui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.VkLoaderApplication;
import com.ifgroup.vkml.db.AudioContentProvider;
import com.ifgroup.vkml.db.table.Audio;
import com.ifgroup.vkml.gui.adapter.AudioCursorAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 10:47 PM
 * May the force be with you always.
 */
public class AudioListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mListView;
    private AudioCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_list);
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_audio_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_logout:
                VkLoaderApplication.logout(this);
                final Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.mi_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, AudioContentProvider.AUDIO_URI,
                new String[]{Audio._ID, Audio.TITLE, Audio.ARTIST, Audio.GENRE, Audio.STATUS, Audio.DURATION, Audio.EXT_URL},
                null, null, Audio.AID + " desc");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter == null) {
            mAdapter = new AudioCursorAdapter(this, null);
            mListView.setAdapter(mAdapter);
        }
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
