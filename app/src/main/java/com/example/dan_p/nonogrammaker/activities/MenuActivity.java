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
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView textViewId = (TextView) findViewById(R.id.textView_user_login_id);

        this.firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            textViewId.setText(String.format("Logged in as %s", firebaseUser.getEmail()));
        }

        Button buttonNewGame = (Button) findViewById(R.id.button_board_list);
        if (buttonNewGame != null) {
            buttonNewGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, BoardPlayMenuActivity.class);
                    //TODO sent list
                    //Bundle bundle = new Bundle();
                    //intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        Button buttonToAllResults = (Button) findViewById(R.id.button_create_new_board);
        if (buttonToAllResults != null) {
            buttonToAllResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, BoardCreateMenuActivity.class);
//TODO send list
//                    Bundle bundle = new Bundle();
//                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

    }
}
