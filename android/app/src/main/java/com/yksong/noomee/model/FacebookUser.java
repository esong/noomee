package com.yksong.noomee.model;

/**
 * Created by esong on 2015-02-27.
 */
public class FacebookUser implements Comparable<FacebookUser> {
    public String id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(FacebookUser another) {
        return id.compareTo(another.id);
    }
}
