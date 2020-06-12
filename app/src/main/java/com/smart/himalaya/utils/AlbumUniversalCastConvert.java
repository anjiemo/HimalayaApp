package com.smart.himalaya.utils;

import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.greenrobot.greendao.converter.PropertyConverter;

public class AlbumUniversalCastConvert implements PropertyConverter<Album, String> {


    @Override
    public Album convertToEntityProperty(String databaseValue) {
        if (ObjectTools.isNotEmpty(databaseValue)) {
            return new Gson().fromJson(databaseValue, Album.class);
        }
        return null;
    }

    @Override
    public String convertToDatabaseValue(Album entityProperty) {
        if (ObjectTools.isNotEmpty(entityProperty)) {
            return new Gson().toJson(entityProperty);
        }
        return null;
    }
}
