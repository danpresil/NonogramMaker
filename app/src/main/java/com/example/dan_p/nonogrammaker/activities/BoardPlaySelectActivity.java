package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dan_p.nonogrammaker.R;
import com.example.dan_p.nonogrammaker.database.BoardEntry;
import com.example.dan_p.nonogrammaker.database.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BoardPlaySelectActivity extends AppCompatActivity {
    private static final int color0 = 0xffa9a9a9;
    private static final int color1 = 0xff00304e;

    private ImageView imageView0;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private String boardKey;
    private BoardEntry boardEntry;

    private String progress0;
    private String progress1;
    private String progress2;
    private String progress3;
    private int time;
    private String solved;

    private DatabaseReference mRootRef;
    private DatabaseReference mBoardsRef;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_play_select);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);

        String key = getIntent().getStringExtra("key");
        if (key != null)
            this.boardKey = key;


    }

    @Override
    protected void onStart() {
        super.onStart();

        this.mBoardsRef = this.mRootRef.child("boards").child(boardKey);
        this.mBoardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String cells0 = (String) dataSnapshot.child("cells0").getValue();
                    String cells1 = (String) dataSnapshot.child("cells1").getValue();
                    String cells2 = (String) dataSnapshot.child("cells2").getValue();
                    String cells3 = (String) dataSnapshot.child("cells3").getValue();
                    String tag = (String) dataSnapshot.child("tag").getValue();
                    String creatorId = (String) dataSnapshot.child("creatorId").getValue();
                    String creatorEmail = (String) dataSnapshot.child("creatorEmail").getValue();

                    boardEntry = new BoardEntry(cells0, cells1, cells2, cells3, tag, creatorId, creatorEmail);

                    mRootRef.child("progression").child(boardKey).child(firebaseUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()){
                                    if (dataSnapshot.child("progress0").getValue() != null)
                                        progress0 = dataSnapshot.child("progress0").getValue(String.class);
                                    if (dataSnapshot.child("progress0") != null)
                                        progress1 = dataSnapshot.child("progress1").getValue(String.class);
                                    if (dataSnapshot.child("progress0").getValue() != null)
                                        progress2 = dataSnapshot.child("progress2").getValue(String.class);
                                    if (dataSnapshot.child("progress0").getValue() != null)
                                        progress3 = dataSnapshot.child("progress3").getValue(String.class);
                                    if (dataSnapshot.child("time").getValue() != null)
                                        time = dataSnapshot.child("time").getValue(Integer.class);
                                    if (dataSnapshot.child("solved").getValue() != null)
                                        solved = dataSnapshot.child("solved").getValue(String.class);
                                    else
                                        solved = "0000";

                                    findViewsById();
                                    setImages();
                                    setActionListeners();
//                                    } else {
//                                        Toast.makeText(BoardPlaySelectActivity.this,
//                                                "Failed to fetch progress", Toast.LENGTH_SHORT).show();
//                                        finish();
//                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
                else {
                    Toast.makeText(BoardPlaySelectActivity.this,
                            "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void findViewsById() {
        this.imageView0 = (ImageView)findViewById(R.id.imageViewPlay0);
        this.imageView1 = (ImageView)findViewById(R.id.imageViewPlay1);
        this.imageView2 = (ImageView)findViewById(R.id.imageViewPlay2);
        this.imageView3 = (ImageView)findViewById(R.id.imageViewPlay3);
    }

    private void setImages() {
        if (this.imageView0 != null)
            if (this.solved.charAt(0) == '1')
                this.imageView0.setImageBitmap(boardEntry.createImage0(color0, color1));
            else
                this.imageView0.setImageResource(R.drawable.questionmarkblue);

        if (this.imageView1 != null)
            if (this.solved.charAt(1) == '1')
                this.imageView1.setImageBitmap(boardEntry.createImage1(color0, color1));
            else
                this.imageView1.setImageResource(R.drawable.questionmarkblue);

        if (this.imageView2 != null)
            if (this.solved.charAt(2) == '1')
                this.imageView2.setImageBitmap(boardEntry.createImage2(color0, color1));
            else
                this.imageView2.setImageResource(R.drawable.questionmarkblue);

        if (this.imageView3 != null)
            if (this.solved.charAt(3) == '1')
                this.imageView3.setImageBitmap(boardEntry.createImage3(color0, color1));
            else
                this.imageView3.setImageResource(R.drawable.questionmarkblue);
    }

    private void setActionListeners() {
        this.imageView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardPlaySelectActivity.this, GameActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progress0 != null)
                    intent.putExtra("progress", progress0);
                if (time != -1 )
                    intent.putExtra("time", time);
                if (solved != null)
                    intent.putExtra("solved", solved);
                intent.putExtra("position", 0);

                startActivity(intent);
            }
        });

        this.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardPlaySelectActivity.this, GameActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progress1 != null)
                    intent.putExtra("progress", progress1);
                if (time != -1 )
                    intent.putExtra("time", time);
                if (solved != null)
                    intent.putExtra("solved", solved);
                intent.putExtra("position", 1);

                startActivity(intent);
            }
        });

        this.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardPlaySelectActivity.this, GameActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progress2 != null)
                    intent.putExtra("progress", progress2);
                if (time != -1 )
                    intent.putExtra("time", time);
                if (solved != null)
                    intent.putExtra("solved", solved);
                intent.putExtra("position", 2);

                startActivity(intent);
            }
        });

        this.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardPlaySelectActivity.this, GameActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progress3 != null)
                    intent.putExtra("progress", progress3);
                if (time != -1 )
                    intent.putExtra("time", time);
                if (solved != null)
                    intent.putExtra("solved", solved);
                intent.putExtra("position", 3);

                startActivity(intent);
            }
        });
    }
}
