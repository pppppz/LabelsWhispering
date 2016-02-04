package com.app.labelswhispering.DetailFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.labelswhispering.Adapter.PagerInMoreDetailAdapter;
import com.app.labelswhispering.Function.isNetworkConnected;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.Model.Pictures;
import com.app.labelswhispering.Model.Schedule;
import com.app.labelswhispering.R;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main_Medicine_Details_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {


    public static String medicineID;
    public static List<Medicine> medicineList = new ArrayList<>();
    public static CoordinatorLayout rootLayout;
    public static ProgressBar progressBar_Medicine_Detail;
    private static TextToSpeech tts;
    private String medicineName;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private String TAG = Main_Medicine_Details_Activity.class.getSimpleName();
    private ParseUser parseUser;
    private boolean hasAdded = false;
    private List<Pictures> pictureList = new ArrayList<>();
    private ImageView imgToolbar;
    private Menu mMenu;
    private TabLayout tabLayout;

    public static void speakOut(String message) {
        // If no text is typed, tts will read out 'You haven't typed text'
        // else it reads out the text you typed


        if (message.length() == 0) {
            tts.speak("You haven't typed text", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            if (!tts.isSpeaking()) {
                tts.speak(message, TextToSpeech.QUEUE_ADD, null);
                Snackbar.make(rootLayout, R.string.reading, Snackbar.LENGTH_LONG).show();
            } else {
                tts.stop();
                Snackbar.make(rootLayout, R.string.stop_reading, Snackbar.LENGTH_LONG).show();
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        GetMedicineID();
        parseUser = ParseUser.getCurrentUser();
        fragmentManager = getSupportFragmentManager();
        tts = new TextToSpeech(this, this);

        bindUI();


    }

    private void bindUI() {
        progressBar_Medicine_Detail = (ProgressBar) findViewById(R.id.progress_bar_medicine_detail);
        progressBar_Medicine_Detail.setVisibility(View.VISIBLE);

        //toolbar
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_medicine_detail);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout_detail);
        collapsingToolbarLayout.setTitle(medicineName);
        /** Setting toolbar */

        toolbar = (Toolbar) findViewById(R.id.toolbar_medicine_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //set tab
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_medicine_detail);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.property));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.how_to_take));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.if_forget));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.side_effect));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.how_to_keep));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.picture));
        imgToolbar = (ImageView) findViewById(R.id.imgViewMedicineDetail);


        checkQuery();
    }

    private void GetMedicineID() {
        Intent intent = getIntent();
        medicineID = intent.getStringExtra("medicineID");
        medicineName = intent.getStringExtra("medicineName");

    }

    private void setDataAndPager() {

        viewPager = (ViewPager) findViewById(R.id.pager_detail);

        final PagerInMoreDetailAdapter adapter = new PagerInMoreDetailAdapter(fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            String[] toolbar_items = getResources().getStringArray(R.array.toolbar_items_detail);

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                toolbar.setTitle(toolbar_items[position]);
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_48; this adds items to the action bar if it is present.

        mMenu = menu;
        getMenuInflater().inflate(R.menu.medicine_detail_menu, mMenu);
        return true;
    }

    private void checkQuery() {

        if (!new isNetworkConnected(this).CheckNow()) {
            indicateMedicineData_Offline(medicineID);
            checkIfMedicineHasAddedOffline();
            Snackbar.make(rootLayout, R.string.offline_mode, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        } else {
            checkIfMedicineHasAdded();
            indicateMedicineData(medicineID);
            loadImgToolbar();
        }


    }

    private void indicateMedicineData(final String medicineID) {
        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("objectId", medicineID);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                medicineList.clear();
                if (error == null) {
                    for (Medicine medicine : items) {
                        medicineList.add(medicine);
                    }
                    setDataAndPager();

                } else {
                    Log.e(TAG, "ParseException : " + String.valueOf(error));
                }
            }
        });
    }

    private void indicateMedicineData_Offline(final String medicineID) {
        Log.e(TAG, "indicate");

        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("objectId", medicineID);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                medicineList.clear();
                if (error == null) {
                    for (Medicine medicine : items) {
                        medicineList.add(medicine);
                    }
                    setDataAndPager();

                } else {
                    Log.e(TAG, "ParseException : " + String.valueOf(error));
                }
            }
        });
    }

    private void loadImgToolbar() {

        Log.e(TAG, "loadImgToolbar");
        ParseQuery<Pictures> query = ParseQuery.getQuery(Pictures.class);
        query.whereEqualTo("medicineId", medicineID);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Pictures>() {
            @Override
            public void done(List<Pictures> items, ParseException error) {
                //check if list task have some data = clear
                if (error == null) {
                    pictureList.clear();
                    if (items.size() != 0) {
                        for (Pictures pictures : items) {
                            pictureList.add(pictures);
                        }
                        byte[] bitmapdata;
                        try {
                            bitmapdata = pictureList.get(0).getFile().getData();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                            imgToolbar.setImageBitmap(bitmap);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e(TAG, "ParseException : " + error);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.action_fav:
                if (hasAdded) {
                    removeMedicineFromUserList();
                    item.setIcon(R.drawable.ic_favorite_border_pink_24dp);
                    hasAdded = false;
                } else {
                    item.setIcon(R.drawable.ic_favorite_pink_24dp);
                    addMedicineToUserList();
                    hasAdded = true;
                }
                break;
            case R.id.action_set_to_schedule:
                setToSchedule();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToSchedule() {
        try {
            /** get data from task_input and set data by method in Current.class **/
            Schedule s = new Schedule();
            s.setACL(new ParseACL(ParseUser.getCurrentUser()));
            s.setUser(ParseUser.getCurrentUser());
            s.setMedicineId(medicineID);
            s.setName(medicineList.get(0).getName());
            s.setAmount(medicineList.get(0).getAmount());
            s.setMorning(medicineList.get(0).isMorning());
            s.setNoon(medicineList.get(0).isAfterNoon());
            s.setEvening(medicineList.get(0).isEvening());
            s.setBedtime(medicineList.get(0).isBedtime());
            s.setBeforeMeal(medicineList.get(0).isBeforeMeal());
            s.pinInBackground();
            s.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Snackbar.make(rootLayout, R.string.added_to_schedule, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(rootLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        Log.e(TAG, "parse to string : " + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "setToSchedule : " + e);
        }

    }

    private void checkIfMedicineHasAdded() {
        try {
            Log.e(TAG, "CheckIfMedicineHasAdded");
            ParseRelation<Medicine> relation = parseUser.getRelation("savedMedicine");
            ParseQuery<Medicine> query = relation.getQuery();
            query.whereEqualTo("objectId", medicineID);
            // Run the query
            query.findInBackground(new FindCallback<Medicine>() {
                @Override
                public void done(List<Medicine> savedByList, ParseException e) {
                    if (e == null) {
                        // If there are results, update the list of posts
                        //medicineList.clear();
                        if (savedByList.size() == 1) {
                            hasAdded = true;
                            try {
                                mMenu.getItem(0).setIcon(R.drawable.ic_favorite_pink_24dp);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            ParseObject.pinAllInBackground(savedByList);
                        } else {
                            hasAdded = false;
                            Log.e(TAG, "don't have this medicine data in column of medicine list , so fav icon will showing border icon");
                        }
                    } else {
                        Log.e("Post retrieval", "Error: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "checkMedicineAdd : " + e.getLocalizedMessage());
        }

    }

    private void checkIfMedicineHasAddedOffline() {
        ParseRelation<Medicine> relation = parseUser.getRelation("savedMedicine");
        ParseQuery<Medicine> query = relation.getQuery();
        query.whereEqualTo("objectId", medicineID);
        query.fromLocalDatastore();
        // Run the query
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> savedByList, ParseException e) {
                if (e == null) {
                    medicineList.clear();
                    // If there are results, update the list of posts
                    if (savedByList.size() == 1) {
                        hasAdded = true;
                        mMenu.getItem(0).setIcon(R.drawable.ic_favorite_pink_24dp);
                        medicineList.add(savedByList.get(0));
                    } else {
                        hasAdded = false;
                        Log.e(TAG, "don't have this medicine data in column of medicine list , so fav icon will showing border icon");
                    }
                    progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);
                } else {
                    Log.e("Post retrieval", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void removeMedicineFromUserList() {
        // set icon to un favourite
        ParseObject medicine = ParseObject.createWithoutData("Medicine", medicineID);
        ParseRelation<ParseObject> relation = parseUser.getRelation("savedMedicine");
        relation.remove(medicine);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Snackbar.make(rootLayout, R.string.unsaved_medicine, Snackbar.LENGTH_LONG).show();
                    Log.e(TAG, "medicine has removed!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addMedicineToUserList() {
        ParseObject medicine = ParseObject.createWithoutData("Medicine", medicineID);
        ParseRelation<ParseObject> relation = parseUser.getRelation("savedMedicine");
        relation.add(medicine);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Snackbar.make(rootLayout, R.string.medicine_has_add, Snackbar.LENGTH_LONG).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(final int status) {
        new Thread(new Runnable() {
            public void run() {
                Log.e(TAG, "onInit");
                // TTS is successfully initialized
                if (status == TextToSpeech.SUCCESS) {

                    tts.setPitch(0.3f);
                    tts.setSpeechRate(1.0f);
                    // Setting speech language
                    Locale locale;
                 /*   String locale_lang = Locale.getDefault().getDisplayLanguage();
                    if (locale_lang.equals("th")) {
                        locale = new Locale("th", "TH");
                    } else {
                        locale = Locale.US;
                    }*/
                    locale = new Locale("th", "TH");
                    Locale.setDefault(locale);
                    int result = tts.setLanguage(locale);
                    // If your device doesn't support language you set above
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Cook simple toast message with message
                        Toast.makeText(getApplicationContext(), R.string.language_not_support, Toast.LENGTH_LONG).show();
                        Log.e("TTS", "Language is not supported");
                    }
                    // Enable the button - It was disabled in main.xml (Go back and
                    // Check it)
                    else {
                        // btnSpeak.setEnabled(true);
                        Log.e(TAG, "else onInit()");
                    }
                    // TTS is not initialized properly
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initilization Failed", Toast.LENGTH_LONG).show();
                    Log.e("TTS", "Initilization Failed");
                }
            }
        }).start();


    }
}
