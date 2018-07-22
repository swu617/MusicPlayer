package com.sam.music.player.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by i301487 on 4/10/16.
 */
public class RegxUrils {

    private static final Pattern pattern = Pattern.compile("[^/]+\\.");


    public static String getFileName(String src) {

        Matcher matcher = pattern.matcher(src);
        if (matcher.find()) {
            String matched = matcher.group().replace(".", "");
            return matched;
        }

        return null;
    }
}
