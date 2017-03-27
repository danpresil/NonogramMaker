package com.example.dan_p.nonogrammaker.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PuzzleEntry implements Parcelable{
    private String cells0;
    private String cells1;
    private String cells2;
    private String cells3;
    private String tag;
    private String creatorId;
    private String creatorEmail;

    public PuzzleEntry() {

    }

    public PuzzleEntry(String cells0, String cells1, String cells2, String cells3,
                      String tag, String creatorId, String creatorEmail) {
        this.cells0 = cells0;
        this.cells1 = cells1;
        this.cells2 = cells2;
        this.cells3 = cells3;
        this.tag = tag;
        this.creatorId = creatorId;
        this.creatorEmail = creatorEmail;
    }

    protected PuzzleEntry(Parcel in) {
        cells0 = in.readString();
        cells1 = in.readString();
        cells2 = in.readString();
        cells3 = in.readString();
        tag = in.readString();
        creatorId = in.readString();
        creatorEmail = in.readString();
    }

    public static final Creator<PuzzleEntry> CREATOR = new Creator<PuzzleEntry>() {
        @Override
        public PuzzleEntry createFromParcel(Parcel in) {
            return new PuzzleEntry(in);
        }

        @Override
        public PuzzleEntry[] newArray(int size) {
            return new PuzzleEntry[size];
        }
    };

    public String getCells0() {
        return cells0;
    }

    public String getCells1() {
        return cells1;
    }

    public String getCells2() {
        return cells2;
    }

    public String getCells3() {
        return cells3;
    }

    public void setCells0(String cells0) {
        this.cells0 = cells0;
    }

    public void setCells1(String cells1) {
        this.cells1 = cells1;
    }

    public void setCells2(String cells2) {
        this.cells2 = cells2;
    }

    public void setCells3(String cells3) {
        this.cells3 = cells3;
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
        result.put("cells0", cells0);
        result.put("cells1", cells1);
        result.put("cells2", cells2);
        result.put("cells3", cells3);
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
        dest.writeString(cells0);
        dest.writeString(cells1);
        dest.writeString(cells2);
        dest.writeString(cells3);
        dest.writeString(tag);
        dest.writeString(creatorId);
        dest.writeString(creatorEmail);
    }
}
