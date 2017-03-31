package com.example.dan_p.nonogrammaker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.nonogram.BoardSizeException;
import com.example.dan_p.nonogrammaker.nonogram.CellImage;
import com.example.dan_p.nonogrammaker.nonogram.CellState;
import com.example.dan_p.nonogrammaker.nonogram.GameBoard;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BoardCreateActivity extends AppCompatActivity implements View.OnClickListener{

    private final int NUMBER_OF_CELLS = 225;
    private final int SIZE = 15;

    private GridLayout gameGrid;
    private GameBoard gameBoard;
    private CellImage[][] cellImages;

    private EditText editTextTag;
    private int position;
    private String boardKey;
//    private String boardCreatorId;
//    private String boardCreatorEmail;
    private BoardEntry boardEntry;

    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        findViewsById();

        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);

        BoardEntry boardEntry = getIntent().getExtras().getParcelable("board");
        if (boardEntry != null) {
            this.boardEntry = boardEntry;
//            boardCells0 = boardEntry.getCells();
//            this.boardCreatorId = boardEntry.getCreatorId();
//            this.boardCreatorEmail = boardEntry.getCreatorEmail();
            this.editTextTag.setText(this.boardEntry.getTag());
//            this.position = boardEntry.getPositon();
        }
        else {
            finish();
        }

        this.position = getIntent().getIntExtra("position", -1);

        String key = getIntent().getStringExtra("key");
        if (key != null)
            this.boardKey = key;

        try {
            switch (this.position) {
                case 0:
                    if (boardEntry.getCells0() == null)
                        this.gameBoard = new GameBoard(GameBoard.makeAnEmptyBoard(SIZE));
                    else
                        this.gameBoard = new GameBoard(boardEntry.getCells0());
                    break;
                case 1:
                    if (boardEntry.getCells1() == null)
                        this.gameBoard = new GameBoard(GameBoard.makeAnEmptyBoard(SIZE));
                    else
                        this.gameBoard = new GameBoard(boardEntry.getCells1());
                    break;
                case 2:
                    if (boardEntry.getCells2() == null)
                        this.gameBoard = new GameBoard(GameBoard.makeAnEmptyBoard(SIZE));
                    else
                        this.gameBoard = new GameBoard(boardEntry.getCells2());
                    break;
                case 3:
                    if (boardEntry.getCells3() == null)
                        this.gameBoard = new GameBoard(GameBoard.makeAnEmptyBoard(SIZE));
                    else
                        this.gameBoard = new GameBoard(boardEntry.getCells3());
                    break;
                default:
                    finish();
            }

        } catch (BoardSizeException e) {
            e.printStackTrace();
        }

        cellImages = getImageCellsFromView();
        updateCells();
        //setActionListeners();
    }

    private void findViewsById() {
        this.gameGrid = (GridLayout)findViewById(R.id.create_board_grid);

        this.editTextTag = (EditText) findViewById(R.id.editText_tag);

    }

    private CellImage[][] getImageCellsFromView() {
        CellImage[][] cellImages = new CellImage[SIZE][SIZE];
        for (int i = 0 ; i < NUMBER_OF_CELLS ; i++) {
            int row = i / SIZE;
            int column = i % SIZE;
            cellImages[row][column] = (CellImage) this.gameGrid.getChildAt((row) * (SIZE) + (column));
            cellImages[row][column].setPosition(row, column);
            cellImages[row][column] = (CellImage) this.gameGrid.getChildAt(i);
            cellImages[row][column].setOnClickListener(this);
        }

        return cellImages;
    }

    private void updateCells() {
        for (int i = 0 ; i < NUMBER_OF_CELLS ; i++) {
            int row = i / SIZE;
            int column = i % SIZE;
            switch (gameBoard.getCells()[row][column].getCellState()) {
                case BLACK:
                    cellImages[row][column].setImageResource(R.drawable.cell1);
                    break;
                case WHITE:
                    cellImages[row][column].setImageResource(R.drawable.cell0);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CellImage) {
            CellImage cellImage = (CellImage)v;
            gameBoard.mark(CellState.BLACK, cellImage.getRow(), cellImage.getColumn());
            updateCells();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveBoard(); // Save board on pause
    }

    private void saveBoard() {
        // update cells + tag by key
        if (this.boardKey != null) {
            mRootRef.child("user_boards").child(this.boardEntry.getCreatorId()).child(boardKey)
                    .child("cells"+position).setValue(this.gameBoard.getCellsAsString());

            mRootRef.child("user_boards").child(this.boardEntry.getCreatorId()).child(boardKey)
                    .child("tag").setValue(this.editTextTag.getText().toString());
        }
    }


}
