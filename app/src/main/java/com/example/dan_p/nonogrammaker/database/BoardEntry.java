package com.example.dan_p.nonogrammaker.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class BoardEntry implements Parcelable{
    private String cells;
    private String tag;
    private String creatorId;
    private String creatorEmail;

    public BoardEntry() {

    }

    public BoardEntry(String cells, String tag, String creatorId, String creatorEmail) {
        this.cells = cells;
        this.tag = tag;
        this.creatorId = creatorId;
        this.creatorEmail = creatorEmail;
    }

    protected BoardEntry(Parcel in) {
        cells = in.readString();
        tag = in.readString();
        creatorId = in.readString();
        creatorEmail = in.readString();
    }

    public static final Creator<BoardEntry> CREATOR = new Creator<BoardEntry>() {
        @Override
        public BoardEntry createFromParcel(Parcel in) {
            return new BoardEntry(in);
        }

        @Override
        public BoardEntry[] newArray(int size) {
            return new BoardEntry[size];
        }
    };

    public String getCells() {
        return cells;
    }

    public void setCells(String cells) {
        this.cells = cells;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cells", cells);
        result.put("tag", tag);
        result.put("creatorId", creatorId);
        result.put("creatorEmail", creatorEmail);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cells);
        dest.writeString(tag);
        dest.writeString(creatorId);
        dest.writeString(creatorEmail);
    }
}
