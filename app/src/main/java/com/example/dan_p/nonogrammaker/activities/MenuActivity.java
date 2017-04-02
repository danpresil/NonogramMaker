package com.example.dan_p.nonogrammaker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dan_p.nonogrammaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String email;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView textViewId = (TextView) findViewById(R.id.textView_user_login_id);

        this.firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            this.email = firebaseUser.getEmail();
            this.uid = firebaseUser.getUid();
            textViewId.setText(String.format("Logged in as %s", email));
        }

        Button buttonPlay = (Button) findViewById(R.id.button_play);
        if (buttonPlay != null) {
            buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, PlayListActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            });
        }

        Button buttonCreate = (Button) findViewById(R.id.button_create);
        if (buttonCreate != null) {
            buttonCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, CreateListActivity.class);
                    startActivity(intent);
                }
            });
        }

        Button buttonProgress = (Button) findViewById(R.id.button_progression);
        buttonProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProgressActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }
}
