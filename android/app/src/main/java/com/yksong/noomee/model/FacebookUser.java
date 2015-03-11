package com.yksong.noomee.model;

/**
 * Created by esong on 2015-02-27.
 */
public class FacebookUser implements Comparable<FacebookUser> {
    public String mId;
    public String mName;

    public FacebookUser(String id, String name) {
        mId = id;
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public int compareTo(FacebookUser another) {
        return mId.compareTo(another.mId);
    }
}
