package com.sam.music.player.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i301487 on 1/2/16.
 */
public class Bean <T>{
    public List<T> list = new ArrayList<>();

    public Bean(T t){
        list.add(t);
    }

    public Bean(T t1, T t2){
        list.add(t1);
        list.add(t2);
    }

    public Bean(T t1, T t2, T t3){
        list.add(t1);
        list.add(t2);
        list.add(t3);
    }
}
