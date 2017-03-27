package com.example.dan_p.nonogrammaker.activities;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.nonogram.BoardSizeException;
import com.example.dan_p.nonogrammaker.nonogram.Cell;
import com.example.dan_p.nonogrammaker.nonogram.CellImage;
import com.example.dan_p.nonogrammaker.nonogram.CellState;
import com.example.dan_p.nonogrammaker.nonogram.GameBoard;
import com.example.dan_p.nonogrammaker.nonogram.GameLogic;
import com.example.dan_p.nonogrammaker.nonogram.GameState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    public static GameActivity gameActivity;

    private RelativeLayout relativeLayout;
    private GameLogic gameLogic;
    private GridLayout gameGrid;
    private CellImage[][] cellImages;
    private TextView[] rowTextViews;
    private TextView[] columnTextViews;
    private RadioGroup actionsGroup;
    private TextView textViewNumberOfMinesLeft;
    private TextView textViewBestTime;
    private Thread timer;

    private static final String TAG = GameActivity.class.getSimpleName();

    private CellImage touchedCellImage;
    private CellImage clickedOnCellImage;
    private String boardCreatorId;
    private String boardKey;
    private String boardCreatorEmail;
    private boolean isSolved;

    private DatabaseReference mRootRef;
    private FirebaseUser firebaseUser;
    private int startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_15x15);
        gameActivity = this;

        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        this.gameLogic = createGameLogic();
        getViewsById();
        updateNumberTextViews();

        //TODO start time only if its the first game
        this.startTime = gameLogic.getTime();
        this.timer = new Thread(new GameTimer());
        this.timer.start();
        if (this.isSolved) {
            this.textViewBestTime.setText("Your best time is : " + this.startTime);
        } else {
            //timer.start();
            this.textViewBestTime.setText("Time : " + this.startTime);
        }
        updateCellImages();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.gameLogic.setRunTimer(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.gameLogic.setRunTimer(false);
        // todo send progress
        saveProgress();
    }

    //TODO fix this method
    private GameLogic createGameLogic() {
        BoardEntry boardEntry = getIntent().getExtras().getParcelable("board");
        String cells = "";
        if (boardEntry != null) {
            cells = boardEntry.getCells(); //TODO switch by placement
            this.boardCreatorId = boardEntry.getCreatorId();
            this.boardCreatorEmail = boardEntry.getCreatorEmail();
        }

        String key = getIntent().getStringExtra("key");
        if (key != null)
            this.boardKey = key;
        else
            return null;

        int time = getIntent().getIntExtra("time", 0);
        String progress = getIntent().getStringExtra("progress");
        this.isSolved = getIntent().getBooleanExtra("solved", false);

        //TODO get sloved intent

        GameLogic gameLogic = null;
        try {
            GameBoard gameBoard = new GameBoard(cells);
            if (!this.isSolved && progress != null) {
                // Continue game using the solution progress
                gameLogic = new GameLogic(gameBoard, new GameBoard(progress), time);
            } else {
                // Start a new game
                gameLogic = new GameLogic(gameBoard);
                this.cellImages = new CellImage[gameLogic.getSize()][gameLogic.getSize()];
            }
        } catch (BoardSizeException e) {
            e.printStackTrace();
        }

        return gameLogic;
    }

    private void getViewsById() {
        this.gameGrid = getGameGridFromView();
        this.cellImages = getCellButtonsFromView();
        this.rowTextViews = getRowTextViewsFromView();
        this.columnTextViews = getColumnTextViewsFromView();
        this.actionsGroup = (RadioGroup) findViewById(R.id.radio_group_actions);
        this.actionsGroup.check(R.id.mine_button);
        this.relativeLayout = (RelativeLayout)findViewById(R.id.relativelayout_activity_game);
        this.textViewBestTime = (TextView) findViewById(R.id.text_current_time);
    }

    private GridLayout getGameGridFromView() {
        return (GridLayout)findViewById(R.id.game_grid);
    }

    private CellImage[][] getCellButtonsFromView() {
        GridLayout gridLayout = (GridLayout)findViewById(R.id.game_grid);

        if (gridLayout == null)
            return null;

        int size = this.gameLogic.getSize();
        CellImage[][] cells = new CellImage[size][size];

        for (int row = 0 ; row < size ; row++) {
            for (int column = 0 ; column < size ; column++) {
                CellImage cellImage = (CellImage)gridLayout.getChildAt((row+1)*(size+1)+(column+1));
                if (cellImage != null) {
                    cellImage.setPosition(row, column);
                    cellImage.setOnClickListener(this);
                    cellImage.setOnTouchListener(this);
                    cells[row][column] = cellImage;
                }
            }
        }
        return cells;
    }

    private TextView[] getRowTextViewsFromView() {
        GridLayout gridLayout = (GridLayout)findViewById(R.id.game_grid);

        if (gridLayout == null)
            return null;

        int size = this.gameLogic.getSize();
        TextView[] rowTextViews = new TextView[size];

        for (int row = 0 ; row < size ; row++) {
                TextView textView = (TextView)gridLayout.getChildAt((row+1)*(size+1));
                if (textView != null)
                    rowTextViews[row] = textView;
        }
        return rowTextViews;
    }

    private TextView[] getColumnTextViewsFromView() {
        GridLayout gridLayout = (GridLayout)findViewById(R.id.game_grid);

        if (gridLayout == null)
            return null;

        int size = this.gameLogic.getSize();
        TextView[] columnTextViews = new TextView[size];

        for (int column = 0; column < size ; column++) {
            TextView textView = (TextView)gridLayout.getChildAt(column + 1);
            if (textView != null)
                columnTextViews[column] = textView;
        }
        return columnTextViews;
    }

    /** Show the puzzle hint numbers */
    private void updateNumberTextViews() {
        int size = this.gameLogic.getSize();

        ArrayList<Integer>[] columnsArrayLists = this.gameLogic.getColumnNumberListsArray();
        for (int column = 0 ; column < size ; column++) {
            StringBuilder columnStringBuilder = new StringBuilder("");
            for (Integer i : columnsArrayLists[column])
                columnStringBuilder.append(i).append("\n");
            columnStringBuilder.deleteCharAt(columnStringBuilder.length()-1);
            this.columnTextViews[column].setText(columnStringBuilder.toString());

            //TODO if list size > 5, change text size
            if (columnsArrayLists.length > 5)
                this.columnTextViews[column].setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.small_size_hint_font));
            else
                this.columnTextViews[column].setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.medium_size_hint_font));
        }

        ArrayList<Integer>[] rowsArrayLists = this.gameLogic.getRowNumberListsArray();
        for (int row = 0 ; row < size ; row++) {
            StringBuilder rowStringBuilder = new StringBuilder("");
            for (Integer i : rowsArrayLists[row])
                rowStringBuilder.append(i).append(" ");
            rowStringBuilder.deleteCharAt(rowStringBuilder.length()-1);
            this.rowTextViews[row].setText(rowStringBuilder.toString());

            if (rowsArrayLists.length > 5)
                this.rowTextViews[row].setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.small_size_hint_font));
            else
                this.rowTextViews[row].setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.medium_size_hint_font));
        }
    }

    private void updateCellImages() {
        for (int row = 0 ; row < this.gameLogic.getSize() ; row++ ) {
            for (int column = 0 ; column < this.gameLogic.getSize() ; column++ ) {
                CellImage cellImage = cellImages[row][column];
                Cell cell = (this.gameLogic.getGameGrid().getCells())[row][column];

                switch (cell.getCellState()) {
                    case WHITE:
                        cellImage.setImageResource(R.drawable.cell0);
                        break;
                    case BLACK:
                        //TODO int res = getResources().getIdentifier("com.example.dap_p.nonogrammaker:drawable/cellminebackground", null, null);
                        cellImage.setImageResource(R.drawable.cell1);
                        break;
                    case X:
                        cellImage.setImageResource(R.drawable.cell2);
                        break;
                }
            }
        }
    }

    private void checkWin() {
        if (gameLogic.getGameState() == GameState.WON) {
            PlayEndGameAnimation(GameState.WON);
            this.isSolved = true;

            // Disable grid clicks
            for (int row = 0 ; row < this.gameLogic.getSize() ; row++ )
                for (int column = 0 ; column < this.gameLogic.getSize() ; column++ )
                    cellImages[row][column].setEnabled(false);

            saveProgress();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }
    }

    private void PlayEndGameAnimation(GameState gameState) {
        if (gameState == GameState.WON) {
            relativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorValue1));

        }
    }

    private void saveProgress() {
        if (this.boardKey != null) {
            // Solution progress
            mRootRef.child("progression").child(boardKey).child(firebaseUser.getUid())
                    .child("progress").setValue(this.gameLogic.getGameGrid().getCellsAsString());

//            if (!isSolved) {
                // Game time
                mRootRef.child("progression").child(boardKey).child(firebaseUser.getUid())
                        .child("time").setValue(this.gameLogic.getTime());

                // Game status: in progress / solved
                mRootRef.child("progression").child(boardKey).child(firebaseUser.getUid())
                        .child("solved").setValue(isSolved);

//            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof CellImage) {
            this.touchedCellImage = (CellImage)v;
            this.touchedCellImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v instanceof CellImage) {
                        CellImage cellImage = (CellImage) v;
                        int row = cellImage.getRow();
                        int column = cellImage.getColumn();

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                rowTextViews[row].setBackgroundColor(Color.RED);
                                columnTextViews[column].setBackgroundColor(Color.RED);
                                break;
                            case MotionEvent.ACTION_UP:
                                rowTextViews[row].setBackgroundColor(Color.WHITE);
                                columnTextViews[column].setBackgroundColor(Color.WHITE);
                                break;
                        }
                    }
                    return false;
                }
            });
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CellImage) {
            this.clickedOnCellImage = (CellImage)v;
            switch (actionsGroup.getCheckedRadioButtonId()) {
                case R.id.mine_button:
                    gameLogic.makeMove(CellState.BLACK, clickedOnCellImage.getRow(), clickedOnCellImage.getColumn());
                    break;
                case R.id.flag_button:
                    gameLogic.makeMove(CellState.X, clickedOnCellImage.getRow(), clickedOnCellImage.getColumn());
                    break;
            }
            updateCellImages();
            checkWin();
        }

    }

    class GameTimer implements Runnable {
        @Override
        public void run() {
            try {
                while(gameLogic.getGameState() != GameState.WON){
                    timer.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewBestTime.setText(String.format("Time: %d",gameLogic.getTime()));
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

