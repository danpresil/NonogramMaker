package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.nonogram.BoardSizeException;
import com.example.dan_p.nonogrammaker.nonogram.GameBoard;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BoardCreateMenuActivity extends AppCompatActivity {
    private final static int SIZE = 15;

    private Button buttonCreateNewBoard;

    private ListView mUserListView;
    private DatabaseReference mRootRef;
    private FirebaseListAdapter<BoardEntry> firebaseListAdapterMyPuzzles;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_boards_list);

        this.buttonCreateNewBoard = (Button) findViewById(R.id.button_create_new_board);
        this.mUserListView = (ListView)findViewById(R.id.createboardlist_listview);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseListAdapterMyPuzzles = new FirebaseListAdapter<BoardEntry>(
                this, BoardEntry.class, R.layout.rowlayout, mRootRef.child("user_boards").child(firebaseUser.getUid())) {

            @Override
            protected void populateView(View v, final BoardEntry model, final int position) {
                TextView textViewCreator = (TextView) v.findViewById(R.id.textView_row_creator);
                textViewCreator.setText(model.getCreatorEmail());
                TextView textViewTag = (TextView) v.findViewById(R.id.textView_row_tag);
                textViewTag.setText(String.format("#%s", model.getTag()));
                try {
                    GameBoard gameBoard = new GameBoard(model.getCells());
                    ImageView imageView = (ImageView) v.findViewById(R.id.row_imageview0);
                    imageView.setImageBitmap(gameBoard.createImage());
                } catch (BoardSizeException e) {
                    e.printStackTrace();
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BoardCreateMenuActivity.this, "Clicked on: " + position +
                                model.getTag(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BoardCreateMenuActivity.this, BoardCreateActivity.class);
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
                BoardEntry g = new BoardEntry(emptyBoard, tag, creator, creator_email);

                // The key to the new empty board
                String key = writeNewBoard(emptyBoard, tag, creator, creator_email);

                Intent intent = new Intent(BoardCreateMenuActivity.this, BoardCreateActivity.class);
                intent.putExtra("board", g);
                intent.putExtra("key", key);

                // The user can edit the empty board in the following intent
                startActivity(intent);
            }
        });
    }

    /** returns the board key */
    private String writeNewBoard(String cells, String tag, String creatorId, String creatorEmail) {
        String key = mRootRef.child("user_boards").child(creatorId).push().getKey();
        BoardEntry boardEntry = new BoardEntry(cells, tag, creatorId, creatorEmail);
        Map<String, Object> gameBoardDataValues = boardEntry.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user_boards/" + creatorId + "/" + key, gameBoardDataValues);

        mRootRef.updateChildren(childUpdates);

        return key;
    }

}
