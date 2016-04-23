package com.app.labelswhispering.Class;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.app.labelswhispering.R;
import com.app.labelswhispering.model.Pictures;
import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.List;

public class DisplayImg_Activity extends AppCompatActivity {
    ImageView imageView;
    private String imgId;
    private String TAG = DisplayImg_Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        imageView = (ImageView) findViewById(R.id.image);
        imgId = getIntent().getStringExtra("imgId");
        loadImage();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void loadImage() {

        ParseQuery<Pictures> query = ParseQuery.getQuery(Pictures.class);
        query.whereEqualTo("objectId", imgId);
        query.findInBackground(new FindCallback<Pictures>() {
            @Override
            public void done(List<Pictures> items, ParseException error) {
                //check if list task have some data = clear
                if (error == null) {
                    if (items.size() != 0) {
                        ParseFile parseFile = items.get(0).getParseFile("file");
                        String url = parseFile.getUrl();
                        Glide.with(DisplayImg_Activity.this).load(url).into(imageView);
                    }
                } else {
                    Log.e(TAG, "ParseException : " + error);
                }
            }
        });
    }
}