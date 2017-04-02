package com.example.dan_p.nonogrammaker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dan_p.nonogrammaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private EditText loginUserId;
    private EditText loginUserPassword;
    private Button loginButton;
    private Button continueButton;
    private LinearLayout linearLayoutLoggedIn;
    private LinearLayout linearLayoutLoggedOut;
    private ProgressDialog progressDialog;
    private TextView textViewRegisterOrLogin;
    private TextView textViewLogout;
    private TextView textViewLoginInfo;
    private TextView textViewTitle;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private boolean register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.progressDialog = new ProgressDialog(this);
        findMainActivityViewById();
        setOnClickListeners();


        this.firebaseAuth = FirebaseAuth.getInstance();
        this.authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    linearLayoutLoggedOut.setVisibility(View.GONE);
                    linearLayoutLoggedIn.setVisibility(View.VISIBLE);

                    String userInfo = firebaseUser.getEmail();
                    textViewLoginInfo.setText(String.format("Signed in as : %s", userInfo));

                } else {
                    linearLayoutLoggedOut.setVisibility(View.VISIBLE);
                    linearLayoutLoggedIn.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void loginWithEmailAndPassword() {
        final String email = loginUserId.getText().toString().trim().toLowerCase();
        final String password = loginUserPassword.getText().toString().trim();

        if (password.length() < 6)
            Toast.makeText(this, "The password has to be at least 6 digits long",
                    Toast.LENGTH_SHORT).show();
        else {
            progressDialog.setMessage("Signing in...");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.cancel();

                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail", task.getException());
                            }
                        }
                    });
        }
    }

    private void registerWithEmailAndPassword() {
        final String email = loginUserId.getText().toString().trim().toLowerCase();
        final String password = loginUserPassword.getText().toString().trim();

        if (password.length() < 6)
            Toast.makeText(this, "The password has to be at least 6 digits long",
                    Toast.LENGTH_SHORT).show();
        else {
            progressDialog.setMessage("Signing in...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.cancel();

                            Log.d(TAG, "createUserWithEmailAndPassword:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "createUserWithEmailAndPassword", task.getException());
                            }
                        }
                    });
        }

    }


    private void setOnClickListeners() {
        this.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register) {
                    registerWithEmailAndPassword();
                } else {
                    loginWithEmailAndPassword();
                }
            }
        });

        this.textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserPassword.setText("");
                firebaseAuth.signOut();
            }
        });

        textViewRegisterOrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register) {
                    register = false;
                    loginButton.setText("Login");
                    textViewTitle.setText("Login:");
                    textViewRegisterOrLogin.setText(R.string.register);
                } else {
                    register = true;
                    textViewTitle.setText("Register:");
                    loginButton.setText("Register");
                    textViewRegisterOrLogin.setText(R.string.login);
                }
            }
        });
    }

    private void findMainActivityViewById() {
        this.loginUserPassword = (EditText)findViewById(R.id.edittext_logic_password);
        this.loginUserId = (EditText)findViewById(R.id.edittext_login_username);
        this.loginButton = (Button)findViewById(R.id.login_ok_button);
        this.linearLayoutLoggedIn = (LinearLayout) findViewById(R.id.login_layout_logged_in);
        this.linearLayoutLoggedOut = (LinearLayout) findViewById(R.id.login_layout_logged_out);
        this.textViewLoginInfo = (TextView) findViewById(R.id.textview_login_info);
        this.textViewRegisterOrLogin = (TextView) findViewById(R.id.textview_register_or_login);
        this.textViewLogout = (TextView) findViewById(R.id.textView8_logout);
        this.textViewTitle = (TextView) findViewById(R.id.textView5_action_title);
        this.continueButton = (Button) findViewById(R.id.button_continue_to_menu);
    }
}