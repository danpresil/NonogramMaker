package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.nonogram.BoardSizeException;
import com.example.dan_p.nonogrammaker.nonogram.GameBoard;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BoardPlayMenuActivity extends AppCompatActivity {
    private DatabaseReference mRootRef;
    private ListView listViewBoards;
    private SearchView searchView;
    private String filter = "";
    private Query firebaseQuery;
    private String boardKey;

    private FirebaseListAdapter<BoardEntry> firebaseListAdapter;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.listViewBoards = (ListView)findViewById(R.id.boardlist_listview);
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);

        this.firebaseQuery = mRootRef.child("boards");
        firebaseListAdapter = new FirebaseListAdapter<BoardEntry>(
                this, BoardEntry.class, R.layout.rowlayout, firebaseQuery) {
            @Override
            protected void populateView(View v, final BoardEntry model, final int position) {
                //TODO add once datasnapshot here: for time and progress
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
                        boardKey = firebaseListAdapter.getRef(position).getKey();
                        mRootRef.child("progression").child(boardKey).child(firebaseUser.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Intent intent = new Intent(BoardPlayMenuActivity.this, GameActivity.class);

                                intent.putExtra("board", model);
                                intent.putExtra("key", boardKey);
                                if (dataSnapshot.child("progress") != null)
                                intent.putExtra("progress",
                                        dataSnapshot.child("progress").getValue(String.class));
                                if (dataSnapshot.child("time") != null )
                                    intent.putExtra("time",
                                            dataSnapshot.child("time").getValue(Integer.class));
                                if (dataSnapshot.child("solved") != null)
                                    intent.putExtra("solved",
                                            dataSnapshot.child("solved").getValue(Boolean.class));

                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        };

        listViewBoards.setAdapter(firebaseListAdapter);

//        RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup_boradlist_1);
//        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (filter.trim().length() == 0)
//                    mRootRef.child("boards");
//                else {
//                    switch (checkedId) {
//                        case R.id.radioButton4:
//                            firebaseQuery = mRootRef.child("boards").orderByChild("tag").equalTo(filter);
//                            break;
//                        case R.id.radioButton5:
//                            firebaseQuery = mRootRef.child("boards").orderByChild("creatorEmail").equalTo(filter);
//                            break;
//                    }
//                    firebaseListAdapter.notifyDataSetChanged();
//                    listViewBoards.setAdapter(firebaseListAdapter);
//                }
//            }
//        });
//        radioGroup1.check(R.id.radioButton4);
//
//        this.searchView = (SearchView) findViewById(R.id.searchview_filter);
//        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filter = query;
//                firebaseQuery = mRootRef.child("boards").orderByChild("tag").equalTo(filter);
//                firebaseListAdapter.notifyDataSetChanged();
//                listViewBoards.setAdapter(firebaseListAdapter);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }
}
