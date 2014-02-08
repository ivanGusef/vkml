package com.ifgroup.vkml.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/11/13
 * Time: 10:21 PM
 * May the force be with you always.
 */
public class ResourceReader {
    public static final String SEPARATOR = "\n";

    public static String readResourceAsString(Context context, int resourceId) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            while ((str = br.readLine()) != null) {
                sb.append(str).append(SEPARATOR);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return sb.toString();
    }
}
