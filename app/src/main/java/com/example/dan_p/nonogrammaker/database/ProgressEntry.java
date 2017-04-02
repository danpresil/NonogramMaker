package com.example.dan_p.nonogrammaker.database;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.dan_p.nonogrammaker.utils.Utils;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ProgressEntry implements Parcelable{
    private String progress0;
    private String progress1;
    private String progress2;
    private String progress3;
    private int time;
    private String solved;

    public ProgressEntry() {

    }

    public ProgressEntry(String progress0, String progress1, String progress2, String progress3,
                         int time, String solved) {
        setProgress0(progress0);
        setProgress1(progress1);
        setProgress2(progress2);
        setProgress3(progress3);
        setTime(time);
        setSolved(solved);
    }

    protected ProgressEntry(Parcel in) {
        progress0 = in.readString();
        progress1 = in.readString();
        progress2 = in.readString();
        progress3 = in.readString();
        time = in.readInt();
        solved = in.readString();
    }

    public static final Creator<ProgressEntry> CREATOR = new Creator<ProgressEntry>() {
        @Override
        public ProgressEntry createFromParcel(Parcel in) {
            return new ProgressEntry(in);
        }

        @Override
        public ProgressEntry[] newArray(int size) {
            return new ProgressEntry[size];
        }
    };

    public String getProgress0() {
        return progress0;
    }

    public String getProgress1() {
        return progress1;
    }

    public String getProgress2() {
        return progress2;
    }

    public String getProgress3() {
        return progress3;
    }

    public void setProgress0(String progress0) {
        if (progress0 != null)
            this.progress0 = progress0;
        else
            this.progress0 = Utils.emptyBoardCells();
    }

    public void setProgress1(String progress1) {
        if (progress0 != null)
            this.progress1 = progress1;
        else
            this.progress1 = Utils.emptyBoardCells();
    }

    public void setProgress2(String progress2) {
        if (progress2 != null)
            this.progress2 = progress2;
        else
            this.progress0 = Utils.emptyBoardCells();
    }

    public void setProgress3(String progress3) {
        if (progress3 != null)
            this.progress3 = progress3;
        else
            this.progress0 = Utils.emptyBoardCells();
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSolved() {
        return this.solved;
    }

    public void setSolved(String solved) {
        if (solved != null)
            this.solved = solved;
        else
            this.solved = "0000";
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("progress0", progress0);
        result.put("progress1", progress1);
        result.put("progress2", progress2);
        result.put("progress3", progress3);
        result.put("time", time);
        result.put("solved", solved);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(progress0);
        dest.writeString(progress1);
        dest.writeString(progress2);
        dest.writeString(progress3);
        dest.writeInt(time);
        dest.writeString(solved);
    }
}
