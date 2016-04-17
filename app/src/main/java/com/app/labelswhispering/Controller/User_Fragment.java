package com.app.labelswhispering.Controller;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.labelswhispering.Controller.Preference.SettingsActivity;
import com.app.labelswhispering.Controller.Services.AlarmActivity;
import com.app.labelswhispering.Model.Adapter.UserMenuAdapter;
import com.app.labelswhispering.Model.Pictures;
import com.app.labelswhispering.Model.UserMenu;
import com.app.labelswhispering.OtherClass.Listener.RecyclerItemClickListener;
import com.app.labelswhispering.R;
import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Fragment extends Fragment {


    public LinearLayoutManager layoutManager;
    CircleImageView mProfileImage;
    TextView mUsername;
    ParseUser parseUser;
    private FragmentActivity fragmentActivity;
    RecyclerView.OnItemTouchListener recycleViewOnTouchListener = new RecyclerItemClickListener(fragmentActivity, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, final int position) {

            Intent intent;

            switch (position) {
                case 0:
                    intent = new Intent(fragmentActivity, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    //savePicture();
                    Toast.makeText(fragmentActivity, "case : save " + position, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    intent = new Intent(fragmentActivity, ReportActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(fragmentActivity, AlarmActivity.class);
                    startActivity(intent);
                    Toast.makeText(fragmentActivity, "case : about " + position, Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(fragmentActivity, User_Edit_Activity.class);
            startActivity(intent);
        }
    };
    private String TAG = User_Fragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_fragment, container, false);
        fragmentActivity = getActivity();
        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        LinearLayout layout_profile = (LinearLayout) view.findViewById(R.id.layoutprofile);
        layout_profile.setOnClickListener(profileListener);

        /**declare list view and set adapter to list view (UI)**/
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        /** Setting List **/
        List<UserMenu> listMenu = new ArrayList<>();
        UserMenu app_setting = new UserMenu(fragmentActivity, R.string.app_settings, R.drawable.ic_settings_black_18dp);
        UserMenu HelpCenter = new UserMenu(fragmentActivity, R.string.help_center, R.drawable.ic_help_outline_black_18dp);
        UserMenu Report = new UserMenu(fragmentActivity, R.string.report, R.drawable.ic_bug_report_black_18dp);
        UserMenu About = new UserMenu(fragmentActivity, R.string.about, R.drawable.ic_report_black_18dp);
        UserMenu Logout = new UserMenu(fragmentActivity, R.string.logout, R.drawable.ic_power_settings_new_black_18dp);
        listMenu.add(app_setting);
        listMenu.add(HelpCenter);
        listMenu.add(Report);
        listMenu.add(About);
        listMenu.add(Logout);

        /** create adapter for put on array from class task **/
        RecyclerView.Adapter mAdapter = new UserMenuAdapter(fragmentActivity, listMenu);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mUsername = (TextView) view.findViewById(R.id.txt_name);

        /**set list view can listen the event when click some row **/
        mRecyclerView.addOnItemTouchListener(recycleViewOnTouchListener);


        return view;
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
            Glide.with(fragmentActivity).load(url).into(mProfileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUsername.setText(parseUser.getString("name"));
    }

    private void savePicture() {
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
    }


    private void LogOut() {
        final AlertDialog.Builder dDialog = new AlertDialog.Builder(fragmentActivity);
        dDialog.setMessage(R.string.want_to_logout);
        dDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ParseUser.logOut();
                // show login screen
                Intent intent = new Intent(fragmentActivity, LoginActivity.class);
                startActivity(intent);
                fragmentActivity.finish();
            }
        });
        dDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        });
        dDialog.show();
    }


}
