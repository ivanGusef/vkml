package com.ifgroup.vkml.db.table;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/24/13
 * Time: 3:13 AM
 * May the force be with you always.
 */
public interface Audio extends BaseColumns {
    String AID = "aid";
    String TITLE = "title";
    String ARTIST = "artist";
    String GENRE = "genre";
    String EXT_URL = "ext_url";
    String LOC_URI = "loc_uri";
    String UPTIME = "uptime";
    String STATUS = "status";
    String DURATION = "duration";
}
