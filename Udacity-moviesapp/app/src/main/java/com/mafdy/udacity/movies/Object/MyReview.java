package com.mafdy.udacity.movies.Object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SBP on 03/05/2018.
 */

public class MyReview implements Parcelable {

    private String author;
    private String content;
    private String url;


    protected MyReview(Parcel in) {
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<MyReview> CREATOR = new Creator<MyReview>() {
        @Override
        public MyReview createFromParcel(Parcel in) {
            return new MyReview(in);
        }

        @Override
        public MyReview[] newArray(int size) {
            return new MyReview[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }
}
