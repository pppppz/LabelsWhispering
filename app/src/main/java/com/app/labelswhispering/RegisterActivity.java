package com.app.labelswhispering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private TextView mErrorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.labelswhispering.R.layout.activity_register);

        mUsernameField = (EditText) findViewById(com.app.labelswhispering.R.id.register_username);
        mPasswordField = (EditText) findViewById(com.app.labelswhispering.R.id.register_password);
        mErrorField = (TextView) findViewById(com.app.labelswhispering.R.id.error_messages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_48; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.app.labelswhispering.R.menu.register, menu);
        return true;
    }

    public void register(final View v) {
        if (mUsernameField.getText().length() == 0 || mPasswordField.getText().length() == 0)
            return;

        v.setEnabled(false);
        ParseUser user = new ParseUser();
        user.setUsername(mUsernameField.getText().toString());
        user.setPassword(mPasswordField.getText().toString());


        mErrorField.setText("");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                    startActivity(intent);

                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    switch (e.getCode()) {

                        case ParseException.USERNAME_TAKEN:
                            mErrorField.setText(R.string.username_taken);
                            break;
                        case ParseException.USERNAME_MISSING:
                            mErrorField.setText(R.string.username_missing);
                            break;
                        case ParseException.PASSWORD_MISSING:
                            mErrorField.setText(R.string.password_missing);
                            break;
                        default:
                            mErrorField.setText(e.getLocalizedMessage());
                    }
                    v.setEnabled(true);
                }
            }
        });
    }

    public void showLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
