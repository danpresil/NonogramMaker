package com.example.dan_p.nonogrammaker.database;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.dan_p.nonogrammaker.nonogram.CellState;
import com.example.dan_p.nonogrammaker.nonogram.GameBoard;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class BoardEntry implements Parcelable{
    private String cells0;
    private String cells1;
    private String cells2;
    private String cells3;
    private String tag;
    private String creatorId;
    private String creatorEmail;

    public BoardEntry() {

    }

    public BoardEntry(String cells0, String cells1, String cells2, String cells3,
                       String tag, String creatorId, String creatorEmail) {
        this.cells0 = cells0;
        this.cells1 = cells1;
        this.cells2 = cells2;
        this.cells3 = cells3;
        this.tag = tag;
        this.creatorId = creatorId;
        this.creatorEmail = creatorEmail;
    }

    protected BoardEntry(Parcel in) {
        cells0 = in.readString();
        cells1 = in.readString();
        cells2 = in.readString();
        cells3 = in.readString();
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

    public Bitmap createImage0(int color0, int color1) {
        return createImage(0, color0, color1);
    }

    public Bitmap createImage1(int color0, int color1) {
        return createImage(1, color0, color1);
    }

    public Bitmap createImage2(int color0, int color1) {
        return createImage(2, color0, color1);
    }

    public Bitmap createImage3(int color0, int color1) {
        return createImage(3, color0, color1);
    }

    private Bitmap createImage(int position, int color0, int color1) {
        int size = 0;
        String cells = "";

        switch (position) {
            case 0:
                size = cells0.length();
                cells = cells0;
                break;
            case 1:
                size = cells1.length();
                cells = cells1;
                break;
            case 2:
                size = cells2.length();
                cells = cells2;
                break;
            case 3:
                size = cells3.length();
                cells = cells3;
                break;
            default:
                return null;
        }

        int[] pixels = new int[size];
        for (int i = 0 ; i < size ; i ++) {
            if (cells.charAt(i) == '0')
                pixels[i] = color0;//0xffa9a9a9;
            else
                pixels[i] = color1;//0xff00304e;
        }

        int squaredSize = (int)Math.sqrt(size);

        return Bitmap.createBitmap(pixels, squaredSize, squaredSize, Bitmap.Config.ARGB_8888);
    }
}
