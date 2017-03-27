package com.example.dan_p.nonogrammaker.nonogram;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CellImage extends ImageView {

    private int row;
    private int column;

    public CellImage(Context context) {
        super(context);
    }

    public CellImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPosition(int row, int column){
        if (row >= 0 && column >= 0) {
            setRow(row);
            setColumn(column);
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
