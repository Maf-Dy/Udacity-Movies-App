package com.mafdy.udacity.movies.Object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SBP on 03/05/2018.
 */

public class MyVideo implements Parcelable {

    private String id;
    private String key;
    private String site;
    private String type;


    public static final Creator<MyVideo> CREATOR = new Creator<MyVideo>() {
        @Override
        public MyVideo createFromParcel(Parcel in) {
            return new MyVideo(in);
        }

        @Override
        public MyVideo[] newArray(int size) {
            return new MyVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected MyVideo(Parcel in) {
        id = in.readString();
        key = in.readString();
        site = in.readString();
        type = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(site);
        dest.writeString(type);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

