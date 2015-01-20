package com.yksong.noomee.model;

/**
 * Created by esong on 2015-01-12.
 */
public class ChiTag {
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
}
