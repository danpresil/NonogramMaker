package com.example.dan_p.nonogrammaker.nonogram;

public class Cell {
    private int row;
    private int column;
    private CellState cellState;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.cellState = CellState.WHITE;
    }

    public CellState getCellState() {
        return cellState;
    }

    public void setCellState(CellState cellState) {
        this.cellState = cellState;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}

