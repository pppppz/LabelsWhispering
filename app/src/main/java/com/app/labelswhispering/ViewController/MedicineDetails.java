package com.app.labelswhispering.viewcontroller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.labelswhispering.Class.DisplayImg_Activity;
import com.app.labelswhispering.Class.RowInDetails;
import com.app.labelswhispering.Class.isNetworkConnected;
import com.app.labelswhispering.Class.listener.RecyclerItemClickListener;
import com.app.labelswhispering.Class.listener.RecyclerViewOnScrollListener;
import com.app.labelswhispering.R;
import com.app.labelswhispering.model.Medicine;
import com.app.labelswhispering.model.Pictures;
import com.app.labelswhispering.model.Schedule;
import com.app.labelswhispering.model.adapter.PictureAdapter;
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


public class MedicineDetails extends AppCompatActivity implements TextToSpeech.OnInitListener {


    public static String medicineID;
    public static List<Medicine> medicineList = new ArrayList<>();
    public static CoordinatorLayout rootLayout;
    public static ProgressBar progressBar_Medicine_Detail;
    public static TextToSpeech tts;
    private final String TAG = MedicineDetails.class.getSimpleName();
    Button.OnClickListener buttonScheduleSetListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            setToSchedule();
        }
    };
    private ParseUser parseUser;
    private boolean hasAdded = false;
    Button.OnClickListener buttonFavListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (hasAdded) {
                removeMedicineFromUserList();
                buttonFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_border_24dp, 0, 0, 0);
                hasAdded = false;
            } else {
                buttonFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_24dp, 0, 0, 0);
                addMedicineToUserList();
                hasAdded = true;
            }

        }
    };
    private List<Pictures> pictureList = new ArrayList<>();
    private Button buttonSchedule, buttonFavourite;
    private ImageView imgToolbar;
    private TextView tvAmountPerTime;
    private LinearLayout mLinearLayoutProperty, mLinearLayoutIfForget, mLinearLayoutSideEffect, mLinearLayoutHowToKeep, mLinearLayoutHowToTake, mLinearLayoutTellDoctor, mLinearLayoutPicture;
    private LinearLayout mLinearLayoutHeaderProperty, mLinearLayoutHeaderIfForget, mLinearLayoutHeaderSideEffect, mLinearLayoutHeaderHowToKeep, mLinearLayoutHeaderHowToTake, mLinearLayoutHeaderTellDoctor, mLinearLayoutHeaderPicture;
    private TextView tvType;
    private CheckBox beforeMeal, afterMeal, Morning, Noon, Evening, Bedtime;
    private WebView webHowToTake, webHowToKeep, webIfForget, webTellDoctor, webGeneral, webBad, webProperty;
    private List<Pictures> picturesList = new ArrayList<>();
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            try {
                String imgId = picturesList.get(position).getObjectId();
                //Create intent
                Intent intent = new Intent(MedicineDetails.this, DisplayImg_Activity.class);
                intent.putExtra("imgId", imgId);
                //Start details activity
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(MedicineDetails.rootLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
            }

        }
    });
    private RecyclerView.Adapter mAdapter;
    private String medicineName;
    private ArrayAdapter<CharSequence> adapterType;

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

    private void checkLanguage() {
        Locale locale;
        String locale_lang = Locale.getDefault().getDisplayLanguage();
        Log.e(TAG, "Lang : " + locale_lang);
        if (locale_lang.equals("th") || locale_lang.equals(getString(R.string.thai))) {
            locale = new Locale("th", "TH");
            adapterType = ArrayAdapter.createFromResource(getBaseContext(), R.array.medicine_type_th, android.R.layout.simple_spinner_item);

        } else {
            locale = Locale.US;
            adapterType = ArrayAdapter.createFromResource(getBaseContext(), R.array.medicine_type, android.R.layout.simple_spinner_item);
        }
        Locale.setDefault(locale);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_detail);
        GetMedicineID();
        parseUser = ParseUser.getCurrentUser();
        tts = new TextToSpeech(this, this);
        checkLanguage();
        bindUI();
        checkQuery();


    }

    private void bindUI() {
        progressBar_Medicine_Detail = (ProgressBar) findViewById(R.id.progress_bar_medicine_detail);
        if (progressBar_Medicine_Detail != null) {
            progressBar_Medicine_Detail.setVisibility(View.VISIBLE);
        }


        //toolbar
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_medicine_detail);

        //collapsing toolbar
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout_detail);

        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(medicineName);
        }
        /** Setting toolbar */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_medicine_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imgToolbar = (ImageView) findViewById(R.id.imgViewMedicineDetail);

        webHowToTake = (WebView) findViewById(R.id.web_howToTake);
        webHowToKeep = (WebView) findViewById(R.id.web_howToKeep);
        webIfForget = (WebView) findViewById(R.id.web_ifForget);
        webProperty = (WebView) findViewById(R.id.web_Property);
        webGeneral = (WebView) findViewById(R.id.web_General_Symptom);
        webBad = (WebView) findViewById(R.id.web_Bad_Symptom);
        webTellDoctor = (WebView) findViewById(R.id.web_tellDoctor);

        afterMeal = (CheckBox) findViewById(R.id.cb_afterMeal_pf);
        beforeMeal = (CheckBox) findViewById(R.id.cb_beforeMeal_pf);
        Morning = (CheckBox) findViewById(R.id.cb_Morning_pf);
        Noon = (CheckBox) findViewById(R.id.cb_Noon_pf);
        Evening = (CheckBox) findViewById(R.id.cb_Evening_pf);
        Bedtime = (CheckBox) findViewById(R.id.cb_Bedtime_pf);

        buttonFavourite = (Button) findViewById(R.id.buttonFavourite);
        buttonSchedule = (Button) findViewById(R.id.buttonSchedule);
        buttonFavourite.setOnClickListener(buttonFavListener);
        buttonSchedule.setOnClickListener(buttonScheduleSetListener);

        tvType = (TextView) findViewById(R.id.tvType);
        tvAmountPerTime = (TextView) findViewById(R.id.tvAmountPerTime);

        //bind property
        mLinearLayoutProperty = (LinearLayout) findViewById(R.id.expandable_property);
        mLinearLayoutHeaderProperty = (LinearLayout) findViewById(R.id.header_property);
        RowInDetails rowProperty = new RowInDetails(mLinearLayoutHeaderProperty, mLinearLayoutProperty, 350);
        rowProperty.init();

        //bind how to take
        mLinearLayoutHeaderHowToTake = (LinearLayout) findViewById(R.id.header_howToTake);
        mLinearLayoutHowToTake = (LinearLayout) findViewById(R.id.expandable_howToTake);
        RowInDetails rowHowToTake = new RowInDetails(mLinearLayoutHeaderHowToTake, mLinearLayoutHowToTake, 600);
        rowHowToTake.init();

        //bind if forget
        mLinearLayoutHeaderIfForget = (LinearLayout) findViewById(R.id.header_ifforget);
        mLinearLayoutIfForget = (LinearLayout) findViewById(R.id.expandable_ifforget);
        RowInDetails rowIfForget = new RowInDetails(mLinearLayoutHeaderIfForget, mLinearLayoutIfForget, 300);
        rowIfForget.init();

        //bind tell doctor
        mLinearLayoutHeaderTellDoctor = (LinearLayout) findViewById(R.id.header_tellDoctor);
        mLinearLayoutTellDoctor = (LinearLayout) findViewById(R.id.expandable_tellDoctor);
        RowInDetails rowTellDoctor = new RowInDetails(mLinearLayoutHeaderTellDoctor, mLinearLayoutTellDoctor, 600);
        rowTellDoctor.init();

        //bind how to keep
        mLinearLayoutHeaderHowToKeep = (LinearLayout) findViewById(R.id.header_howToKeep);
        mLinearLayoutHowToKeep = (LinearLayout) findViewById(R.id.expandable_howToKeep);
        RowInDetails rowHowToKeep = new RowInDetails(mLinearLayoutHeaderHowToKeep, mLinearLayoutHowToKeep, 250);
        rowHowToKeep.init();

        //bind side effect
        mLinearLayoutHeaderSideEffect = (LinearLayout) findViewById(R.id.header_side_effect);
        mLinearLayoutSideEffect = (LinearLayout) findViewById(R.id.expandable_side_effect);
        RowInDetails rowSideEffect = new RowInDetails(mLinearLayoutHeaderSideEffect, mLinearLayoutSideEffect, 700);
        rowSideEffect.init();


        //bind picture
        mLinearLayoutHeaderPicture = (LinearLayout) findViewById(R.id.header_picture);
        mLinearLayoutPicture = (LinearLayout) findViewById(R.id.expandable_picture);
        RowInDetails rowPicture = new RowInDetails(mLinearLayoutHeaderPicture, mLinearLayoutPicture, 300);
        rowPicture.init();


        //bind data for picture
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_picture);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mLayoutManager);


        /** create adapter for put on array from class schedule **/
        mAdapter = new PictureAdapter(this, picturesList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);

        /**set list view can listen the event when click some row **/
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());

    }

    private void GetMedicineID() {
        Intent intent = getIntent();
        medicineID = intent.getStringExtra("medicineID");
        medicineName = intent.getStringExtra("medicineName");
    }

    private void setData() {

        afterMeal.setChecked(medicineList.get(0).isAfterMeal());
        beforeMeal.setChecked(medicineList.get(0).isBeforeMeal());
        Morning.setChecked(medicineList.get(0).isMorning());
        Noon.setChecked(medicineList.get(0).isAfterNoon());
        Evening.setChecked(medicineList.get(0).isEvening());
        Bedtime.setChecked(medicineList.get(0).isBedtime());
        tvType.setText(adapterType.getItem(medicineList.get(0).getType()));
        tvAmountPerTime.setText(String.valueOf(medicineList.get(0).getAmount()));

        final String htmlText = "<html><body style=\"text-align:justify\"> %s </body></Html>";
        webHowToTake.loadData(String.format(htmlText, medicineList.get(0).getHowTake()), "text/html; charset=utf-8", "UTF-8");
        webHowToKeep.loadData(String.format(htmlText, medicineList.get(0).getHowKeep()), "text/html; charset=utf-8", "UTF-8");
        webIfForget.loadData(String.format(htmlText, medicineList.get(0).getIfForget()), "text/html; charset=utf-8", "UTF-8");
        webGeneral.loadData(String.format(htmlText, medicineList.get(0).getGeneralSymptom()), "text/html; charset=utf-8", "UTF-8");
        webBad.loadData(String.format(htmlText, medicineList.get(0).getBadSymptom()), "text/html; charset=utf-8", "UTF-8");
        webTellDoctor.loadData(String.format(htmlText, medicineList.get(0).getTellDoctor()), "text/html; charset=utf-8", "UTF-8");
        webProperty.loadData(String.format(htmlText, medicineList.get(0).getUseFor()), "text/html; charset=utf-8", "UTF-8");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tts.stop();
        tts.shutdown();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_48; this adds items to the action bar if it is present.

        mMenu = menu;
        getMenuInflater().inflate(R.menu.medicine_detail_menu, mMenu);
        return true;
    }*/

    private void checkQuery() {

        if (!new isNetworkConnected().Check(this)) {
            indicateMedicineData_Offline(medicineID);
            checkIfMedicineHasAddedOffline();
            loadImg_Offline();
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
            loadImg();
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
                    setData();
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
                    setData();
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
/*
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
    }*/

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
                                //    mMenu.getItem(0).setIcon(R.drawable.ic_favorite_pink_24dp);
                                buttonFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_24dp, 0, 0, 0);
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
                        //mMenu.getItem(0).setIcon(R.drawable.ic_favorite_pink_24dp);
                        buttonFavourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_24dp, 0, 0, 0);
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
    public void onStop(){
        tts.stop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        //Thread.currentThread().interrupt();

    }

    @Override
    public void onInit(final int status) {
        new Thread(new Runnable() {
            public void run() {
                Log.e(TAG, "onInit");
                // TTS is successfully initialized
                if (status == TextToSpeech.SUCCESS) {

                    tts.setPitch(1.0f);
                    tts.setSpeechRate(1.0f);
                    // Setting speech language
                    Locale locale;
                    String locale_lang = Locale.getDefault().getDisplayLanguage();
                    if (locale_lang.equals("th") || locale_lang.equals(getResources().getString(R.string.thai))) {
                        locale = new Locale("th", "TH");
                    } else {
                        locale = Locale.US;
                    }
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

    public void sideEffectOnClick(View v) {
        speakOut(getString(R.string.general_symptom) + "  " + medicineList.get(0).getGeneralSymptom() + "   " + getString(R.string.bad_symptom) + "   " + medicineList.get(0).getBadSymptom());
    }

    public void howToKeepOnClick(View v) {
        speakOut(getString(R.string.how_to_keep) + medicineList.get(0).getHowKeep());
    }

    public void howToTakeOnClick(View v) {
        speakOut(getString(R.string.how_to_take) + medicineList.get(0).getHowTake());
    }

    public void ifForgetOnClick(View v) {
        speakOut(getString(R.string.if_forget) + medicineList.get(0).getIfForget());
    }

    public void tellDoctorOnClick(View v) {
        speakOut(getString(R.string.tell_doctor) + medicineList.get(0).getTellDoctor());
    }

    public void propertyOnClick(View v) {
        speakOut(getString(R.string.property) + medicineList.get(0).getUseFor());
    }

    private void loadImg() {
        ParseQuery<Pictures> query = ParseQuery.getQuery(Pictures.class);
        query.whereEqualTo("medicineId", MedicineDetails.medicineID);
        Log.e(TAG, "medicineId " + MedicineDetails.medicineID);
        query.findInBackground(new FindCallback<Pictures>() {
            @Override
            public void done(List<Pictures> items, ParseException error) {
                //check if list task have some data = clear
                Log.e(TAG, "query done");
                if (error == null) {
                    picturesList.clear();
                    picturesList.addAll(items);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "notify");
                } else {
                    Log.e(TAG, "ParseException : " + error);
                }
                Log.e(TAG, "end loop");
                progressBar_Medicine_Detail.setVisibility(View.GONE);
            }

        });
    }

    private void loadImg_Offline() {
        ParseQuery<Pictures> query = ParseQuery.getQuery(Pictures.class);
        query.whereEqualTo("medicineId", MedicineDetails.medicineID);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Pictures>() {
            @Override
            public void done(List<Pictures> items, ParseException error) {
                //check if list task have some data = clear
                Log.e(TAG, "query done");
                if (error == null) {
                    picturesList.clear();
                    picturesList.addAll(items);
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG, "notify");
                } else {
                    Log.e(TAG, "ParseException : " + error);
                }
                Log.e(TAG, "end loop");
                progressBar_Medicine_Detail.setVisibility(View.GONE);
            }

        });
    }






}
