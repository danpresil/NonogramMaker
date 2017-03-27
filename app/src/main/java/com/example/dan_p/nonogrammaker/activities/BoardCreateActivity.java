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
    private Button buttonUploadBoard;
    private EditText editTextTag;
    private Button buttonDelete;
    private boolean deleted = false;

    private String boardKey;
    private String boardCreatorId;
    private String boardCreatorEmail;

    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        findViewsById();

        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);

        String boardCells0 = "";

        BoardEntry boardEntry = getIntent().getExtras().getParcelable("board");
        if (boardEntry != null) {
            boardCells0 = boardEntry.getCells();
            this.boardCreatorId = boardEntry.getCreatorId();
            this.boardCreatorEmail = boardEntry.getCreatorEmail();
            this.editTextTag.setText(boardEntry.getTag());
        }

        String key = getIntent().getStringExtra("key");
        if (key != null)
            this.boardKey = key;

        try {
            this.gameBoard = new GameBoard(boardCells0);
        } catch (BoardSizeException e) {
            e.printStackTrace();
        }

        cellImages = getImageCellsFromView();
        updateCells();
        setActionListeners();
    }

    private void findViewsById() {
        this.gameGrid = (GridLayout)findViewById(R.id.create_board_grid);
        this.buttonUploadBoard = (Button) findViewById(R.id.button_upload_board);
        this.editTextTag = (EditText) findViewById(R.id.editText_tag);
        this.buttonDelete = (Button) findViewById(R.id.button_delete);
    }

    private void setActionListeners() {
        this.buttonUploadBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BoardCreateActivity.this)
                    .setTitle("Upload board")
                    .setMessage("You can't edit a board after you uploading it.\n" +
                            "Are you sure you want to upload this board?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            uploadBoard();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            }
        });

        this.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BoardCreateActivity.this)
                    .setTitle("Delete board")
                    .setMessage("Are you sure you want to delete this board?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteBoard();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            }
        });
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
        if (!deleted)
            saveBoard(); // Save board on pause
    }

    private void saveBoard() {
        // update cells + tag by key
        if (this.boardKey != null) {
            mRootRef.child("user_boards").child(this.boardCreatorId).child(boardKey)
                    .child("cells").setValue(this.gameBoard.getCellsAsString());

            mRootRef.child("user_boards").child(this.boardCreatorId).child(boardKey)
                    .child("tag").setValue(this.editTextTag.getText().toString());
        }
    }

    private void uploadBoard() {
        String key = mRootRef.child("boards").push().getKey();
        BoardEntry boardEntry = new BoardEntry(
                gameBoard.getCellsAsString(),
                editTextTag.getText().toString(), boardCreatorId, boardCreatorEmail);
        Map<String, Object> gameBoardDataValues = boardEntry.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/boards/" + key, gameBoardDataValues);

        mRootRef.updateChildren(childUpdates);

        deleteBoard();
    }

    private void deleteBoard() {
        if (this.boardKey != null) {
            mRootRef.child("user_boards").child(this.boardCreatorId).child(boardKey).removeValue();
            deleted = true;
            finish();
        }
    }
}
