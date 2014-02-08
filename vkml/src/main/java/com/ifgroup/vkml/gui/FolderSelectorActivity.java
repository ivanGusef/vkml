package com.ifgroup.vkml.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;

import java.io.File;
import java.io.FileFilter;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/27/13
 * Time: 9:36 PM
 * May the force be with you always.
 */
public class FolderSelectorActivity extends ActionBarActivity implements FolderCreatorDialog.OnCreateListener,
        AdapterView.OnItemClickListener {

    private static final String SAVE_KEY_ROOT = "root";

    private static final File ROOT = Environment.getExternalStorageDirectory();

    private File mCurrentRoot;

    private FolderAdapter mAdapter = new FolderAdapter();
    private final FileFilter mFileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_KEY_ROOT, mCurrentRoot.getPath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_list);

        final ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(findViewById(android.R.id.empty));

        enableHomeButton();

        if (savedInstanceState != null) {
            openFolder(new File(savedInstanceState.getString(SAVE_KEY_ROOT)));
        } else {
            openFolder(ROOT);
        }
    }

    private void enableHomeButton() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuRes;
        if (ROOT.equals(mCurrentRoot)) {
            menuRes = R.menu.m_folder_selector_root;
        } else {
            menuRes = R.menu.m_folder_selector;
        }
        getMenuInflater().inflate(menuRes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, AudioListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.mi_up:
                openFolder(mCurrentRoot.getParentFile());
                return true;
            case R.id.mi_create_folder:
                new FolderCreatorDialog(this, mCurrentRoot, this).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFolder(File folder) {
        mAdapter.setFiles(getFolderInDirectory(folder));
        supportInvalidateOptionsMenu();
    }

    private File[] getFolderInDirectory(File file) {
        mCurrentRoot = file;
        return file.listFiles(mFileFilter);
    }

    @Override
    public void onCreate(File file) {
        Toast.makeText(this, getString(R.string.folder_successfuly_created, file.getName()), Toast.LENGTH_LONG).show();
        openFolder(mCurrentRoot);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openFolder(mAdapter.getItem(position));
    }

    private class FolderAdapter extends BaseAdapter implements View.OnClickListener {

        private File[] mFiles;

        public FolderAdapter() {
        }

        public void setFiles(File[] files) {
            mFiles = files;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mFiles == null) return 0;
            return mFiles.length;
        }

        @Override
        public File getItem(int position) {
            if (mFiles == null) return null;
            return mFiles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.i_folder, null);
                holder = new ViewHolder();
                holder.fileName = (TextView) view.findViewById(R.id.folder_name);
                holder.takeBtn = view.findViewById(R.id.take_btn);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            File file = getItem(position);
            holder.fileName.setText(file.getName());
            holder.takeBtn.setTag(position);
            holder.takeBtn.setOnClickListener(this);
            view.setTag(holder);
            return view;
        }

        @Override
        public void onClick(View v) {
            File file = getItem((Integer) v.getTag());
            Intent data = new Intent();
            data.putExtra(C.Extra.DEST_FOLDER, file.getPath());
            setResult(RESULT_OK, data);
            finish();
        }

        private class ViewHolder {
            TextView fileName;
            View takeBtn;
        }
    }
}
