package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.database.ProgressEntry;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PlayListActivity extends AppCompatActivity {
    private static final int COLOR_0 = 0xFF000000;
    private static final int COLOR_1 = 0xFFC5C5C5;


    private DatabaseReference mRootRef;
    private ListView listViewBoards;
    private SearchView searchView;
    private String filter = "";
    private Query firebaseQuery;

    private String email;
    private String userKey = "";

    private FirebaseListAdapter<BoardEntry> firebaseListAdapter;
    private FirebaseUser firebaseUser;
    private HashMap<String, ProgressEntry> userProgressEntryHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        this.email = getIntent().getStringExtra("email");
        this.userKey = getIntent().getStringExtra("uid");

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.listViewBoards = (ListView)findViewById(R.id.boardlist_listview);
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);
        queryProgression();

        SearchView sv = (SearchView) findViewById(R.id.searchview_filter);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                createBoardList(newText);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryProgression();
    }

    private void createBoardList(String tagfilter) {
        if(tagfilter != null && (tagfilter.trim().length() != 0))
            this.firebaseQuery = mRootRef.child("boards").orderByChild("tag").startAt(tagfilter);
        else
            this.firebaseQuery = mRootRef.child("boards");

        firebaseListAdapter = new FirebaseListAdapter<BoardEntry>(
                this, BoardEntry.class, R.layout.rowlayout, firebaseQuery) {
            @Override
            protected void populateView(View v, final BoardEntry model, final int position) {
                TextView textViewCreator = (TextView) v.findViewById(R.id.textView_row_creator);
                textViewCreator.setText(model.getCreatorEmail());
                TextView textViewTag = (TextView) v.findViewById(R.id.textView_row_tag);
                textViewTag.setText(String.format("#%s", model.getTag()));

                String boardKey0 = firebaseListAdapter.getRef(position).getKey();
                ProgressEntry progress = userProgressEntryHashMap.get(boardKey0);

                TextView textViewTime = (TextView) v.findViewById(R.id.textView_row_bestTime);
                if (progress == null)
                    textViewTime.setText("No time set");
                else {
                    textViewTime.setText(String.format("My time: %d", progress.getTime()));

                    if (userProgressEntryHashMap.containsKey(boardKey0) && userProgressEntryHashMap
                            .get(boardKey0).getSolved().charAt(0) == '1') {
                        ImageView imageView0 = (ImageView) v.findViewById(R.id.row_imageview0);
                        imageView0.setImageBitmap(model.createImage0(COLOR_0, COLOR_1));
                    }

                    if (userProgressEntryHashMap.containsKey(boardKey0) && userProgressEntryHashMap
                            .get(boardKey0).getSolved().charAt(1) == '1') {
                        ImageView imageView1 = (ImageView) v.findViewById(R.id.row_imageview1);
                        imageView1.setImageBitmap(model.createImage1(COLOR_0, COLOR_1));
                    }

                    if (userProgressEntryHashMap.containsKey(boardKey0) && userProgressEntryHashMap
                            .get(boardKey0).getSolved().charAt(2) == '1') {
                        ImageView imageView2 = (ImageView) v.findViewById(R.id.row_imageview2);
                        imageView2.setImageBitmap(model.createImage2(COLOR_0, COLOR_1));
                    }

                    if (userProgressEntryHashMap.containsKey(boardKey0) && userProgressEntryHashMap
                            .get(boardKey0).getSolved().charAt(3) == '1') {
                        ImageView imageView3 = (ImageView) v.findViewById(R.id.row_imageview3);
                        imageView3.setImageBitmap(model.createImage3(COLOR_0, COLOR_1));
                    }
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String boardKey = firebaseListAdapter.getRef(position).getKey();
                        Intent intent = new Intent(PlayListActivity.this, PlaySelectActivity.class);
                        intent.putExtra("key", boardKey);
                        startActivity(intent);
                    }
                });
            }
        };
        listViewBoards.setAdapter(firebaseListAdapter);
    }

    private void queryProgression() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL);
        mRootRef.child("progression").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ProgressEntry userProgression = snapshot.getValue(ProgressEntry.class);
                    String boardKey = snapshot.getKey();
                    userProgressEntryHashMap.put(boardKey, userProgression);
                }

                createBoardList("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
