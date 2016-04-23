package com.app.labelswhispering.viewcontroller;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.labelswhispering.Class.listener.RecyclerItemClickListener;
import com.app.labelswhispering.R;
import com.app.labelswhispering.model.UserMenu;
import com.app.labelswhispering.model.adapter.UserMenuAdapter;
import com.app.labelswhispering.viewcontroller.preference.SettingsActivity;
import com.app.labelswhispering.viewcontroller.services.AlarmScreenActivity;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {


    public LinearLayoutManager layoutManager;
    RecyclerView.OnItemTouchListener recycleViewOnTouchListener = new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, final int position) {

            Intent intent;

            switch (position) {
                case 0:
                    intent = new Intent(SettingActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    //savePicture();
                    Toast.makeText(SettingActivity.this, "case : save " + position, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    intent = new Intent(SettingActivity.this, ReportActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(SettingActivity.this, AlarmScreenActivity.class);
                    startActivity(intent);
                    Toast.makeText(SettingActivity.this, "case : about " + position, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    LogOut();
                    break;
            }
        }
    }
    );
    LinearLayout.OnClickListener profileListener = new LinearLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingActivity.this, UserEdit.class);
            startActivity(intent);
        }
    };
    private CircleImageView mProfileImage;
    private TextView mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_fragment);
        loadUI();
    }

    public void loadUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layoutprofile);
        layout_profile.setOnClickListener(profileListener);

        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        /** Setting List **/
        List<UserMenu> listMenu = new ArrayList<>();
        UserMenu app_setting = new UserMenu(this, R.string.app_settings, R.drawable.ic_settings_black_18dp);
        UserMenu HelpCenter = new UserMenu(this, R.string.help_center, R.drawable.ic_help_outline_black_18dp);
        UserMenu Report = new UserMenu(this, R.string.report, R.drawable.ic_bug_report_black_18dp);
        UserMenu About = new UserMenu(this, R.string.about, R.drawable.ic_report_black_18dp);
        UserMenu Logout = new UserMenu(this, R.string.logout, R.drawable.ic_power_settings_new_black_18dp);
        listMenu.add(app_setting);
        listMenu.add(HelpCenter);
        listMenu.add(Report);
        listMenu.add(About);
        listMenu.add(Logout);

        /** create adapter for put on array from class task **/
        RecyclerView.Adapter mAdapter = new UserMenuAdapter(this, listMenu);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mUsername = (TextView) findViewById(R.id.txt_name);

        /**set list view can listen the event when click some row **/
        mRecyclerView.addOnItemTouchListener(recycleViewOnTouchListener);


    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetailsFromParse();
    }

    public void getUserDetailsFromParse() {
        ParseUser parseUser = ParseUser.getCurrentUser();

        //Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            String url = parseFile.getUrl();
            Glide.with(this).load(url).into(mProfileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUsername.setText(parseUser.getString("name"));
    }

  /*  private void savePicture() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_favorite_pink_24dp);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        String thumbName = "paracetamol";
        ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

        //ParseObject pictures = new ParseObject("Pictures");
        ParseObject pictures = ParseObject.create(Pictures.class);
        pictures.put("medicineId", "8DivlP0L1M");
        pictures.put("file", parseFile);
        pictures.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //Finally save to the picture table
                if (e == null) {
                    Log.e(TAG, "picture had saved.");
                } else {
                    Log.e(TAG, e.toString());
                }

            }
        });
    }*/


    private void LogOut() {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);
        dDialog.setMessage(R.string.want_to_logout);
        dDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ParseUser.logOut();
                // show login screen
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
