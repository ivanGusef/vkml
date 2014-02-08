package com.ifgroup.vkml.db.table;

import com.ifgroup.vkml.R;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/24/13
 * Time: 3:38 AM
 * May the force be with you always.
 */
public enum Status {
    REMOTE(R.drawable.stub_not_downloaded),
    LOADING(R.drawable.stub_downloading),
    FAILED(R.drawable.stub_download_failed),
    LOCAL(R.drawable.stub_downloaded);

    public int resourceId;

    private Status(int resourceId) {
        this.resourceId = resourceId;
    }
}
