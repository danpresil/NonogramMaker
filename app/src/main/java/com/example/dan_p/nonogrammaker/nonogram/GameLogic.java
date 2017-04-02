package com.example.dan_p.nonogrammaker.nonogram;

import java.util.ArrayList;

public class GameLogic {
    private GameState gameState;

    private Thread timer;
    private boolean runTimer;
    private int time;

    private GameBoard gameBoardSolution;
    private GameBoard gameBoard;

    public GameLogic(GameBoard gameBoard) {
        this(gameBoard, null, 0);
    }

    public GameLogic(GameBoard gameBoard, GameBoard gameBoardSavedProgress, final int gameTime){
        this.gameState = GameState.IN_PROGRESS;
        this.gameBoardSolution = gameBoard;
        if (gameBoardSavedProgress != null)
            this.gameBoard = gameBoardSavedProgress;
        else
            this.gameBoard = new GameBoard(gameBoardSolution.getSize());

        time = gameTime;
        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //time = gameTime;
                    while(gameState != GameState.WON){
                        timer.sleep(1000);
                        if (GameLogic.this.runTimer)
                            time++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        this.setRunTimer(true);
        this.timer.start();
    }

    private boolean isInBounds(int row, int column) {
        return (row >= 0 && row < this.gameBoard.getSize()) &&
                (column >= 0 && column < this.gameBoard.getSize());
    }

    public ArrayList<Integer>[] getRowNumberListsArray() {
        return this.gameBoardSolution.getRowNumbersLists();
    }

    public ArrayList<Integer>[] getColumnNumberListsArray() {
        return this.gameBoardSolution.getColumnNumbersLists();
    }

    public GameState makeMove(CellState cellState, int row, int column) {
        if (isInBounds(row, column)) {
            this.gameState = GameState.IN_PROGRESS;
            this.gameBoard.mark(cellState, row, column);
        }

        if (checkWin()) {
            setRunTimer(false);
            this.gameState = GameState.WON;
        }
        return this.gameState;
    }

    private boolean checkWin() {
        return this.gameBoard.equals(this.gameBoardSolution);
    }

    public int getSize() {
        return this.gameBoard.getSize();
    }

    public GameBoard getGameGrid() {
        return gameBoard;
    }

    public GameBoard getGameSolution() {
        return gameBoardSolution;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getTime() {
        return time;
    }

    public void setRunTimer(boolean runTimer) {
        if (this.gameState == GameState.IN_PROGRESS)
            this.runTimer = runTimer;
        else
            this.runTimer = false;
    }

}
