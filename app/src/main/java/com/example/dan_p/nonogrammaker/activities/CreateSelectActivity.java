package com.example.dan_p.nonogrammaker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.util.HashMap;
import java.util.Map;

public class CreateSelectActivity extends AppCompatActivity {
    private static final int COLOR_0 = 0xFF000000;
    private static final int COLOR_1 = 0xFFC5C5C5;

    private ImageView imageView0;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private EditText editTextTag;

    boolean deleted = false;

    private String boardKey;
    private BoardEntry boardEntry;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserBoardRef;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_select);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);

        String key = getIntent().getStringExtra("key");
        if (key != null)
            this.boardKey = key;
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.mUserBoardRef = this.mRootRef.child("user_boards").child(firebaseUser.getUid()).child(boardKey);
        this.mUserBoardRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    findViewsById();
                    setImages();
                    setActionListeners();

                    editTextTag.setText(tag);
                }
                else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!deleted)
            updateTag();
    }

    private void updateTag() {
        String tag = editTextTag.getText().toString().trim();
        if (tag.length() > 0)
            mRootRef.child("user_boards").child(this.boardEntry.getCreatorId()).child(boardKey)
                    .child("tag").setValue(tag);
    }

    private void findViewsById() {
        this.imageView0 = (ImageView)findViewById(R.id.imageView0create);
        this.imageView1 = (ImageView)findViewById(R.id.imageView1create);
        this.imageView2 = (ImageView)findViewById(R.id.imageView2create);
        this.imageView3 = (ImageView)findViewById(R.id.imageView3create);
        this.editTextTag = (EditText) findViewById(R.id.editText2);

    }

    private void setImages() {
        if (this.imageView0 != null)
            this.imageView0.setImageBitmap(boardEntry.createImage0(COLOR_0, COLOR_1));

        if (this.imageView1 != null)
            this.imageView1.setImageBitmap(boardEntry.createImage1(COLOR_0, COLOR_1));

        if (this.imageView2 != null)
            this.imageView2.setImageBitmap(boardEntry.createImage2(COLOR_0, COLOR_1));

        if (this.imageView3 != null)
            this.imageView3.setImageBitmap(boardEntry.createImage3(COLOR_0, COLOR_1));
    }

    private void setActionListeners() {
        Button buttonUploadBoard = (Button) findViewById(R.id.buttonupload);
        Button buttonDelete = (Button) findViewById(R.id.buttondelete);

        buttonUploadBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CreateSelectActivity.this)
                        .setTitle("Upload board")
                        .setMessage("You can't edit a board after you uploading it.\n" +
                                "Are you sure you want to upload this board?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                uploadBoard();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CreateSelectActivity.this)
                        .setTitle("Delete board")
                        .setMessage("Are you sure you want to delete this board?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteBoard();


                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        this.imageView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBoardEntryTag();
                Intent intent = new Intent(CreateSelectActivity.this, CreateBoardActivity.class);
                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });

        this.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBoardEntryTag();
                Intent intent = new Intent(CreateSelectActivity.this, CreateBoardActivity.class);
                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                intent.putExtra("position", 1);
                startActivity(intent);
            }
        });

        this.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBoardEntryTag();
                Intent intent = new Intent(CreateSelectActivity.this, CreateBoardActivity.class);
                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                intent.putExtra("position", 2);
                startActivity(intent);
            }
        });

        this.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBoardEntryTag();
                Intent intent = new Intent(CreateSelectActivity.this, CreateBoardActivity.class);
                intent.putExtra("board", boardEntry);
                intent.putExtra("key", boardKey);
                intent.putExtra("position", 3);
                startActivity(intent);
            }
        });
    }
    private void uploadBoard() {
        updateBoardEntryTag();

        Map<String, Object> boardValues = boardEntry.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/boards/" + boardKey, boardValues);

        mRootRef.updateChildren(childUpdates);

        deleteBoard();
    }

    private void deleteBoard() {
        if (this.boardKey != null) {
            mRootRef.child("user_boards").child(boardEntry.getCreatorId()).child(boardKey).removeValue();
            deleted = true;
            finish();
        }
    }

    private void updateBoardEntryTag() {
        String tag = editTextTag.getText().toString().trim();
        if (tag.length() > 0)
            boardEntry.setTag(tag);
    }

}
