package com.app.labelswhispering;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import de.hdodenhof.circleimageview.CircleImageView;


public class User_Edit_Activity extends AppCompatActivity {

    private static final String TAG = User_Edit_Activity.class.getSimpleName();
    private LinearLayoutCompat llName, llPassword, llEmail;
    private CoordinatorLayout rootLayout;
    private ParseUser parseUser;
    LinearLayoutCompat.OnClickListener llPasswordLN = new LinearLayoutCompat.OnClickListener() {
        @Override
        public void onClick(View v) {
            askNewPassword();
        }
    };
    LinearLayoutCompat.OnClickListener llEmailLN = new LinearLayoutCompat.OnClickListener() {
        @Override
        public void onClick(View v) {
            askNewEmail(email);
        }
    };
    private CircleImageView mProfileImage;
    private String name, email, password;
    private TextView tvName, tvEmail;
    LinearLayoutCompat.OnClickListener llNameLN = new LinearLayoutCompat.OnClickListener() {
        @Override
        public void onClick(View v) {
            askNewName(name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_activity);
        makeUI();

    }

    private void makeUI() {
        /** about toolbar **/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_edit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //bind xml ui with java
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_user_edit);
        llName = (LinearLayoutCompat) findViewById(R.id.llName_Edit);
        llPassword = (LinearLayoutCompat) findViewById(R.id.llPassword_Edit);
        llEmail = (LinearLayoutCompat) findViewById(R.id.llEmail_Edit);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image_edit);
        tvName = (TextView) findViewById(R.id.tvName_Edit);
        tvEmail = (TextView) findViewById(R.id.tvEmail_Edit);

        //set onclick for linear
        llName.setOnClickListener(llNameLN);
        llPassword.setOnClickListener(llPasswordLN);
        llEmail.setOnClickListener(llEmailLN);

    }

    public void askNewName(String name) {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setText(name);
        dDialog.setTitle("Please input your name");
        dDialog.setView(input);
        dDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                saveName(value);
            }
        });
        dDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }

    private void askNewEmail(String email) {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setText(email);
        dDialog.setTitle("Please input your email");
        dDialog.setView(input);
        dDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                saveEmail(value);
            }
        });
        dDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }


    private void askNewPassword() {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        dDialog.setTitle("Please input your new passsowrd");
        dDialog.setView(input);
        dDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                savePassword(value);
            }
        });
        dDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetailsFromParse();
    }


    public void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();

        //Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            String url = parseFile.getUrl();
            Glide.with(this).load(url).into(mProfileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        name = parseUser.getString("name");
        email = parseUser.getEmail();
        password = parseUser.getString("password");

        tvName.setText(name);
        tvEmail.setText(email);
    }

    private void saveName(final String name) {
        parseUser.put("name", name);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    tvName.setText(name);
                    Snackbar.make(rootLayout, "Your name has changed.", Snackbar.LENGTH_LONG).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void savePassword(final String password) {
        parseUser.put("password", password);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Snackbar.make(rootLayout, "Password has changed.", Snackbar.LENGTH_LONG).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveEmail(final String email) {
        parseUser.put("email", email);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    tvEmail.setText(email);
                    Snackbar.make(rootLayout, "Email has changed.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(rootLayout, "Something was wrong ,Please check format of email again.", Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

}


