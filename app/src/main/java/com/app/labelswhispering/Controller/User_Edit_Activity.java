package com.app.labelswhispering.Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.labelswhispering.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class User_Edit_Activity extends AppCompatActivity {

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
    private String name, email;
    private TextView tvName, tvEmail;
    LinearLayoutCompat.OnClickListener llNameLN = new LinearLayoutCompat.OnClickListener() {
        @Override
        public void onClick(View v) {
            askNewName(name);
        }
    };
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_activity);
        makeUI();
    }

    private void makeUI() {
        /** about toolbar **/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_edit);
        toolbar.setTitle(R.string.profile);
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
        LinearLayoutCompat llName = (LinearLayoutCompat) findViewById(R.id.llName_Edit);
        LinearLayoutCompat llPassword = (LinearLayoutCompat) findViewById(R.id.llPassword_Edit);
        LinearLayoutCompat llEmail = (LinearLayoutCompat) findViewById(R.id.llEmail_Edit);
        tvName = (TextView) findViewById(R.id.tvName_Edit);
        tvEmail = (TextView) findViewById(R.id.tvEmail_Edit);

        imgUser = (ImageView) findViewById(R.id.imgViewUserDetail);

        //set onclick for linear
        llName.setOnClickListener(llNameLN);
        llPassword.setOnClickListener(llPasswordLN);
        llEmail.setOnClickListener(llEmailLN);

    }

    public void askNewName(String name) {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setText(name);
        dDialog.setTitle(R.string.please_input_your_name);
        dDialog.setView(input);
        dDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                saveName(value);
            }
        });
        dDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }

    private void askNewEmail(String email) {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setText(email);
        dDialog.setTitle(R.string.please_input_your_email);
        dDialog.setView(input);
        dDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                saveEmail(value);
            }
        });
        dDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }

    private void askNewPassword() {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        dDialog.setTitle(R.string.please_input_new_password_message);
        dDialog.setView(input);
        dDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                savePassword(value);
            }
        });
        dDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
            byte[] bitmapdata = parseUser.getParseFile("profileThumb").getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            imgUser.setImageBitmap(bitmap);
            // Glide.with(this).load(url).into(mProfileImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        name = parseUser.getString("name");
        email = parseUser.getEmail();

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
                    Snackbar.make(rootLayout, R.string.your_name_has_changed, Snackbar.LENGTH_LONG).show();
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
                    Snackbar.make(rootLayout, R.string.password_has_changed, Snackbar.LENGTH_LONG).show();
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
                    Snackbar.make(rootLayout, R.string.email_has_changed, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(rootLayout, R.string.email_type_wrong_message, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }


}


