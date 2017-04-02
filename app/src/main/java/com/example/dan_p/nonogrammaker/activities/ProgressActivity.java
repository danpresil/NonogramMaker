package com.example.dan_p.nonogrammaker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.example.dan_p.nonogrammaker.database.ProgressEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProgressActivity extends AppCompatActivity {
    private TextView textView0;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;

    private String email = "";

    private int numberOfBoardAvailable = 0;
    private int numberOfSolvedBoards = 0;
    private int numberOfUploadedBoards = 0;
    private int numberOfCreatedBoards = 0;
    private double percentageSolved = 0;
    private String userKey = "";


    private HashMap<String, ProgressEntry> userProgressEntryHashMap = new HashMap<>();
    private HashMap<String, BoardEntry> userCreatedBoardEntriesHashMap = new HashMap<>();
    private HashMap<String, BoardEntry> allBoardEntriesHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        getViewsById();

        this.email = getIntent().getStringExtra("email");
        this.userKey = getIntent().getStringExtra("uid");

        if (this.email == null || this.userKey == null)
            finish();

        this.textView0.setText(String.format("Statistics for %s", email));

        queryProgression();
        queryCreatedBoards();
        queryUploadedBoards();
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
                calculateStats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void queryUploadedBoards() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL);
        mRootRef.child("boards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardEntry boardEntry = snapshot.getValue(BoardEntry.class);
                    String boardKey = snapshot.getKey();

                    allBoardEntriesHashMap.put(boardKey, boardEntry);

                }
                calculateStats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void queryCreatedBoards() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL);
        mRootRef.child("user_boards").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardEntry boardEntry = snapshot.getValue(BoardEntry.class);
                    String boardKey = snapshot.getKey();

                    userCreatedBoardEntriesHashMap.put(boardKey, boardEntry);

                }
                calculateStats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void calculateStats() {
        // Count solved boards
        int numberOfSolvedBoards = 0;
        for (ProgressEntry p : this.userProgressEntryHashMap.values()) {
            if (p.getSolved().compareTo("1111") == 0)
                numberOfSolvedBoards++;
        }
        this.numberOfSolvedBoards = numberOfSolvedBoards;

        // Count available boards
        // Count user uploaded boards
        int numberOfBoardAvailable = 0;
        int numberOfUploadedBoards = 0;
        for (BoardEntry b : this.allBoardEntriesHashMap.values()) {
            numberOfBoardAvailable++;
            if (b != null && b.getCreatorEmail().compareTo(email) == 0) //
                numberOfUploadedBoards++;
        }
        this.numberOfBoardAvailable = numberOfBoardAvailable;
        this.numberOfUploadedBoards = numberOfUploadedBoards;

        // Count user created (offline) boards
        int numberOfCreatedBoards = 0;
        for (BoardEntry b : this.userCreatedBoardEntriesHashMap.values()) {
            numberOfCreatedBoards++;
        }
        this.numberOfCreatedBoards = numberOfCreatedBoards;

        // Calculate stats
        if (numberOfBoardAvailable == 0)
            this.percentageSolved = 0;
        else
            this.percentageSolved = 100* (double) numberOfSolvedBoards / (double)(numberOfBoardAvailable);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextViewText();
            }
        });
    }

    private void getViewsById() {
        this.textView0 = (TextView) findViewById(R.id.textViewUserInfo);
        this.textView1 = (TextView) findViewById(R.id.textViewStats0);
        this.textView2 = (TextView) findViewById(R.id.textViewStats1);
        this.textView3 = (TextView) findViewById(R.id.textViewStats2);
        this.textView4 = (TextView) findViewById(R.id.textViewStats3);
        this.textView5 = (TextView) findViewById(R.id.textViewStats4);
    }

    private void setTextViewText() {
        this.textView1.setText(String.format("Boards solved: %d", this.numberOfSolvedBoards));
        this.textView2.setText(String.format("Boards available online: %d",
                this.numberOfBoardAvailable));
        this.textView3.setText(String.format("Progress: %.2f%s", this.percentageSolved,"%"));
        this.textView4.setText(String.format("Boards created: %d", this.numberOfCreatedBoards));
        this.textView5.setText(String.format("Boards uploaded: %d", this.numberOfUploadedBoards));
    }
}
