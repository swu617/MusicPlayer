/*
 * Created by i301487 on 2014/10/31
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player.db.models;

import android.os.Bundle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public abstract class ODataItem<T> {
    public List<T> itemList = new ArrayList<>();
    
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Column {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsonProperty {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SubModule {
        String[] fields();

        String[] columns();
    }
}
