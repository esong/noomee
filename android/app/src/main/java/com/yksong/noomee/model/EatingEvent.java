package com.yksong.noomee.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by esong on 2015-02-27.
 */
public class EatingEvent implements Comparable<EatingEvent> {
    public String creator;
    public String location;
    public Date time;
    public List<FacebookUser> users;

    @Override
    public int compareTo(EatingEvent another) {
        return another.time.compareTo(time);
    }
}
