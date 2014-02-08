package com.ifgroup.vkml;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 9:41 PM
 * May the force be with you always.
 */
public interface C {

    interface App {
        String CHARSET = "UTF-8";
    }

    interface Pref {
        String ACCESS_TOKEN = "access_token";
        String AUTODOWNLOAD = "autodownload";
        String WIFI_ONLY = "wifi_only";
        String DEST_FOLDER = "dest_folder";
        String FIRST_LOADING = "first_loading";
    }

    interface DBase {

    }

    interface API {
        String LOGIN_URL = "https://oauth.vk.com/authorize?client_id=3872936&scope=8&redirect_uri=https://oauth.vk.com/blank.html&display=mobile&response_type=token";

        String AUDIO_GET = "audio.get";
    }

    interface Extra {
        String DEST_FOLDER = "extra_dest_folder";
        String ID = "extra_id";
        String URL = "extra_url";
        String ARTIST = "extra_artist";
        String TITLE = "extra_title";
        String ACCESS_TOKEN = "extra_access_token";
        String FILE_PATH = "extra_file_name";
    }

    interface Message {
        int REFRESH = 1;
    }

    interface Action {
        String TOKEN_STATE_CHANGED = "com.ifgroup.vkloader.TOKEN_STATE_CHANGED";
        String SHUTDOWN_DOWNLOAD = "com.ifgroup.vkloader.SHUTDOWN_DOWNLOAD";
    }
}
