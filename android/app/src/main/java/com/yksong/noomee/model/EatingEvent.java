package com.yksong.noomee.model;

import java.util.Date;
import java.util.List;

/**
 * Created by esong on 2015-02-27.
 */
public class EatingEvent implements Comparable<EatingEvent> {
    public String location;
    public Date time;
    public Date createdTime;
    public List<FacebookUser> users;
    public String eventId;

    @Override
    public int compareTo(EatingEvent another) {
        return another.createdTime.compareTo(createdTime);
    }
}
