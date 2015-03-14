package com.yksong.noomee.model;

/**
 * Created by esong on 2015-01-17.
 */
public class Restaurant {
    public String mobile_url;
    public String rating_img_url;
    public String image_url;
    public String name;
    public String id;
    public Location location;

    public String toString() {
        return name;
    }

    public class Location {
        public Coordinate coordinate;

        public Location() {}

        public class Coordinate {
            public double latitude;
            public double longitude;

            public Coordinate () {}
        }
    }
}
