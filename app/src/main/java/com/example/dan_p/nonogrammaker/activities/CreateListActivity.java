package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.nonogram.BoardSizeException;
import com.example.dan_p.nonogrammaker.nonogram.GameBoard;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateListActivity extends AppCompatActivity {
    private static final int COLOR_0 = 0xFF000000;
    private static final int COLOR_1 = 0xFFC5C5C5;

    private final static int SIZE = 15;

    private Button buttonCreateNewBoard;

    private ListView mUserListView;
    private DatabaseReference mRootRef;
    private FirebaseListAdapter<BoardEntry> firebaseListAdapterMyPuzzles;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        this.buttonCreateNewBoard = (Button) findViewById(R.id.button_create);
        this.mUserListView = (ListView)findViewById(R.id.createboardlist_listview);
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseListAdapterMyPuzzles = new FirebaseListAdapter<BoardEntry>(
                this, BoardEntry.class, R.layout.rowlayout, mRootRef.child("user_boards").child(firebaseUser.getUid())) {

            @Override
            protected void populateView(View v, final BoardEntry model, final int position) {
                TextView textViewCreator = (TextView) v.findViewById(R.id.textView_row_creator);
                textViewCreator.setText(model.getCreatorEmail());
                TextView textViewTag = (TextView) v.findViewById(R.id.textView_row_tag);
                TextView textViewTime = (TextView) v.findViewById(R.id.textView_row_bestTime);
                textViewTime.setText("");
                textViewTag.setText(String.format("#%s", model.getTag()));
                try {
                    GameBoard gameBoard0 = new GameBoard(model.getCells0());
                    ImageView imageView0 = (ImageView) v.findViewById(R.id.row_imageview0);
                    imageView0.setImageBitmap(gameBoard0.createImage(COLOR_0, COLOR_1));

                    GameBoard gameBoard1 = new GameBoard(model.getCells1());
                    ImageView imageView1 = (ImageView) v.findViewById(R.id.row_imageview1);
                    imageView1.setImageBitmap(gameBoard1.createImage(COLOR_0, COLOR_1));

                    GameBoard gameBoard2 = new GameBoard(model.getCells2());
                    ImageView imageView2 = (ImageView) v.findViewById(R.id.row_imageview2);
                    imageView2.setImageBitmap(gameBoard2.createImage(COLOR_0, COLOR_1));

                    GameBoard gameBoard3 = new GameBoard(model.getCells3());
                    ImageView imageView3 = (ImageView) v.findViewById(R.id.row_imageview3);
                    imageView3.setImageBitmap(gameBoard3.createImage(COLOR_0, COLOR_1));
                } catch (BoardSizeException e) {
                    e.printStackTrace();
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateListActivity.this, CreateSelectActivity.class);
                        intent.putExtra("board", model);
                        intent.putExtra("key", firebaseListAdapterMyPuzzles.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }
        };

        mUserListView.setAdapter(firebaseListAdapterMyPuzzles);

        buttonCreateNewBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emptyBoard = GameBoard.makeAnEmptyBoard(SIZE);
                String tag = "new";
                String creator = firebaseUser.getUid();
                String creator_email = firebaseUser.getEmail();
                BoardEntry newBoard = new BoardEntry(emptyBoard, emptyBoard, emptyBoard, emptyBoard
                        , tag, creator, creator_email);

                // The key to the new empty board
                String key = writeNewBoard(emptyBoard, emptyBoard, emptyBoard, emptyBoard, tag, creator, creator_email);

                Intent intent = new Intent(CreateListActivity.this, CreateSelectActivity.class);
                intent.putExtra("board", newBoard);
                intent.putExtra("key", key);

                // The user can edit the empty board in the following intent
                startActivity(intent);
            }
        });
    }

    /** returns the board key */
    private String writeNewBoard(String cells0, String cells1, String cells2, String cells3, String tag, String creatorId, String creatorEmail) {
        // Generate a new key for the new board
        String key = mRootRef.child("user_boards").child(creatorId).push().getKey();
        BoardEntry boardEntry = new BoardEntry(cells0, cells1, cells2, cells3, tag, creatorId, creatorEmail);
        Map<String, Object> gameBoardDataValues = boardEntry.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user_boards/" + creatorId + "/" + key, gameBoardDataValues);

        mRootRef.updateChildren(childUpdates);

        return key;
    }

}
