package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class PlaySelectActivity extends AppCompatActivity {
    private static final int COLOR_0 = 0xFF000000;
    private static final int COLOR_1 = 0xFFC5C5C5;

    private ImageView imageView0;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private String boardKey;
    private BoardEntry boardEntry;

    private ProgressEntry progressEntry;

    private DatabaseReference mRootRef;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_select);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);

        String key = getIntent().getStringExtra("key");
        if (key != null)
            this.boardKey = key;


    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference mBoardsRef = this.mRootRef.child("boards").child(boardKey);
        mBoardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    mRootRef.child("progression").child(firebaseUser.getUid()).child(boardKey)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    progressEntry = dataSnapshot
                                            .getValue(ProgressEntry.class);

                                    findViewsById();
                                    setImages();
                                    setActionListeners();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
                else {
                    Toast.makeText(PlaySelectActivity.this,
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
            if (this.progressEntry != null && this.progressEntry.getSolved().charAt(0) == '1')
                this.imageView0.setImageBitmap(boardEntry.createImage0(COLOR_0, COLOR_1));
            else
                this.imageView0.setImageResource(R.drawable.questionmarkred);

        if (this.imageView1 != null)
            if (this.progressEntry != null && this.progressEntry.getSolved().charAt(1) == '1')
                this.imageView1.setImageBitmap(boardEntry.createImage1(COLOR_0, COLOR_1));
            else
                this.imageView1.setImageResource(R.drawable.questionmarkred);

        if (this.imageView2 != null)
            if (this.progressEntry != null && this.progressEntry.getSolved().charAt(2) == '1')
                this.imageView2.setImageBitmap(boardEntry.createImage2(COLOR_0, COLOR_1));
            else
                this.imageView2.setImageResource(R.drawable.questionmarkred);

        if (this.imageView3 != null)
            if (this.progressEntry != null && this.progressEntry.getSolved().charAt(3) == '1')
                this.imageView3.setImageBitmap(boardEntry.createImage3(COLOR_0, COLOR_1));
            else
                this.imageView3.setImageResource(R.drawable.questionmarkred);

        TextView textViewTime = (TextView) findViewById(R.id.textView5);
        if (this.progressEntry != null && progressEntry.getTime() != -1)
            textViewTime.setText(String.format("Time: %d", progressEntry.getTime()));
        else
            textViewTime.setText("");

        TextView textViewInfo = (TextView) findViewById(R.id.textView2);
        textViewInfo.setText("Tag: " + boardEntry.getTag() + "\nCreated by: " + boardEntry.getCreatorEmail());

    }

    private void setActionListeners() {
        this.imageView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaySelectActivity.this, PlayBoardActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progressEntry != null && progressEntry.getProgress0() != null)
                    intent.putExtra("progress", progressEntry.getProgress0());
                if (progressEntry != null && progressEntry.getTime() != -1 )
                    intent.putExtra("time", progressEntry.getTime());
                if (progressEntry != null && progressEntry.getSolved() != null)
                    intent.putExtra("solved", progressEntry.getSolved());
                intent.putExtra("position", 0);

                startActivity(intent);
            }
        });

        this.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaySelectActivity.this, PlayBoardActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progressEntry != null && progressEntry.getProgress1() != null)
                    intent.putExtra("progress", progressEntry.getProgress1());
                if (progressEntry != null && progressEntry.getTime() != -1 )
                    intent.putExtra("time", progressEntry.getTime());
                if (progressEntry != null && progressEntry.getSolved() != null)
                    intent.putExtra("solved", progressEntry.getSolved());
                intent.putExtra("position", 1);

                startActivity(intent);
            }
        });

        this.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaySelectActivity.this, PlayBoardActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progressEntry != null && progressEntry.getProgress2() != null)
                    intent.putExtra("progress", progressEntry.getProgress2());
                if (progressEntry != null && progressEntry.getTime() != -1 )
                    intent.putExtra("time", progressEntry.getTime());
                if (progressEntry != null && progressEntry.getSolved() != null)
                    intent.putExtra("solved", progressEntry.getSolved());
                intent.putExtra("position", 2);

                startActivity(intent);
            }
        });

        this.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaySelectActivity.this, PlayBoardActivity.class);

                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                if (progressEntry != null && progressEntry.getProgress3() != null)
                    intent.putExtra("progress", progressEntry.getProgress3());
                if (progressEntry != null && progressEntry.getTime() != -1 )
                    intent.putExtra("time", progressEntry.getTime());
                if (progressEntry != null && progressEntry.getSolved() != null)
                    intent.putExtra("solved", progressEntry.getSolved());
                intent.putExtra("position", 3);

                startActivity(intent);
            }
        });
    }
}
