package com.yksong.noomee.util;

/**
 * Created by Ed on 11/03/2015.
 */
public class YelpUtil {
    public static String switchToLsImageUrl(String url) {
        return url.substring(0, url.lastIndexOf('/')+1).concat("ls.jpg");
    }

    public static String switchTo348ImageUrl(String url) {
        return url.substring(0, url.lastIndexOf('/')+1).concat("348s.jpg");
    }
}
