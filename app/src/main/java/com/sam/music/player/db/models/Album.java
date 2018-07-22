package com.sam.music.player.db.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.sam.music.player.RHSApp;

/**
 * Created by i301487 on 4/20/16.
 */
public class Album extends ODataItem<Album> implements Parcelable {
    public static final String ID = "id";

    public static final String URI = "uri";
    public static final String RES_ID = "resource_id";
    public static final String TITLE = "title";
    public static final String TITLE_ID = "title_id";
    public static final String INDEX = "displayIndex";

    public Album() {
    }

    public Album(int titleId, int resId, String id) {
        this.resId = resId;
        this.titleId = titleId;
        isInner = true;
        this.id = id;
    }

    @Column(ID)
    public String id;

    @Column(TITLE)
    public String title;

    public String getTitle() {
        if (isInner) {
            return RHSApp.getAppContext().getString(titleId);
        }
        return title;
    }

    @Column(URI)
    public String uri;

    public Uri getUri() {
        return uri != null && uri.length() > 0 ? Uri.parse(uri) : null;
    }


    @Column(TITLE_ID)
    public int titleId;

    @Column(RES_ID)
    public int resId;

    //@Column(INDEX)
    //public int displayIndex;

    public boolean isInner;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.uri);
        dest.writeInt(this.titleId);
        dest.writeInt(this.resId);
        //dest.writeInt(this.displayIndex);
        dest.writeByte(isInner ? (byte) 1 : (byte) 0);
    }

    protected Album(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.uri = in.readString();
        this.titleId = in.readInt();
        this.resId = in.readInt();
        //this.displayIndex = in.readInt();
        this.isInner = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
