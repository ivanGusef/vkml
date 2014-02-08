package com.ifgroup.vkml.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.ifgroup.vkml.C;
import com.ifgroup.vkml.R;
import com.ifgroup.vkml.client.service.DownloaderService;
import com.ifgroup.vkml.db.table.Audio;
import com.ifgroup.vkml.db.table.Genre;
import com.ifgroup.vkml.db.table.Status;
import com.ifgroup.vkml.utils.ConnectivityUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/24/13
 * Time: 2:40 AM
 * May the force be with you always.
 */
public class AudioCursorAdapter extends CursorAdapter {

    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final OnDownloadClickListener mListener = new OnDownloadClickListener();

    public AudioCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mLayoutInflater.inflate(R.layout.i_audio, null);
        final ViewHolder holder = new ViewHolder();
        holder.title = (TextView) view.findViewById(R.id.title);
        holder.genre = (TextView) view.findViewById(R.id.genre);
        holder.duration = (TextView) view.findViewById(R.id.duration);
        holder.downloadStatus = (ImageButton) view.findViewById(R.id.download_status_btn);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.title.setText(buildTitle(context, cursor));
        holder.genre.setText(buildGenre(context, cursor));
        holder.duration.setText(buildDuration(cursor));

        final Status status = buildStatus(cursor);
        holder.downloadStatus.setImageResource(status.resourceId);
        holder.downloadStatus.setTag(cursor.getPosition());
        if (status.equals(Status.REMOTE) || status.equals(Status.FAILED)) {
            holder.downloadStatus.setOnClickListener(mListener);
        } else {
            holder.downloadStatus.setOnClickListener(null);
        }
    }

    private CharSequence buildGenre(Context context, Cursor cursor) {
        return context.getString(Genre.valueOf(cursor.getString(cursor.getColumnIndex(Audio.GENRE))).resourceId);
    }

    private Status buildStatus(Cursor cursor) {
        return Status.valueOf(cursor.getString(cursor.getColumnIndex(Audio.STATUS)));
    }

    private CharSequence buildDuration(Cursor cursor) {
        return TIME_FORMAT.format(new Date(cursor.getLong(cursor.getColumnIndex(Audio.DURATION))));
    }

    private CharSequence buildTitle(Context context, Cursor cursor) {
        final String artist = cursor.getString(cursor.getColumnIndex(Audio.ARTIST));
        final String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
        return context.getString(R.string.audio_title_template, artist, title);
    }


    private class ViewHolder {
        TextView title, genre, duration;
        ImageButton downloadStatus;
    }

    private class OnDownloadClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (ConnectivityUtils.isConnected(mContext)) {
                final int position = (Integer) v.getTag();
                final Cursor cursor = (Cursor) getItem(position);
                if (cursor != null) {
                    final Intent intent = new Intent(mContext, DownloaderService.class);
                    intent.putExtra(C.Extra.URL, cursor.getString(cursor.getColumnIndex(Audio.EXT_URL)));
                    intent.putExtra(C.Extra.ID, cursor.getLong(cursor.getColumnIndex(Audio._ID)));
                    intent.putExtra(C.Extra.ARTIST, cursor.getString(cursor.getColumnIndex(Audio.ARTIST)));
                    intent.putExtra(C.Extra.TITLE, cursor.getString(cursor.getColumnIndex(Audio.TITLE)));
                    mContext.startService(intent);
                }
            } else {
                Toast.makeText(mContext, R.string.e_no_network, Toast.LENGTH_LONG).show();
            }
        }
    }
}
