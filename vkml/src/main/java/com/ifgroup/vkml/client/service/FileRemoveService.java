package com.ifgroup.vkml.client.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.ifgroup.vkml.C;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by ivan on 29.01.14.
 */
public class FileRemoveService extends IntentService {

    public FileRemoveService() {
        super("FileRemoveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String fileName = intent.getStringExtra(C.Extra.FILE_PATH);
        final File file = new File(fileName);
        try {
            if (!file.delete()){
                throw new FileNotFoundException("File " + file.getPath() + " can't be deleted");
            }
        } catch (FileNotFoundException e) {
            Log.e("FileRemoveService -> onHandleIntent", e.getMessage());
        }
    }
}
