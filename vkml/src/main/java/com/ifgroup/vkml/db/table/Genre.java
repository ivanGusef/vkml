package com.ifgroup.vkml.db.table;

import com.ifgroup.vkml.R;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/25/13
 * Time: 8:20 PM
 * May the force be with you always.
 */
public enum Genre {
    ROCK(1, R.string.rock),
    POP(2, R.string.pop),
    RAP_AND_HIP_HOP(3, R.string.rap_and_hip_hop),
    EASY_LISTENING(4, R.string.easy_listening),
    DANCE_AND_HOUSE(5, R.string.dance_and_house),
    INSTRUMENTAL(6, R.string.instrumental),
    METAL(7, R.string.metal),
    ALTERNATIVE(21, R.string.alternative),
    DUBSTEP(8, R.string.dubstep),
    JAZZ_AND_BLUES(9, R.string.jazz_and_blues),
    DRUM_AND_BASS(10, R.string.drum_and_bass),
    TRANCE(11, R.string.trance),
    CHANSON(12, R.string.chanson),
    ETHNIC(13, R.string.ethnic),
    ACOUSTIC_AND_VOCAL(14, R.string.acoustic_and_vocal),
    REGGAE(15, R.string.reggae),
    CLASSICAL(16, R.string.classical),
    INDIE_POP(17, R.string.indie_pop),
    SPEECH(19, R.string.speech),
    ELECTROPOP_AND_DISCO(22, R.string.electropop_and_disco),
    OTHER(18, R.string.other),
    UNKNOWN(-1, R.string.unknown);

    public int id;
    public int resourceId;

    private Genre(int id, int resourceId) {
        this.id = id;
        this.resourceId = resourceId;
    }

    public static Genre getById(int id) {
        for (Genre genre : values()) {
            if (genre.id == id) return genre;
        }
        return UNKNOWN;
    }
}
