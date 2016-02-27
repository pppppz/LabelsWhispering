package com.app.labelswhispering;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {


    Button.OnClickListener FacebookOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, FacebookLoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    Button.OnClickListener TwitterOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(LoginActivity.this, TwitterLoginActivity.class);
            startActivity(intent);
            finish();

        }
    };
    private EditText UsernameField;
    private EditText PasswordField;
    private String Username;
    private String Password;
    private String TAG = LoginActivity.class.getSimpleName();
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UI();
    }

    private void UI() {

        UsernameField = (EditText) findViewById(R.id.login_username);
        PasswordField = (EditText) findViewById(R.id.login_password);

        Button mBtnFb = (Button) findViewById(R.id.facebook_login_button);
        mBtnFb.setOnClickListener(FacebookOnClick);

        Button mBtnTwitter = (Button) findViewById(R.id.twitter_login_button);
        mBtnTwitter.setOnClickListener(TwitterOnClick);

        linearLayout = (LinearLayout) findViewById(R.id.ll_login);
        progressBar = (ProgressBar) findViewById(R.id.pgbLogIn);

    }

    //click signIn for execute asyncTask login (in background)
    public void signIn(final View v) {
        getID();
        new loginTask().execute();

    }

    //click register button for go to intent register page
    public void toRegistration(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    //insert menu_48
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_48; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    private void getID() {
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                Username = String.valueOf(UsernameField.getText());
                Password = String.valueOf(PasswordField.getText());
            }
        }));
    }

    private class loginTask extends AsyncTask<Void, Integer, Void> {


        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... params) {
            ParseUser.logInInBackground(Username, Password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (user != null) {
                        Log.e(TAG, " switching to MainActivity");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        switch (e.getCode()) {
                            case ParseException.USERNAME_TAKEN:
                                Snackbar.make(linearLayout, R.string.username_already, Snackbar.LENGTH_LONG).show();
                                break;
                            case ParseException.USERNAME_MISSING:
                                Snackbar.make(linearLayout, R.string.user_missing, Snackbar.LENGTH_LONG).show();
                                break;
                            case ParseException.PASSWORD_MISSING:
                                Snackbar.make(linearLayout, R.string.password_missing, Snackbar.LENGTH_LONG).show();
                                break;
                            case ParseException.OBJECT_NOT_FOUND:
                                Snackbar.make(linearLayout, R.string.object_not_found, Snackbar.LENGTH_LONG).show();
                                break;
                            default:
                                Snackbar.make(linearLayout, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                e.printStackTrace();
                                break;

                        }

                    }
                }
            });

            return null;
        }

        protected void onProgressUpdate(Integer... values) {
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);

        }
    }


}
