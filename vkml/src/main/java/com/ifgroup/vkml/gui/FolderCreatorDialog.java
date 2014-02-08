package com.ifgroup.vkml.gui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ifgroup.vkml.R;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/29/13
 * Time: 1:38 PM
 * May the force be with you always.
 */
public class FolderCreatorDialog extends Dialog implements View.OnClickListener {

    private final File mRoot;
    private EditText mFolderNameEditor;
    private OnCreateListener mListener;

    public FolderCreatorDialog(Context context, File root, OnCreateListener listener) {
        super(context);
        mRoot = root;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_folder_creator);
        setTitle(R.string.new_folder_creation);
        mFolderNameEditor = (EditText) findViewById(R.id.folder_name);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.create).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                String folderName = mFolderNameEditor.getText().toString();
                if (TextUtils.isEmpty(folderName)) {
                    folderName = getContext().getString(R.string.unnamed);
                }
                create(folderName);
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void create(String folderName) {
        if (!folderName.replaceAll("[|/?*<\":>+\\[\\]/]", "").equals(folderName)) {
            mFolderNameEditor.setError(getContext().getString(R.string.file_name_cant_contains, "|, \\, /, ?, *, \", :, +, <, >, [, ]"));
            return;
        }
        for (String fileName : mRoot.list()) {
            if (folderName.equals(fileName)) {
                mFolderNameEditor.setError(getContext().getString(R.string.file_name_already_exists));
                return;
            }
        }
        final File newFolder = new File(mRoot, folderName);
        if (newFolder.mkdir()) {
            mListener.onCreate(newFolder);
            dismiss();
        } else {
            Toast.makeText(getContext(), R.string.e_folder_creation_failed, Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    public static interface OnCreateListener {
        void onCreate(File file);
    }
}
