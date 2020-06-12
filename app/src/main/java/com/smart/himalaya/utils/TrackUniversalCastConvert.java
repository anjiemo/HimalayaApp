package com.smart.himalaya.utils;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.greenrobot.greendao.converter.PropertyConverter;

public class TrackUniversalCastConvert implements PropertyConverter<Track,String> {

    @Override
    public Track convertToEntityProperty(String databaseValue) {
        if (ObjectTools.isNotEmpty(databaseValue)) {
            return new Gson().fromJson(databaseValue, Track.class);
        }
        return null;
    }

    @Override
    public String convertToDatabaseValue(Track entityProperty) {
        if (ObjectTools.isNotEmpty(entityProperty)) {
            return new Gson().toJson(entityProperty);
        }
        return null;
    }
}
