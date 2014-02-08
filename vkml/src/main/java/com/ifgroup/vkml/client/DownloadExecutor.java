package com.ifgroup.vkml.client;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/28/13
 * Time: 1:33 AM
 * May the force be with you always.
 */
public interface DownloadExecutor {

    void remove(DownloadTask task);

    int getNextId();
}
