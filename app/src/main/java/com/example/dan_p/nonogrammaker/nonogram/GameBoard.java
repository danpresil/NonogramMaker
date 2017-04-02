package com.example.dan_p.nonogrammaker.nonogram;

import android.graphics.Bitmap;

import com.example.dan_p.nonogrammaker.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class GameBoard {
    private static final int COLOR_0 = 0xFF000000;
    private static final int COLOR_1 = 0xFFC5C5C5;

    private int size;
    private Cell[][] cells;
    private ArrayList<Integer>[] rowNumbersLists;
    private ArrayList<Integer>[] columnNumbersLists;
    private int numberOfMarkedCells;
    private Bitmap image;

    public GameBoard(String cells) throws BoardSizeException {
        this(Utils.parseStringToIntegerArray(cells));
    }

    public GameBoard(int[] cells) throws BoardSizeException {
        if (!checkBoardSize(cells) || cells.length == 0)
        throw new BoardSizeException("The board has to be square");

        this.size = (int)Math.sqrt(cells.length);
        initializeBoard(cells);
        updateSolutionNumbers();
        this.image = createImage();
    }

    /* An empty board */
    public GameBoard(int size) {
        this.size = size;
        this.numberOfMarkedCells = 0;
        initializeBoard();
        this.image = createImage();
    }

    private void initializeBoard() {
        initializeBoard(null);
    }

    private void initializeBoard(int[] cells) {
        this.cells = new Cell[size][size];
        for (int row = 0 ; row < this.size ; row++) {
            for (int column =  0 ; column < this.size ; column++) {
                this.cells[row][column] = new Cell(row, column);

                if (cells != null) {
                    switch (cells[row*size + column]) {
                        case 0:
                            this.cells[row][column].setCellState(CellState.WHITE);
                            break;
                        case 1:
                            this.cells[row][column].setCellState(CellState.BLACK);
                            break;
                        case 2:
                            this.cells[row][column].setCellState(CellState.X);
                            break;
                        default:
                            this.cells[row][column].setCellState(CellState.WHITE);
                            break;
                    }
                } else
                    this.cells[row][column].setCellState(CellState.WHITE);
            }
        }

        this.rowNumbersLists = new ArrayList[size];
        this.columnNumbersLists = new ArrayList[size];
        for (int i = 0 ; i < this.size ; i++) {
            this.rowNumbersLists[i] = new ArrayList<>();
            this.columnNumbersLists[i] = new ArrayList<>();
        }

        updateSolutionNumbers();
    }

    public ArrayList<Integer>[] getRowNumbersLists() {
        updateSolutionNumbers();
        return this.rowNumbersLists;
    }

    public ArrayList<Integer>[] getColumnNumbersLists() {
        updateSolutionNumbers();
        return this.columnNumbersLists;
    }

    private void updateSolutionNumbers() {
        for (int i = 0 ; i < this.size ; i ++) {
            this.rowNumbersLists[i] = countNumbersInRow(i);
            this.columnNumbersLists[i] = countNumbersInColumn(i);
        }
    }

    private ArrayList<Integer> countNumbersInRow(int row) {
        ArrayList<Integer> numbersList = new ArrayList<>();
        int count = 0;

        for (int column = 0 ; column < this.size ; column++) {
            if (this.cells[row][column].getCellState() == CellState.BLACK)
                count++;
            else if (count != 0){
                numbersList.add(count);
                count = 0;
            }
        }
        if (count != 0)
            numbersList.add(count);

        /*No numbers in this row*/
        if (numbersList.size() == 0)
            numbersList.add(0);

        return numbersList;
    }

    private ArrayList<Integer> countNumbersInColumn(int column) {
        ArrayList<Integer> numbersList = new ArrayList<>();
        int count = 0;

        for (int row = 0 ; row < this.size ; row++) {
            if (this.cells[row][column].getCellState() == CellState.BLACK)
                count++;
            else if (count != 0){
                numbersList.add(count);
                count = 0;
            }
        }
        if (count != 0)
            numbersList.add(count);

        /*No numbers in this column*/
        if (numbersList.size() == 0)
            numbersList.add(0);

        return numbersList;
    }

    public int getSize() {
        return this.size;
    }

    public Bitmap getImage() {
        return image;
    }

    public Bitmap createImage() {
        return createImage(COLOR_0, COLOR_1);
    }

    public Bitmap createImage(int color0, int color1) {
        int width = getSize();
        int height = getSize();
        int[] pixels = new int[width * height];
        int pixelsIndex = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (this.cells[i][j].getCellState() == CellState.BLACK)
                    pixels[pixelsIndex] = color0;
                else
                    pixels[pixelsIndex] = color1;
                pixelsIndex ++;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public boolean mark(CellState cellState, int row, int column) {
        boolean isChanged = false;
        if (isInBounds(row, column)) {
            Cell cell = this.cells[row][column];

            if (cellState == CellState.BLACK) {
                switch (cell.getCellState()) {
                    case BLACK:
                        this.numberOfMarkedCells--;
                        cell.setCellState(CellState.WHITE);
                        break;
                    case WHITE:
                        this.numberOfMarkedCells++;
                        cell.setCellState(CellState.BLACK);
                        break;
                    case X:
                        this.numberOfMarkedCells++;
                        cell.setCellState(CellState.BLACK);
                        break;
                }
            } else if (cellState == CellState.X) {
                switch (cell.getCellState()) {
                    case BLACK:
                        this.numberOfMarkedCells--;
                        cell.setCellState(CellState.X);
                        break;
                    case WHITE:
                        cell.setCellState(CellState.X);
                        break;
                    case X:
                        cell.setCellState(CellState.WHITE);
                        break;
                }
            }
            isChanged = true;
            updateSolutionNumbers();
        }

        return isChanged;
    }

    private boolean isInBounds(int row, int column) {
        return (row >= 0 && row < this.size) && (column >= 0 && column < this.size);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int row = 0 ; row < this.size ; row++) {
            for (int column =  0 ; column < this.size ; column++) {
                if (this.cells[row][column].getCellState() == CellState.WHITE)
                    stringBuilder.append(' ');
                else
                    stringBuilder.append('X');
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public Cell[][] getCells() {
        return cells;
    }

    /** Two boards are equal when their number conditions match.
     * @see <a href="https://en.wikipedia.org/wiki/Nonogram#Multiple_solutions">
     *     Nonogram - Multiple solutions</a>  */
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof GameBoard))
            return false;

        GameBoard another = (GameBoard)o;
        this.updateSolutionNumbers();
        another.updateSolutionNumbers();

        return Arrays.equals(this.getRowNumbersLists(), another.getRowNumbersLists()) &&
                Arrays.equals(this.getColumnNumbersLists(), another.getColumnNumbersLists());
    }

    public int getNumberOfMarkedCells() {
        return numberOfMarkedCells;
    }

    public String getCellsAsString() {
        StringBuilder stringBuilder = new StringBuilder("");
        int size = getSize();

        for (int row = 0 ; row < size ; row++) {
            for (int column = 0 ; column < size ; column++) {
                switch (this.cells[row][column].getCellState()) {
                    case BLACK:
                        stringBuilder.append("1");
                        break;
                    case WHITE:
                        stringBuilder.append("0");
                        break;
                    case X:
                        stringBuilder.append("2");
                        break;
                }
            }
        }
        return stringBuilder.toString();
    }

    private boolean checkBoardSize(int[] cells) {
        int size = cells.length;
        return (Math.pow((int)Math.sqrt(size),2) == size);

    }

    public static String makeAnEmptyBoard(int size) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0 ; i < size ; i++)
            for (int j = 0 ; j < size ; j++)
                stringBuilder.append("0");
        return stringBuilder.toString();
    }
}
