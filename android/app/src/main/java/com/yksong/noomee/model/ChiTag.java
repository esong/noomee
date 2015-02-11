package com.yksong.noomee.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by esong on 2015-01-12.
 */
public class ChiTag implements Parcelable {
    String name;
    String query;
    int count;

    public ChiTag(String name, String query, int count){
        this.name = name;
        this.query = query;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(query);
        dest.writeInt(count);
    }

    private ChiTag(Parcel in) {
        name = in.readString();
        query = in.readString();
        count = in.readInt();
    }

    public static final Parcelable.Creator<ChiTag> CREATOR = new Parcelable.Creator<ChiTag>() {
        public ChiTag createFromParcel(Parcel in) {
            return new ChiTag(in);
        }
        public ChiTag[] newArray(int size) {
            return new ChiTag[size];
        }
    };

}
